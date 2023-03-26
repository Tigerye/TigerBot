# coding=utf-8
# Copyright 2022 The HuggingFace Inc. team. All rights reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
import torch
from datasets import load_dataset
from tqdm import tqdm
from transformers import pipeline, AutoTokenizer, HfArgumentParser
from typing import Optional
from dataclasses import dataclass, field
from trl import PPOTrainer, PPOConfig, AutoModelForCausalLMWithValueHead, set_seed
from trl.core import LengthSampler
tqdm.pandas()

########################################################################
# This is a fully working simple example to use trl with accelerate.
#
# This example fine-tunes a GPT2 model on the IMDB dataset using PPO
# (proximal policy optimization).
# in any of the following settings (with the same script):
#   - single CPU or single GPU
#   - multi GPUS (using PyTorch distributed mode)
#   - multi GPUS (using DeepSpeed ZeRO-Offload stages 1 & 2)
#   - fp16 (mixed-precision) or fp32 (normal precision)
#
# To run it in each of these various modes, first initialize the accelerate
# configuration with `accelerate config`
#
########################################################################
tok_q = "\n\nHuman: "
tok_a = "\n\nAssistant: "


# Define and parse arguments.
@dataclass
class ScriptArguments:
    """
    These arguments vary depending on how many GPUs you have, what their capacity and features are, and what size model you want to train.
    """

    local_rank: Optional[int] = field(default=0, metadata={"help": "Used for multi-gpu"})
    model_name: Optional[str] = field(
        default="gpt2",
        metadata={
            "help": "The model that you want to train from the Hugging Face hub. E.g. gpt2, gpt2-xl, bert, etc."
        },
    )
    reward_name: Optional[str] = field(
        default=None,
        metadata={
            "help": "The model that you want to train from the Hugging Face hub. E.g. gpt2, gpt2-xl, bert, etc."
        },
    )
    train_file: Optional[str] = field(default=None, metadata={"help": "The input training data file (a text/json file)."})
    output_dir: Optional[str] = field(default=None, metadata={"help": "The input training data file (a text/json file)."})
    max_question_length: Optional[int] = field(
        default="200",
        metadata={
            "help": "The max length of question to be trained."
        }
    )
    batch_size: Optional[int] = field(default="256", metadata={"help": "The input training batch sieze."})
    mini_batch_size: Optional[int] = field(default="16", metadata={"help": "The input training batch sieze."})
    learning_rate: Optional[float] = field(default=1.41e-5, metadata={"help": "The learning rate."})
    lr_scheduler: Optional[str] = field(default=None, metadata={"help": "The lr scheduler)."})
    adap_kl_ctrl: Optional[bool] = field(default=True, metadata={"help": "Whether adaptive kl control)."})
    init_kl_coef: Optional[float] = field(default=0.02, metadata={"help": "The KL beta)."})


parser = HfArgumentParser(ScriptArguments)
script_args = parser.parse_args_into_dataclasses()[0]

# We first define the configuration of the experiment, defining the model, the dataset,
# the training parameters, and the PPO parameters.
# Check the default arguments in the `PPOConfig` class for more details.
# If you want to log with tensorboard, add the kwarg
# `accelerator_kwargs={"logging_dir": PATH_TO_LOGS}` to the PPOConfig.
config = PPOConfig(
    model_name=script_args.model_name,
    learning_rate=script_args.learning_rate,
    batch_size=script_args.batch_size,
    mini_batch_size=script_args.mini_batch_size,
    adap_kl_ctrl=script_args.adap_kl_ctrl,
    init_kl_coef=script_args.init_kl_coef,
)


def tag_query(query):
    return tok_q + query + tok_a


def untag_query(query):
    return query[len(tok_q):len(query) - len(tok_a)]


def clean_response(response):
    return response.replace(tok_q, '').replace(tok_a, '').strip()


# Below is an example function to build the dataset. In our case, we use the IMDB dataset
# from the `datasets` library. One should customize this function to train the model on
# its own dataset.
def build_dataset():
    """
    Build dataset for training. This builds the dataset from `load_dataset`, one should
    customize this function to train the model on its own dataset.

    Args:
        dataset_name (`str`):
            The name of the dataset to be loaded.

    Returns:
        dataloader (`torch.utils.data.DataLoader`):
            The dataloader for the dataset.
    """
    tokenizer = AutoTokenizer.from_pretrained(script_args.model_name)
    tokenizer.pad_token = tokenizer.eos_token
    ds = load_dataset('json', data_files=script_args.train_file)["train"]
    ds = ds.filter(lambda x: len(x["question"]) <= min(tokenizer.model_max_length, script_args.max_question_length), batched=False)

    def tokenize(sample):
        sample["query"] = tag_query(sample["question"])
        sample["input_ids"] = tokenizer.encode(sample["query"])
        return sample

    num_proc = 8  # Can adjust to be higher if you have more processors. Should work even if you don't have 8 CPUs, though.
    original_columns = ds.column_names
    ds = ds.map(tokenize, batched=False, num_proc=num_proc, remove_columns=original_columns)
    ds.set_format(type="torch")
    return ds

    
