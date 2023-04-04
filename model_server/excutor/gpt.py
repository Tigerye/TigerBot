from typing import Optional, Dict
from transformers import GPT2Tokenizer, GPT2LMHeadModel
import torch
from docarray import DocumentArray, Document
from jina import Executor, requests
import gc
from multiprocessing import set_start_method

try:
    set_start_method('spawn')
except RuntimeError:
    pass


class QAGPT(Executor):
    def __init__(self,
                 model_path_or_name: str,
                 max_length: int = 256,
                 device: Optional[str] = None,
                 **kwargs):
        super().__init__(**kwargs)
        self.device = device
        if not device:
            self.device = "cuda" if torch.cuda.is_available() else "cpu"
        else:
            self.device = device
        self.device = torch.device(self.device)
        self.tokenizer = GPT2Tokenizer.from_pretrained(model_path_or_name)
        self.tokenizer.pad_token = self.tokenizer.eos_token
        self.model = GPT2LMHeadModel.from_pretrained(model_path_or_name, pad_token_id=self.tokenizer.eos_token_id)
        self.model.to(self.device)
        self.model.eval()
        self.max_length = max_length

    @requests
    def generate(self,
                 docs,
                 parameters: Dict = {}) -> DocumentArray:
        tok_boqa = "<|beginofqa|>"
        tok_eoqa = "<|endofqa|>"
        tok_sepqa = "<|sepofqa|>"
        if "max_length" in parameters:
            self.max_length = parameters["max_length"]
        if "top_k" in parameters:
            top_k = parameters["top_k"]
        else:
            top_k = 4
        texts = [doc.text for doc in docs]
        da = DocumentArray()
        for text in texts:
            text = tok_boqa + text.strip() + tok_sepqa
            inputs = self.tokenizer(text, return_tensors='pt')
            inputs = {k: v.to(self.device) for k, v in inputs.items()}
            output = self.model.generate(**inputs,
                                         penalty_alpha=0.6,
                                         top_k=top_k,
                                         max_length=self.max_length,
                                         eos_token_id=self.tokenizer.convert_tokens_to_ids(tok_eoqa),
                                         pad_token_id=self.tokenizer.eos_token_id,
                                         early_stopping=True)
            gc.collect()
            torch.cuda.empty_cache()
            cur_res = self.tokenizer.decode(output[0], skip_special_tokens=False,
                                            spaces_between_special_tokens=False)
            print(cur_res)
            answer = cur_res[len(text):].split(tok_eoqa)[0].replace(tok_boqa, '').replace(tok_eoqa,
                                                                                          '').replace(
                tok_sepqa, '').strip()
            print(answer)
            da.append(Document(text=answer))
        return da
