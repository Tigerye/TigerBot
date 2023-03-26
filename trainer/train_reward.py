from dataclasses import dataclass, field
from typing import Optional, Union, List, Dict, Any
import evaluate
import numpy as np
import torch
from datasets import load_dataset
from transformers import (
    AutoTokenizer,
    AutoModelForSequenceClassification,
    TrainingArguments,
    Trainer,
    PreTrainedTokenizerBase,
    HfArgumentParser,
)
from transformers.utils import PaddingStrategy
import os
os.environ["WANDB_DISABLED"] = "true"

tok_q = "\n\nHuman: "
tok_a = "\n\nAssistant: "

max_len = 512


# Define and parse arguments.
@dataclass
class ScriptArguments:
    """
    These arguments vary depending on how many GPUs you have, what their capacity and features are, and what size model you want to train.
    """

    local_rank: Optional[int] = field(default=0, metadata={"help": "Used for multi-gpu"})
    resume_from_checkpoint: Optional[bool] = field(
        default=False, metadata={"help": "If you want to resume training where it left off."}
    )
    deepspeed: Optional[str] = field(
        default=None,
        metadata={
            "help": "Path to deepspeed config if using deepspeed. You may need this if the model that you want to train doesn't fit on a single GPU."
        },
    )
    per_device_train_batch_size: Optional[int] = field(default=16)
    per_device_eval_batch_size: Optional[int] = field(default=16)
    gradient_accumulation_steps: Optional[int] = field(default=4)
    learning_rate: Optional[int] = field(default=2e-5)
    weight_decay: Optional[int] = field(default=0.001)
    model_name: Optional[str] = field(
        default="gpt2",
        metadata={
            "help": "The model that you want to train from the Hugging Face hub. E.g. gpt2, gpt2-xl, bert, etc."
        },
    )
    bf16: Optional[bool] = field(
        default=False,
        metadata={
            "help": "This essentially cuts the training time in half if you want to sacrifice a little precision and have a supported GPU."
        },
    )
    num_train_epochs: Optional[int] = field(
        default="5",
        metadata={
            "help": "The number of training epochs for the reward model. OpenAI used 5."
        }
    )
    train_file: Optional[str] = field(default=None, metadata={"help": "The training data file (a text/json file)."})
    eval_file: Optional[str] = field(default=None, metadata={"help": "The eval data file (a text/json file)."})
    gradient_checkpointing: Optional[bool] = field(default=False, metadata={"help": "The gradient checkpointing)."})
    

parser = HfArgumentParser(ScriptArguments)
script_args = parser.parse_args_into_dataclasses()[0]

# Load the human comparisons dataset for tuning the reward model.
# ds = load_dataset("openai/summarize_from_feedback", name="comparisons")
ds = load_dataset("json", data_files=script_args.train_file)
if script_args.eval_file:
    ds_eval = load_dataset("json", data_files=script_args.eval_file)
    ds["validation"] = ds_eval["train"]

# Define the training args. Needs to be done before the model is loaded if you are using deepspeed.
training_args = TrainingArguments(
    output_dir=f"{script_args.model_name}_reward",
    learning_rate=script_args.learning_rate,
    per_device_train_batch_size=script_args.per_device_train_batch_size,
    per_device_eval_batch_size=script_args.per_device_eval_batch_size,
    num_train_epochs=script_args.num_train_epochs,
    weight_decay=script_args.weight_decay,
    evaluation_strategy="epoch",
    save_strategy="epoch",
    gradient_accumulation_steps=script_args.gradient_accumulation_steps,
    gradient_checkpointing=script_args.gradient_checkpointing,
    deepspeed=script_args.deepspeed,
    local_rank=script_args.local_rank,
    remove_unused_columns=False,
    label_names=[],
)

# Load the value-head model and tokenizer.
tokenizer = AutoTokenizer.from_pretrained(script_args.model_name)
model = AutoModelForSequenceClassification.from_pretrained(script_args.model_name, num_labels=1)

# Need to do this for gpt2, because it doesn't have an official pad token.
tokenizer.pad_token = tokenizer.eos_token
model.config.pad_token_id = tokenizer.eos_token_id


# Turn the dataset into pairs of post + summaries, where text_j is the preferred post + summary and text_k is the other.
def turn_into_text_classification_format(examples):
    new_examples = {"text_j": [], "text_k": [], "count": []}
    for question, answers, choice, pair_count in zip(examples["question"], examples["answers"], examples["choice"], examples["pair_count"]):
        if len(answers) != 2 or choice not in (0, 1):
            raise ValueError(f"There should be two answers with a choice that's either 0 or 1. Received {len(answers)} summaries and choice={choice}.")
        if question.startswith(tok_q):
                chosen = question + " " + answers[choice]["text"]
                reject = question + " " + answers[0 if choice == 1 else 1]["text"]
        else:
                chosen = tok_q + question + tok_a + answers[choice]["text"]
                reject = tok_q + question + tok_a + answers[0 if choice == 1 else 1]["text"]
        
        new_examples["text_j"].append(
            chosen + tok_q
        )
        new_examples["text_k"].append(
            reject + tok_q
        )
        new_examples["count"].append(pair_count)
    return new_examples


