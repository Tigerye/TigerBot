import json
import os
import glob
from datasets import load_dataset
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
    "baike_qa": 0.02,
    "crosswoz": 1.0,
    "duconv": 0.5,
    "dureader": 0.1,
    "naturalconv": 0.5,
    "alpaca": 1.0,
    "belle": 1.0,
    }

# baikeqa
for p in {"train", "valid"}:
    inpath = f"/data/yechen/lm/baike_qa2019/baike_qa_{p}.json"
    outpath = f"/data/yechen/lm/zh/sft/baike_qa_{p}.json"
    numlines = 0
    result = []
    with open(inpath, "r") as infile:
        for line in infile:
            numlines += 1
            data = json.loads(line)
            title = data["title"]
            desc = data["desc"]
            if len(title) >= len(desc):
                question = title
            else:
                question = desc
            qa = {}
            qa["instruction"] = question
            qa["output"] = data["answer"]
            result.append(qa)
            
            if numlines % 10000 == 0:
                print("num of lines read: " + str(numlines))
            
    with open(outpath, "w", encoding='utf-8') as outfile:
        json.dump(result, outfile, ensure_ascii=False, indent=4)
    print(f"done process {len(result)} qa pairs, from baikeqa.")
    if p == "train":
        all_train_result.extend(result)
        all_train_result_prop.extend(sample_result(result, train_sample_ratio["baike_qa"]))

# crosswoz
for p in {"train", "val", "test"}:
    inpath = f"/home/yechen/Workspace/CrossWOZ/data/crosswoz/{p}.json"
    outpath = f"/data/yechen/lm/zh/sft/crosswoz_{p}.json"
    num_id = 0
    num_msg = 0

    with open(inpath, 'r') as infile:
        data = json.load(infile)

        result = []
        for id in data:
            num_id += 1
            messages = data[id]["messages"]
            num_msg += 1
            content = ''
            len_msg = len(messages)
            num_pair = random.randint(1, len_msg / 2)
            for m in range(0, num_pair * 2, 2):
                mess_h = messages[m]["content"]
                mess_a = messages[m + 1]["content"]
                content += tok_ins + mess_h + tok_res + mess_a
            qa = {}
            question = content.rsplit(tok_res, 1)[0].split(tok_ins, 1)[1]
            answer = content.rsplit(tok_res, 1)[1]
            qa["instruction"] = question
            qa["output"] = answer
            result.append(qa)
    
    with open(outpath, "w", encoding='utf-8') as outfile:
        json.dump(result, outfile, ensure_ascii=False, indent=4)
    print(f"done process {len(result)} qa, from {num_id} ids, and {num_msg} messages, from crosswoz.")
    if p == "train":
        all_train_result.extend(result)
        all_train_result_prop.extend(sample_result(result, train_sample_ratio["crosswoz"]))
    
# duconv
for p in {"train", "dev"}:
    inpath = f"/home/yechen/Workspace/ACL-duconv/data/resource/{p}.txt"
    outpath = f"/data/yechen/lm/zh/sft/duconv_{p}.json"
    num_lines = 0
    result = []
    with open(inpath, 'r') as infile:
        for line in infile:
            num_lines += 1
            data = json.loads(line)
            conv = data["conversation"]
            len_conv = len(conv)
            if len_conv % 2 != 0:
                len_conv -= 1
            content = ''
            num_pair = random.randint(1, len_conv / 2)
            for m in range(0, num_pair * 2, 2):
                mess_h = conv[m].replace(" ", "")
                mess_a = conv[m + 1].replace(" ", "")
                content += tok_ins + mess_h + tok_res + mess_a
            qa = {}
            question = content.rsplit(tok_res, 1)[0].split(tok_ins, 1)[1]
            answer = content.rsplit(tok_res, 1)[1]
            qa["instruction"] = question
            qa["output"] = answer
            result.append(qa)
    
    with open(outpath, "w", encoding='utf-8') as outfile:
        json.dump(result, outfile, ensure_ascii=False, indent=4)
    print(f"done process {len(result)} qa, from {num_lines} chats.")
    if p == "train":
        all_train_result.extend(result)
        all_train_result_prop.extend(sample_result(result, train_sample_ratio["duconv"]))
    
