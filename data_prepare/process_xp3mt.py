from datasets import load_dataset
import json
import re
import random

tok_ins = "\n\n### Instruction:\n"
tok_res = "\n\n### Response:\n"


def sample_result(
        result: list=None,
        ratio: float=1.0,
        ):
    return random.sample(result, round(len(result) * ratio))
    

all_train_result = []
all_train_result_prop = []

train_sample_ratio = {
    "wiki_qa": 1.0,
    }

inpath = "/data/yechen/lm/xp3mt/xp3mt-zh-train.jsonl"
data = []
with open(inpath, "r", encoding="utf-8") as f:
    for idx, line in enumerate(f):
        data.append(json.loads(line.strip()))
    
    num_en_inp = 0
    num_en_tgt = 0
    for d in data:
        if re.search('[a-zA-Z]', d["inputs"]):
            num_en_inp += 1
        if re.search('[a-zA-Z]', d["targets"]):
            num_en_tgt += 1
    print(f"inp: {num_en_inp/len(data)}")
    print(f"tgt: {num_en_tgt/len(data)}")
            
outpath = f"/data/yechen/lm/en/sft/wiki_qa_{p}.json"
with open(outpath, "w", encoding='utf-8') as outfile:
    json.dump(result, outfile, ensure_ascii=False, indent=4)
print(f"done process {len(result)} qa from {p} part of wiki_qa.")
if p == "train":
    all_train_result.extend(result)
    all_train_result_prop.extend(sample_result(result, train_sample_ratio["wiki_qa"]))
        