num_proc = 8  # Can adjust to be higher if you have more processors. Should work even if you don't have 8 CPUs, though.
original_columns = ds["train"].column_names
ds = ds.map(turn_into_text_classification_format, batched=True, num_proc=num_proc, remove_columns=original_columns)


# Tokenize the dataset.
def preprocess_function(examples):
    tokenized_j = tokenizer(examples["text_j"], max_length=max_len, padding=True, truncation=True, return_tensors="pt")
    tokenized_k = tokenizer(examples["text_k"], max_length=max_len, padding=True, truncation=True, return_tensors="pt")
    return {
        "input_ids_j": tokenized_j["input_ids"],
        "attention_mask_j": tokenized_j["attention_mask"],
        "input_ids_k": tokenized_k["input_ids"],
        "attention_mask_k": tokenized_k["attention_mask"],
        "norm_count": examples["count"],
    }


original_columns = ds["train"].column_names
tokenized_ds = ds.map(preprocess_function, batched=True, num_proc=num_proc, remove_columns=original_columns)


# We need to define a special data collator that batches the data in our j vs k format.
@dataclass
class RewardDataCollatorWithPadding:
    tokenizer: PreTrainedTokenizerBase
    padding: Union[bool, str, PaddingStrategy] = True
    max_length: Optional[int] = None
    pad_to_multiple_of: Optional[int] = None
    return_tensors: str = "pt"

    def __call__(self, features: List[Dict[str, Any]]) -> Dict[str, Any]:
        features_j = []
        features_k = []
        features_c = []
        for feature in features:
            features_j.append({"input_ids": feature["input_ids_j"], "attention_mask": feature["attention_mask_j"]})
            features_k.append({"input_ids": feature["input_ids_k"], "attention_mask": feature["attention_mask_k"]})
            features_c.append(feature["norm_count"])
        batch_j = self.tokenizer.pad(
            features_j,
            padding=self.padding,
            max_length=self.max_length,
            pad_to_multiple_of=self.pad_to_multiple_of,
            return_tensors=self.return_tensors,
        )
        batch_k = self.tokenizer.pad(
            features_k,
            padding=self.padding,
            max_length=self.max_length,
            pad_to_multiple_of=self.pad_to_multiple_of,
            return_tensors=self.return_tensors,
        )
        batch = {
            "input_ids_j": batch_j["input_ids"],
            "attention_mask_j": batch_j["attention_mask"],
            "input_ids_k": batch_k["input_ids"],
            "attention_mask_k": batch_k["attention_mask"],
            "norm_count": features_c,
            "return_loss": True,
        }
        return batch


# Define the metric that we'll use for validation.
accuracy = evaluate.load("accuracy")


def compute_metrics(eval_pred):
    predictions, _ = eval_pred
    # Here, predictions is rewards_j and rewards_k.
    # We want to see how much of the time rewards_j > rewards_k.
    predictions = np.argmax(predictions, axis=0)
    labels = np.zeros(predictions.shape)
    return accuracy.compute(predictions=predictions, references=labels)


class RewardTrainer(Trainer):

    # Define how to compute the reward loss.
    def compute_loss(self, model, inputs, return_outputs=False):
        rewards_j = model(input_ids=inputs["input_ids_j"], attention_mask=inputs["attention_mask_j"])[0]
        rewards_k = model(input_ids=inputs["input_ids_k"], attention_mask=inputs["attention_mask_k"])[0]
        # loss = -torch.nn.functional.logsigmoid(rewards_j - rewards_k).mean()
        losses = -torch.nn.functional.logsigmoid(rewards_j - rewards_k)
        loss = (losses / torch.tensor(inputs["norm_count"]).to(losses.get_device())).mean()
        # loss = loss / torch.tensor(inputs["norm_count"]).to(loss.get_device())
        if return_outputs:
            return loss, {"rewards_j": rewards_j, "rewards_k": rewards_k}
        return loss


# Train the model, woohoo.
trainer = RewardTrainer(
    model=model,
    args=training_args,
    train_dataset=tokenized_ds["train"],
    eval_dataset=tokenized_ds["validation"],
    compute_metrics=compute_metrics,
    data_collator=RewardDataCollatorWithPadding(tokenizer=tokenizer, max_length=max_len),
)

trainer.train(script_args.resume_from_checkpoint)

trainer.save_model()

# Push to the hub so you can share it with people :D
# model.push_to_hub(script_args.model_name)
# tokenizer.push_to_hub(script_args.model_name)