# natural
inpath = "/data/yechen/lm/NaturalConv/dialog_release.json"
outpath = f"/data/yechen/lm/zh/sft/naturalconv_train.json"
num_lines = 0
result = []
with open(inpath, 'r') as infile:
    data = json.load(infile)
    for line in data:
        num_lines += 1
        conv = line["content"]
        len_conv = len(conv)
        if len_conv % 2 != 0:
            len_conv -= 1
        content = ''
        num_pair = random.randint(1, len_conv / 2)
        for m in range(0, num_pair * 2, 2):
            mess_h = conv[m].replace(" ", "")
            mess_a = conv[m + 1].replace(" ", "")
            content += tok_ins + mess_h + tok_res + mess_a
        qa = {}
        question = content.rsplit(tok_res, 1)[0].split(tok_ins, 1)[1]
        answer = content.rsplit(tok_res, 1)[1]
        qa["instruction"] = question
        qa["output"] = answer
        result.append(qa)
        
with open(outpath, "w", encoding='utf-8') as outfile:
    json.dump(result, outfile, ensure_ascii=False, indent=4)
print(f"done process {len(result)} qa, from {num_lines} chats, from naturalconv.")
all_train_result.extend(result)
all_train_result_prop.extend(sample_result(result, train_sample_ratio["naturalconv"]))

# dureader
indir_dev = "/data/yechen/lm/dureader/dureader-*.dev.json"
indir_train = "/data/yechen/lm/dureader/dureader-*.train.json"
outdir = "/data/yechen/lm/zh/sft/"

numlines = 0
numfiles = 0
numqa = 0

files = glob.glob(indir_dev)
files.extend(glob.glob(indir_train))

for f in files:
    numfiles += 1
    result = []
    ds = load_dataset('json', data_files=f)
    for d in ds["train"]:
        numlines += 1
        if len(d["answers"]) == 0:
            continue
        qa = {}        
        qa["instruction"] = d["question"]
        qa["output"] = d["answers"][0]
        result.append(qa)
        numqa += 1
    
    outpath = outdir + os.path.basename(f) 
    with open(outpath, "w", encoding='utf-8') as outfile:
        json.dump(result, outfile, ensure_ascii=False, indent=4)
    print(f"wrote file: {outpath}, with {len(result)} qa.")
    if f.find("train") >= 0:
        all_train_result.extend(result)
        all_train_result_prop.extend(sample_result(result, train_sample_ratio["dureader"]))

# alpaca
inpath = "/data/yechen/lm/alpaca/alpaca_zh_train.json"
alpaca = load_dataset('json', data_files=inpath)
for p in alpaca:
    result = []
    for d in alpaca[p]:
        qa = {}
        if d["input"]:
            qa["instruction"] = d["instruction"] + "\n\n" + d["input"]
        else:
            qa["instruction"] = d["instruction"]
        qa["output"] = d["output"]
        result.append(qa)
    outpath = f"/data/yechen/lm/zh/sft/alpaca_insres_{p}.json"
    with open(outpath, "w", encoding='utf-8') as outfile:
        json.dump(result, outfile, ensure_ascii=False, indent=4)
    print(f"done process {len(result)} qa from {p} part of alpaca.")
    if p == "train":
        all_train_result.extend(result)
        all_train_result_prop.extend(sample_result(result, train_sample_ratio["alpaca"]))
    
# belle
belle = load_dataset("json", data_files="/data/yechen/lm/belle/Belle.train.clean.json")
for p in belle:
    result = []
    for d in belle[p]:
        qa = {}
        qa["instruction"] = d["input"].strip()
        qa["output"] = d["target"].strip()
        result.append(qa)

    outpath = f"/data/yechen/lm/zh/sft/belle_{p}.json"
    with open(outpath, "w", encoding='utf-8') as outfile:
        json.dump(result, outfile, ensure_ascii=False, indent=4)
    print(f"done process {len(result)} qa from {p} part of belle.")
    if p == "train":
        all_train_result.extend(result)
        all_train_result_prop.extend(sample_result(result, train_sample_ratio["belle"]))

outpath = f"/data/yechen/lm/zh/sft/all_train.json"  
with open(outpath, "w", encoding='utf-8') as outfile:
        json.dump(all_train_result, outfile, ensure_ascii=False, indent=4)
print(f"done process {len(all_train_result)} qa from train part of all.")
outpath = f"/data/yechen/lm/zh/sft/all_train_prop.json"  
with open(outpath, "w", encoding='utf-8') as outfile:
        json.dump(all_train_result_prop, outfile, ensure_ascii=False, indent=4)
print(f"done process {len(all_train_result_prop)} qa from {p} part of all train prop.")
