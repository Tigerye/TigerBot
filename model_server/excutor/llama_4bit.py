import torch
import torch.nn as nn

from gptq import *
from modelutils import *
from quant import *

from transformers import AutoTokenizer
from typing import Optional, Dict
import torch
from docarray import DocumentArray, Document
from jina import Executor, requests
import gc
from multiprocessing import set_start_method

try:
    set_start_method('spawn')
except RuntimeError:
    pass

DEV = torch.device('cuda:0')


def get_llama(model):
    import torch
    def skip(*args, **kwargs):
        pass

    torch.nn.init.kaiming_uniform_ = skip
    torch.nn.init.uniform_ = skip
    torch.nn.init.normal_ = skip
    from transformers import LlamaForCausalLM
    model = LlamaForCausalLM.from_pretrained(model, torch_dtype='auto')
    model.seqlen = 2048
    return model


def load_quant(model, checkpoint, wbits, groupsize):
    from transformers import LlamaConfig, LlamaForCausalLM
    config = LlamaConfig.from_pretrained(model)

    def noop(*args, **kwargs):
        pass

    torch.nn.init.kaiming_uniform_ = noop
    torch.nn.init.uniform_ = noop
    torch.nn.init.normal_ = noop

    torch.set_default_dtype(torch.half)
    transformers.modeling_utils._init_weights = False
    torch.set_default_dtype(torch.half)
    model = LlamaForCausalLM(config)
    torch.set_default_dtype(torch.float)
    model = model.eval()
    layers = find_layers(model)
    for name in ['lm_head']:
        if name in layers:
            del layers[name]
    make_quant(model, layers, wbits, groupsize)

    print('Loading model ...')
    if checkpoint.endswith('.safetensors'):
        from safetensors.torch import load_file as safe_load
        model.load_state_dict(safe_load(checkpoint))
    else:
        model.load_state_dict(torch.load(checkpoint))
    model.seqlen = 2048
    print('Done.')

    return model


class Llama4bit(Executor):
    def __init__(self,
                 model: str,
                 load: str,
                 wbits: int = 4,
                 groupsize: int = -1,
                 device: str = "cuda",
                 max_input_length: int = 512,
                 max_generate_length: int = 1024,
                 **kwargs):
        super().__init__(**kwargs)
        self.model = load_quant(model, load, wbits, groupsize)
        self.device = torch.device(device)
        self.model.to(self.device)
        self.tokenizer = AutoTokenizer.from_pretrained(model, padding_side="left", truncation_side='left')
        self.max_input_length = max_input_length
        self.max_generate_length = max_generate_length
        self.generation_kwargs = {
            "top_p": 0.95,
            "temperature": 0.8,
            "max_length": max_generate_length,
            "eos_token_id": self.tokenizer.eos_token_id,
            "pad_token_id": self.tokenizer.pad_token_id,
            "early_stopping": True,
            "no_repeat_ngram_size": 4,
        }

    @requests
    def generate(self, docs, **kwargs) -> DocumentArray:
        tok_ins = "\n\n### Instruction:\n"
        tok_res = "\n\n### Response:\n"
        prompt_input = tok_ins + "{instruction}" + tok_res

        da = DocumentArray()
        for doc in docs:
            sess_text = ""
            if doc.tags["session"]:
                for s in doc.tags["session"]:
                    sess_text += tok_ins + s["human"] + tok_res + s["assistant"]
            print(f"session text: {sess_text}")
            sess_text += tok_ins + doc.text
            print("=" * 100)
            input_text = prompt_input.format_map({'instruction': sess_text.split(tok_ins, 1)[1]})
            print(f"input text: {input_text}")
            print("=" * 100)
            input_ids = self.tokenizer.encode(input_text, return_tensors="pt", truncation=True,
                                              max_length=self.max_input_length).to(self.device)
            input_length = input_ids.shape[1]
            with torch.no_grad():
                generated_ids = self.model.generate(
                    input_ids,
                    **self.generation_kwargs
                )
                gc.collect()
                torch.cuda.empty_cache()

            result = self.tokenizer.decode([el.item() for el in generated_ids[0][input_length:]],
                                           skip_special_tokens=True,
                                           spaces_between_special_tokens=False)
            answer = result.rstrip(self.tokenizer.eos_token)
            sess_text += tok_res + answer
            print("=" * 100)
            print(answer)
            print("=" * 100)
            da.append(Document(text=answer))
        return da
