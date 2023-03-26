import json
import os
import glob
from datasets import load_dataset
import random
import re

tok_q = "\n\nHuman: "
tok_a = "\n\nAssistant: "

tok_ins = "\n\n### Instruction:\n"
tok_res = "\n\n### Response:\n"


def sample_result(
        result: list=None,
        ratio: float=1.0,
        ):
    return random.sample(result, round(len(result) * ratio))

    
# merge alpaca and belle
all_train_result = []
all_train_result_prop = []

train_sample_ratio = {
    "alpaca": 1.0,
    "belle": 0.1,
    "belle1m": 0.1,
    "belle1": 0.2,
    "rmstatic": 0.1,
    "naturalconv": 0.5,
    }

# alpaca en
alpaca = load_dataset('json', data_files='/data/yechen/lm/alpaca/alpaca_data_cleaned.json')
for p in alpaca:
    result = []
    for d in alpaca[p]:
        if not d["instruction"] or not d["output"]:
            continue
        qa = {}
        if d["input"]:
            qa["instruction"] = d["instruction"].strip() + "\n" + d["input"].strip()
        else:
            qa["instruction"] = d["instruction"].strip()
        qa["output"] = d["output"]
        result.append(qa)
    if p == "train":
        all_train_result.extend(result)
        all_train_result_prop.extend(sample_result(result, train_sample_ratio["alpaca"]))
    
# belle
belle = load_dataset("json", data_files="/data/yechen/lm/belle/Belle.train.clean.json")
for p in belle:
    result = []
    for d in belle[p]:
        qa = {}
        input = d["input"].replace("\\n", "\n")
        target = d["target"].replace("\\n", "\n")
        input = re.sub(r"\n+", "\n", input)
        target = re.sub(r"\n+", "\n", target)
        qa["instruction"] = input.strip()
        qa["output"] = target.strip()
        result.append(qa)
    if p == "train":
        all_train_result.extend(result)
        all_train_result_prop.extend(sample_result(result, train_sample_ratio["belle"]))
        
# belle1m
belle = load_dataset("json", data_files="/data/yechen/lm/belle/Belle-1m-train.json")
for p in belle:
    result = []
    for d in belle[p]:
        qa = {}
        input = d["input"].replace("\\n", "\n")
        target = d["target"].replace("\\n", "\n")
        input = re.sub(r"\n+", "\n", input)
        target = re.sub(r"\n+", "\n", target)
        qa["instruction"] = input.strip()
        qa["output"] = target.strip()
        result.append(qa)
    if p == "train":
        all_train_result.extend(result)
        all_train_result_prop.extend(sample_result(result, train_sample_ratio["belle1m"]))
        
# # belle1
# belle1 = load_dataset("json", data_files="/mnt/nfs/datapool/belle/belle-part01.json")
# for p in belle1:
#     result = []
#     for d in belle[p]:
#         qa = {}
#         qa["instruction"] = d["input"].strip()
#         qa["output"] = d["target"].strip()
#         result.append(qa)
#     if p == "train":
#         all_train_result.extend(result)
#         all_train_result_prop.extend(sample_result(result, train_sample_ratio["belle1"]))
        
# rmstatic = load_dataset("Dahoas/rm-static")
# for p in rmstatic:
#     result = []
#     for d in rmstatic[p]:
#         qa = {}
#         question = d["prompt"].replace(tok_a, tok_res)
#         question = question.replace(tok_q, tok_ins)
#         question = question.split(tok_ins, 1)[1]
#         question = question.rsplit("\n\nAssistant:", 1)[0]      
#         qa["instruction"] = question
#         qa["output"] = d["chosen"].strip()
#         result.append(qa)
#
#     if p == "train":
#         all_train_result.extend(result)
#         all_train_result_prop.extend(sample_result(result, train_sample_ratio["rmstatic"]))
#
# # natural
# inpath = "/data/yechen/lm/NaturalConv/dialog_release.json"
# num_lines = 0
# result = []
# with open(inpath, 'r') as infile:
#     data = json.load(infile)
#     for line in data:
#         num_lines += 1
#         conv = line["content"]
#         len_conv = len(conv)
#         if len_conv % 2 != 0:
#             len_conv -= 1
#         content = ''
#         num_pair = random.randint(1, len_conv / 2)
#         for m in range(0, num_pair * 2, 2):
#             mess_h = conv[m].replace(" ", "")
#             mess_a = conv[m + 1].replace(" ", "")
#             content += tok_ins + mess_h + tok_res + mess_a
#         qa = {}
#         question = content.rsplit(tok_res, 1)[0].split(tok_ins, 1)[1]
#         answer = content.rsplit(tok_res, 1)[1]
#         qa["instruction"] = question
#         qa["output"] = answer
#         result.append(qa)
#
# all_train_result.extend(result)
# all_train_result_prop.extend(sample_result(result, train_sample_ratio["naturalconv"]))

outpath = f"/data/yechen/lm/en/sft/all_train_alpaca_belle.json"  
with open(outpath, "w", encoding='utf-8') as outfile:
        json.dump(all_train_result, outfile, ensure_ascii=False, indent=4)
print(f"done process {len(all_train_result)} qa from train part of all.")
outpath = f"/data/yechen/lm/en/sft/all_train_alpaca_belle_prop_0.1.json"  
with open(outpath, "w", encoding='utf-8') as outfile:
        json.dump(all_train_result_prop, outfile, ensure_ascii=False, indent=4)
print(f"done process {len(all_train_result_prop)} qa from {p} part of all train prop.")

# merge all
# en_all_path = "/data/yechen/lm/en/sft/all_train.json"
# zh_all_path = "/data/yechen/lm/zh/sft/all_train.json"
# result = []
# data = load_dataset("json", data_files=en_all_path)
# result.extend(data["train"])
# data = load_dataset("json", data_files=zh_all_path)
# result.extend(data["train"])
#
# outpath = "/data/yechen/lm/en/sft/all_train_enzh.json"
# with open(outpath, "w", encoding='utf-8') as outfile:
#         json.dump(result, outfile, ensure_ascii=False, indent=4)
# print(f"done process {len(result)} qa from all result.")
#
# en_prop_path = "/data/yechen/lm/en/sft/all_train_prop.json"
# zh_prop_path = f"/data/yechen/lm/zh/sft/all_train_prop.json"
# result = []
# data = load_dataset("json", data_files=en_prop_path)
# result.extend(data["train"])
# data = load_dataset("json", data_files=zh_prop_path)
# result.extend(data["train"])
#
# outpath = "/data/yechen/lm/en/sft/all_train_enzh_prop.json"  
# with open(outpath, "w", encoding='utf-8') as outfile:
#         json.dump(result, outfile, ensure_ascii=False, indent=4)
# print(f"done process {len(result)} qa from {p} part of all result prop.")

