from typing import Optional, Dict
from transformers import AutoTokenizer, AutoModelForCausalLM
import torch
from docarray import DocumentArray, Document
from jina import Executor, requests
import gc
from multiprocessing import set_start_method

try:
    set_start_method('spawn')
except RuntimeError:
    pass


class Bloom(Executor):
    def __init__(self,
                 model_path_or_name: str,
                 max_input_length: int = 512,
                 max_generate_length: int = 1024,
                 device: Optional[str] = None,
                 **kwargs):
        super().__init__(**kwargs)
        self.max_input_length = max_input_length
        self.max_generate_length = max_generate_length
        self.device = device
        if not device:
            self.device = "cuda" if torch.cuda.is_available() else "cpu"
        else:
            self.device = device
        self.device = torch.device(self.device)
        self.tokenizer = AutoTokenizer.from_pretrained(
            model_path_or_name,
            cache_dir=None,
            model_max_length=self.max_generate_length,
            padding_side="left",
            truncation_side='left',
            padding=True,
            truncation=True
        )

        self.model = AutoModelForCausalLM.from_pretrained(model_path_or_name, pad_token_id=self.tokenizer.pad_token_id)
        self.model.to(self.device)
        self.model.eval()

    @requests
    def generate(self,
                 docs) -> DocumentArray:

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
            inputs = self.tokenizer(input_text, return_tensors="pt", truncation=True, max_length=self.max_input_length)

            inputs = {k: v.to(self.device) for k, v in inputs.items()}
            with torch.no_grad():
                output = self.model.generate(**inputs,
                                             top_p=0.95,
                                             temperature=0.8,
                                             max_length=self.max_generate_length,
                                             eos_token_id=self.tokenizer.eos_token_id,
                                             pad_token_id=self.tokenizer.pad_token_id,
                                             no_repeat_ngram_size=4,
                                             early_stopping=True)
                gc.collect()
                torch.cuda.empty_cache()
            cur_res = self.tokenizer.decode(output[0], skip_special_tokens=False,
                                            spaces_between_special_tokens=False)
            print(f"cur decode result: {cur_res}")
            print("=" * 100)
            answer = cur_res.rsplit(tok_res, 1)[1].rstrip(self.tokenizer.eos_token)
            print(f"answer: {answer}")
            print("=" * 100)
            da.append(Document(text=answer))
        return da