# We retrieve the dataloader by calling the `build_dataset` function.
dataset = build_dataset()
# data_length = len(dataset)
# if data_length >= script_args.batch_size:
#     data_length = (data_length // script_args.batch_size) * script_args.batch_size
# dataset = dataset.select(range(data_length))


def collator(data):
    return dict((key, [d[key] for d in data]) for key in data[0])


# set seed before initializing value head for deterministic eval
set_seed(config.seed)

# Now let's build the model, the reference model, and the tokenizer.
model = AutoModelForCausalLMWithValueHead.from_pretrained(script_args.model_name)
ref_model = AutoModelForCausalLMWithValueHead.from_pretrained(script_args.model_name)
tokenizer = AutoTokenizer.from_pretrained(script_args.model_name)
tokenizer.pad_token = tokenizer.eos_token

# optimizer = torch.optim.Adam(model.parameters(), lr=config.learning_rate)
# if script_args.lr_scheduler == 'cosine':
#     lr_scheduler = torch.optim.lr_scheduler.CosineAnnealingLR(optimizer, T_max=256000, eta_min=0.1 * config.learning_rate, last_epoch=-1, verbose=False)

# We then build the PPOTrainer, passing the model, the reference model, the tokenizer
# ppo_trainer = PPOTrainer(config, model, ref_model, tokenizer, dataset=dataset, data_collator=collator, lr_scheduler=lr_scheduler)
ppo_trainer = PPOTrainer(config, model, ref_model, tokenizer, dataset=dataset, data_collator=collator)

# We then build the sentiment analysis pipeline, passing the model name and the
# sentiment analysis pipeline arguments. Let's also make sure to set the device
# to the same device as the PPOTrainer.
device = ppo_trainer.accelerator.device
if ppo_trainer.accelerator.num_processes == 1:
    device = 0 if torch.cuda.is_available() else "cpu"  # to avoid a `pipeline` bug
    
reward_kwargs = {"function_to_apply": "none", "batch_size": config.mini_batch_size}
reward_pipe = pipeline("text-classification", model=script_args.reward_name, tokenizer=tokenizer, device=device)

# We then define the arguments to pass to the `generate` function. These arguments
# are passed to the `generate` function of the PPOTrainer, which is a wrapper around
# the `generate` function of the trained model.

generation_kwargs = {
    "penalty_alpha": 0.6,
    "top_k": 4,
    "max_length": 256,
    "eos_token_id":tokenizer.convert_tokens_to_ids(tok_q),
    "pad_token_id": tokenizer.eos_token_id,
}

min_response_len = 4
for epoch, batch in tqdm(enumerate(ppo_trainer.dataloader)):
    query_tensors = batch["input_ids"]

    #### Get response from gpt2
    response_tensors = []
    for query in query_tensors:
        query_len = len(query)
        response = ppo_trainer.generate(query, **generation_kwargs)
        response_len = len(response.squeeze())
        response_pos = max(response_len - query_len, min_response_len)
        response_tensors.append(response.squeeze()[-response_pos:])
    batch["response"] = [clean_response(tokenizer.decode(r.squeeze(), skip_special_tokens=False, spaces_between_special_tokens=False)) for r in response_tensors]

    #### Compute sentiment score
    texts = [q + r + tok_q for q, r in zip(batch["query"], batch["response"])]
    pipe_outputs = reward_pipe(texts, **reward_kwargs)
    rewards = [torch.tensor(output["score"]).to(device) for output in pipe_outputs]

    #### Run PPO step
    stats = ppo_trainer.step(query_tensors, response_tensors, rewards)
    ppo_trainer.log_stats(stats, batch, rewards)
    
    print(f"done epoch {epoch} with a batch size of {len(batch['input_ids'])}")

model.save_pretrained(script_args.output_dir)
tokenizer.save_pretrained(script_args.output_dir)
print("done training.")
