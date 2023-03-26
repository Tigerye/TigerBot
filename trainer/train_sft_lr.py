#    Copyright 2023 Rohan Taori, Ishaan Gulrajani, Tianyi Zhang, Yann Dubois, Xuechen Li
#
#    Licensed under the Apache License, Version 2.0 (the "License");
#    you may not use this file except in compliance with the License.
#    You may obtain a copy of the License at
#
#        http://www.apache.org/licenses/LICENSE-2.0
#
#    Unless required by applicable law or agreed to in writing, software
#    distributed under the License is distributed on an "AS IS" BASIS,
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#    See the License for the specific language governing permissions and
#    limitations under the License.

import copy
import logging
from dataclasses import dataclass, field
from typing import Optional, Dict, Sequence, List
import random
import gc
import torch
import transformers
from torch.utils.data import Dataset
from transformers import Trainer
from datasets import load_dataset
import torch.nn.functional as F

import utils
import math
import os
from _pickle import load
os.environ["WANDB_DISABLED"] = "true"

tok_ins = "\n\n### Instruction:\n"
tok_res = "\n\n### Response:\n"

src_max_ratio = 0.9
src_min_ratio = 0.1

IGNORE_INDEX = -100
DEFAULT_PAD_TOKEN = "[PAD]"
DEFAULT_EOS_TOKEN = "</s>"
DEFAULT_BOS_TOKEN = "</s>"
DEFAULT_UNK_TOKEN = "</s>"


def prompt_input(input: str):
    return tok_ins + input + tok_res


@dataclass
class ModelArguments:
    model_name_or_path: Optional[str] = field(default="facebook/opt-125m")


@dataclass
class DataArguments:
    data_path: str = field(default=None, metadata={"help": "Path to the training data."})


@dataclass
class TrainingArguments(transformers.TrainingArguments):
    cache_dir: Optional[str] = field(default=None)
    optim: str = field(default="adamw_torch")
    model_max_length: int = field(
        default=512,
        metadata={"help": "Maximum sequence length. Sequences will be right padded (and possibly truncated)."},
    )

    
def smart_tokenizer_and_embedding_resize(
    special_tokens_dict: Dict,
    tokenizer: transformers.PreTrainedTokenizer,
    model: transformers.PreTrainedModel,
):
    num_new_tokens = tokenizer.add_special_tokens(special_tokens_dict)
    model.resize_token_embeddings(len(tokenizer))
    if num_new_tokens > 0:
        input_embeddings = model.get_input_embeddings().weight.data
        output_embeddings = model.get_output_embeddings().weight.data
        input_embeddings_avg = input_embeddings[:-num_new_tokens].mean(dim=0, keepdim=True)
        output_embeddings_avg = output_embeddings[:-num_new_tokens].mean(dim=0, keepdim=True)
        input_embeddings[-num_new_tokens:] = input_embeddings_avg
        output_embeddings[-num_new_tokens:] = output_embeddings_avg


def safe_save_model_for_hf_trainer(trainer: transformers.Trainer, output_dir: str):
    """Collects the state dict and dump to disk."""
    state_dict = trainer.model.state_dict()
    if trainer.args.should_save:
        cpu_state_dict = {key: value.cpu() for key, value in state_dict.items()}
        del state_dict
        trainer._save(output_dir, state_dict=cpu_state_dict)  # noqa


def _tokenize_fn(strings: List[str], tokenizer: transformers.PreTrainedTokenizer) -> Dict:
    """Tokenize a list of strings."""
    tokenized = tokenizer(
            strings,
            return_tensors="pt",
            padding="longest",
            # padding=True,
            max_length=tokenizer.model_max_length,
            truncation=True,
        )
    input_ids = tokenized["input_ids"]
    input_ids_lens = input_ids.ne(tokenizer.pad_token_id).sum(-1)
    return dict(
        input_ids=input_ids,
        input_ids_lens=input_ids_lens,
    )


@dataclass
class DataCollatorForSupervisedDataset(object):
    """Collate examples for supervised fine-tuning."""

    tokenizer: transformers.PreTrainedTokenizer

    def __call__(self, instances: Sequence[Dict]) -> Dict[str, torch.Tensor]:
        input_ids, labels = tuple([instance[key] for instance in instances] for key in ("input_ids", "labels"))
        input_ids = torch.nn.utils.rnn.pad_sequence(
            torch.tensor(input_ids), batch_first=True, padding_value=self.tokenizer.pad_token_id
        )
        labels = torch.nn.utils.rnn.pad_sequence(torch.tensor(labels), batch_first=True, padding_value=IGNORE_INDEX)
        return dict(
            input_ids=input_ids,
            labels=labels,
            attention_mask=input_ids.ne(self.tokenizer.pad_token_id),
        )

    
def train():
    parser = transformers.HfArgumentParser((ModelArguments, DataArguments, TrainingArguments))
    model_args, data_args, training_args = parser.parse_args_into_dataclasses()

    model = transformers.AutoModelForCausalLM.from_pretrained(
        model_args.model_name_or_path,
        cache_dir=training_args.cache_dir,
    )

    if "llama" in model_args.model_name_or_path:
        tokenizer = transformers.LlamaTokenizer.from_pretrained(
            model_args.model_name_or_path,
            cache_dir=training_args.cache_dir,
            model_max_length=training_args.model_max_length,
            padding_side="right",
            trunction_side="right",
            use_fast=False,
            )
    else: 
        tokenizer = transformers.AutoTokenizer.from_pretrained(
            model_args.model_name_or_path,
            cache_dir=training_args.cache_dir,
            model_max_length=training_args.model_max_length,
            padding_side="right",
            trunction_side="right",
            use_fast=False,
            )
    
    if tokenizer.model_max_length is None or tokenizer.model_max_length > training_args.model_max_length:
        tokenizer.model_max_length = training_args.model_max_length

    special_tokens_dict = {}
    if tokenizer.pad_token_id is None:
        special_tokens_dict = dict(pad_token=DEFAULT_PAD_TOKEN)
    if "llama" in model_args.model_name_or_path:
        special_tokens_dict["eos_token"] = DEFAULT_EOS_TOKEN
        special_tokens_dict["bos_token"] = DEFAULT_BOS_TOKEN
        special_tokens_dict["unk_token"] = DEFAULT_UNK_TOKEN
    special_tokens_dict['additional_special_tokens'] = [tok_ins, tok_res]
    smart_tokenizer_and_embedding_resize(
            special_tokens_dict=special_tokens_dict,
            tokenizer=tokenizer,
            model=model,
        )
        
    tokenizer_src = copy.deepcopy(tokenizer)
    tokenizer_src.padding_side = 'left'
    tokenizer_src.truncation_side = 'right'
    tokenizer_tgt = copy.deepcopy(tokenizer)
    tokenizer_tgt.padding_side = 'right'
    tokenizer_tgt.truncation_side = 'right'
    
    def process_supervised(examples):
        logging.warning("Processing supervised data...")
        sources = [prompt_input(example) for example in examples["instruction"]]
        targets = [f"{example}{tokenizer_src.eos_token}" for example in examples["output"]]
        sources_tokenized, targets_tokenized = [_tokenize_fn(sources, tokenizer_src), _tokenize_fn(targets, tokenizer_tgt)]
        del sources
        del targets
        gc.collect()
        sources_input_ids = sources_tokenized["input_ids"]
        targets_input_ids = targets_tokenized["input_ids"]
        sources_len = sources_tokenized["input_ids_lens"].unsqueeze(1)
        targets_len = targets_tokenized["input_ids_lens"].unsqueeze(1)
        del sources_tokenized
        del targets_tokenized
        gc.collect()
        max_len = tokenizer_src.model_max_length
        bs = sources_input_ids.shape[0]
        s_len = sources_input_ids.shape[1]
        t_len = targets_input_ids.shape[1]
        trunc = False
        if s_len + t_len > max_len:
            trunc = True
            input_ids = torch.zeros(bs, max_len, dtype=torch.int)
            for s, t, sl, tl, m in zip(sources_input_ids, targets_input_ids, sources_len, targets_len, input_ids):
                sr = min(sl / (sl + tl), src_max_ratio)
                sr = max(sr, src_min_ratio)
                sl[:] = math.floor(max_len * sr)
                tl[:] = max_len - sl
                s_pad_pos = s.ne(tokenizer_src.pad_token_id).to(dtype=torch.int).argmax()
                s = s[s_pad_pos:sl + s_pad_pos]
                if sl > s.shape[0]:
                    pad_len = sl.item() - s.shape[0]
                    s = F.pad(s, (pad_len, 0), 'constant', tokenizer.pad_token_id)
                t = t[:tl]
                if tl > t.shape[0]:
                    pad_len = tl.item() - t.shape[0]
                    t = F.pad(t, (0, pad_len), 'constant', tokenizer.pad_token_id)
                m[:] = torch.cat([s, t])
        else:
            input_ids = torch.cat([sources_input_ids, targets_input_ids], dim=1)
        del sources_input_ids
        del targets_input_ids
        gc.collect()
        labels = copy.deepcopy(input_ids)
        res_pos = input_ids.eq(tokenizer_src.convert_tokens_to_ids(tok_res)).to(dtype=torch.int).argmax(1) + 1
        pad_pos = input_ids.ne(tokenizer_src.pad_token_id).to(dtype=torch.int).argmax(1)
        for lb, rs, sl, pd in zip(labels, res_pos, sources_len, pad_pos):
            if trunc:
                lb[:min(rs, sl)] = IGNORE_INDEX
            else:
                lb[:min(rs, sl + pd) ] = IGNORE_INDEX
        target_pad_pos = labels.eq(tokenizer_src.pad_token_id).to(dtype=torch.int).argmax(1)
        for lb, pd in zip(labels, target_pad_pos):
            if pd != 0:
                lb[pd:] = IGNORE_INDEX
        return dict(input_ids=input_ids.to(dtype=torch.int), labels=labels.to(dtype=torch.int))
    
    raw_dataset = load_dataset("json", data_files=data_args.data_path)["train"]
    # shuffle
    # raw_dataset = raw_dataset.shuffle()
    column_names = raw_dataset.column_names    
    supervised_dataset = raw_dataset.map(
            process_supervised,
            batched=True,
            # batch_size=1,
            num_proc=8,
            remove_columns=column_names,
            desc="Processing supervised data",
        )
    data_collator = DataCollatorForSupervisedDataset(tokenizer=tokenizer)

    trainer = Trainer(model=model, tokenizer=tokenizer, args=training_args, train_dataset=supervised_dataset, data_collator=data_collator)     
    trainer.train()
    trainer.save_state()
    safe_save_model_for_hf_trainer(trainer=trainer, output_dir=training_args.output_dir)


if __name__ == "__main__":
    train()
