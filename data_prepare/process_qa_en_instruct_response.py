from datasets import load_dataset
import json
import re
import random

tok_q = "\n\nHuman: "
tok_a = "\n\nAssistant: "

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
    "squad": 0.1,
    "squad_v2": 0.1,
    "web_questions": 1.0,
    "commonsense_qa": 1.0,
    "openbookqa": 1.0,
    "rmstatic": 0.1,
    "mrqa": 0.01,
    "trivia_qa": 0.3,
    "yahoo_answers_qa": 1.0,
    "instruct_syn_pr": 1.0,
    "alpaca": 1.0
    }

wiki_qa = load_dataset("wiki_qa")
for p in wiki_qa:
    result = []
    for d in wiki_qa[p]:
        if d["label"] == 1:
            qa = {}
            qa["instruction"] = d["question"]
            qa["output"] = d["answer"]
            result.append(qa)
            
    outpath = f"/data/yechen/lm/en/sft/wiki_qa_{p}.json"
    with open(outpath, "w", encoding='utf-8') as outfile:
        json.dump(result, outfile, ensure_ascii=False, indent=4)
    print(f"done process {len(result)} qa from {p} part of wiki_qa.")
    if p == "train":
        all_train_result.extend(result)
        all_train_result_prop.extend(sample_result(result, train_sample_ratio["wiki_qa"]))

squad = load_dataset("squad")
for p in squad:
    result = []
    for d in squad[p]:
        qa = {}
        qa["instruction"] = d["context"] + "\n\n" + d["question"]
        qa["output"] = d["answers"]["text"][0]
        result.append(qa)

    outpath = f"/data/yechen/lm/en/sft/squad_{p}.json"
    with open(outpath, "w", encoding='utf-8') as outfile:
        json.dump(result, outfile, ensure_ascii=False, indent=4)
    print(f"done process {len(result)} qa from {p} part of squad.")
    if p == "train":
        all_train_result.extend(result)
        all_train_result_prop.extend(sample_result(result, train_sample_ratio["squad"]))

squad_v2 = load_dataset("squad_v2")
for p in squad_v2:
    result = []
    for d in squad_v2[p]:
        if not d["answers"]["text"]:
            continue 
        qa = {}
        qa["instruction"] = d["context"] + "\n\n" + d["question"]
        qa["output"] = d["answers"]["text"][0]
        result.append(qa)

    outpath = f"/data/yechen/lm/en/sft/squad_v2_{p}.json"
    with open(outpath, "w", encoding='utf-8') as outfile:
        json.dump(result, outfile, ensure_ascii=False, indent=4)
    print(f"done process {len(result)} qa from {p} part of squad_v2.")
    if p == "train":
        all_train_result.extend(result)
        all_train_result_prop.extend(sample_result(result, train_sample_ratio["squad_v2"]))

web_questions = load_dataset("web_questions")
for p in web_questions:
    result = []
    for d in web_questions[p]:
        qa = {}
        qa["instruction"] = d["question"]
        qa["output"] = d["answers"][0]
        result.append(qa)

    outpath = f"/data/yechen/lm/en/sft/web_questions_{p}.json"
    with open(outpath, "w", encoding='utf-8') as outfile:
        json.dump(result, outfile, ensure_ascii=False, indent=4)
    print(f"done process {len(result)} qa from {p} part of web_questions.")
    if p == "train":
        all_train_result.extend(result)
        all_train_result_prop.extend(sample_result(result, train_sample_ratio["web_questions"]))

commonsense_qa = load_dataset("commonsense_qa")
for p in commonsense_qa:
    result = []
    for d in commonsense_qa[p]:
        qa = {}
        if not d["answerKey"]:
            continue
        question = d["question"]
        answer = d["choices"]["text"][d["choices"]["label"].index(d["answerKey"])]
        qa["instruction"] = question
        qa["output"] = answer
        result.append(qa)

    outpath = f"/data/yechen/lm/en/sft/commonsense_{p}.json"
    with open(outpath, "w", encoding='utf-8') as outfile:
        json.dump(result, outfile, ensure_ascii=False, indent=4)
    print(f"done process {len(result)} qa from {p} part of commonsense_qa.")
    if p == "train":
        all_train_result.extend(result)
        all_train_result_prop.extend(sample_result(result, train_sample_ratio["commonsense_qa"]))

openbookqa = load_dataset("openbookqa")
for p in openbookqa:
    result = []
    for d in openbookqa[p]:
        qa = {}
        if not d["answerKey"]:
            continue
        question = d["question_stem"]
        answer = d["choices"]["text"][d["choices"]["label"].index(d["answerKey"])]        
        qa["instruction"] = question
        qa["output"] = answer
        result.append(qa)

    outpath = f"/data/yechen/lm/en/sft/openbookqa_{p}.json"
    with open(outpath, "w", encoding='utf-8') as outfile:
        json.dump(result, outfile, ensure_ascii=False, indent=4)
    print(f"done process {len(result)} qa from {p} part of openbookqa.")
    if p == "train":
        all_train_result.extend(result)
        all_train_result_prop.extend(sample_result(result, train_sample_ratio["openbookqa"]))

rmstatic = load_dataset("Dahoas/rm-static")
for p in rmstatic:
    result = []
    for d in rmstatic[p]:
        qa = {}
        question = d["prompt"].replace(tok_a, tok_res)
        question = question.replace(tok_q, tok_ins)
        question = question.split(tok_ins, 1)[1]
        question = question.rsplit("\n\nAssistant:", 1)[0]      
        qa["instruction"] = question
        qa["output"] = d["chosen"].strip()
        result.append(qa)

    outpath = f"/data/yechen/lm/en/sft/rmstatic_{p}.json"
    with open(outpath, "w", encoding='utf-8') as outfile:
        json.dump(result, outfile, ensure_ascii=False, indent=4)
    print(f"done process {len(result)} qa pairs, from {p} part of rmstatic.")
    if p == "train":
        all_train_result.extend(result)
        all_train_result_prop.extend(sample_result(result, train_sample_ratio["rmstatic"]))

mrqa = load_dataset("mrqa")
for p in mrqa:
    result = []
    for d in mrqa[p]:
        qa = {}        
        qa["instruction"] = d["context"] + "\n\n" + d["question"]
        qa["output"] = d["answers"][0]
        result.append(qa)

    outpath = f"/data/yechen/lm/en/sft/mrqa_{p}.json"
    with open(outpath, "w", encoding='utf-8') as outfile:
        json.dump(result, outfile, ensure_ascii=False, indent=4)
    print(f"done process {len(result)} qa from {p} part of mrqa.")
    if p == "train":
        all_train_result.extend(result)
        all_train_result_prop.extend(sample_result(result, train_sample_ratio["mrqa"]))

trivia_qa = load_dataset("trivia_qa", 'rc.nocontext')
for p in trivia_qa:
    result = []
    for d in trivia_qa[p]:
        qa = {}        
        qa["instruction"] = d["question"]
        qa["output"] = d["answer"]["value"]
        result.append(qa)

    outpath = f"/data/yechen/lm/en/sft/trivia_qa_{p}.json"
    with open(outpath, "w", encoding='utf-8') as outfile:
        json.dump(result, outfile, ensure_ascii=False, indent=4)
    print(f"done process {len(result)} qa from {p} part of trivia_qa.")
    if p == "train":
        all_train_result.extend(result)
        all_train_result_prop.extend(sample_result(result, train_sample_ratio["trivia_qa"]))

yahoo_answers_qa = load_dataset("yahoo_answers_qa")
for p in yahoo_answers_qa:
    result = []
    for d in yahoo_answers_qa[p]:
        qa = {}
        qa["instruction"] = d["question"]
        qa["output"] = d["answer"]
        result.append(qa)

    outpath = f"/data/yechen/lm/en/sft/yahoo_answers_{p}.json"
    with open(outpath, "w", encoding='utf-8') as outfile:
        json.dump(result, outfile, ensure_ascii=False, indent=4)
    print(f"done process {len(result)} qa from {p} part of yahoo_answers_qa.")
    if p == "train":
        all_train_result.extend(result)
        all_train_result_prop.extend(sample_result(result, train_sample_ratio["yahoo_answers_qa"]))

truthful_qa = load_dataset('truthful_qa', 'generation')
for p in truthful_qa:
    result = []
    for d in truthful_qa[p]:
        qa = {}
        qa["instruction"] = d["question"]
        qa["output"] = d["best_answer"]
        result.append(qa)

    outpath = f"/data/yechen/lm/en/sft/truthful_qa_{p}.json"
    with open(outpath, "w", encoding='utf-8') as outfile:
        json.dump(result, outfile, ensure_ascii=False, indent=4)
    print(f"done process {len(result)} qa from {p} part of truthful_qa.")
    if p == "train":
        all_train_result.extend(result)
        all_train_result_prop.extend(sample_result(result, train_sample_ratio["truthful_qa"]))
    
instruct_syn_pr = load_dataset('Dahoas/instruct-synthetic-prompt-responses')
for p in instruct_syn_pr:
    result = []
    for d in instruct_syn_pr[p]:
        qa = {}
        question = d["prompt"]
        answer = d["response"]
        question = re.sub('[0-9\s]+$', '', question).strip()
        answer = re.sub('Answer:', '', answer.strip()).strip()
        qa["instruction"] = question
        qa["output"] = answer
        result.append(qa)
    outpath = f"/data/yechen/lm/en/sft/instruct_syn_{p}.json"
    with open(outpath, "w", encoding='utf-8') as outfile:
        json.dump(result, outfile, ensure_ascii=False, indent=4)
    print(f"done process {len(result)} qa from {p} part of instruct_syn.")
    if p == "train":
        all_train_result.extend(result)
        all_train_result_prop.extend(sample_result(result, train_sample_ratio["instruct_syn_pr"]))
    
alpaca = load_dataset('json', data_files='/home/yechen/Workspace/stanford_alpaca/alpaca_data.json')
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
    outpath = f"/data/yechen/lm/en/sft/alpaca_insres_{p}.json"
    with open(outpath, "w", encoding='utf-8') as outfile:
        json.dump(result, outfile, ensure_ascii=False, indent=4)
    print(f"done process {len(result)} qa from {p} part of alpaca.")
    if p == "train":
        all_train_result.extend(result)
        all_train_result_prop.extend(sample_result(result, train_sample_ratio["alpaca"]))

outpath = f"/data/yechen/lm/en/sft/all_train.json"  
with open(outpath, "w", encoding='utf-8') as outfile:
        json.dump(all_train_result, outfile, ensure_ascii=False, indent=4)
print(f"done process {len(all_train_result)} qa from {p} part of all train.")
outpath = f"/data/yechen/lm/en/sft/all_train_prop.json"  
with open(outpath, "w", encoding='utf-8') as outfile:
        json.dump(all_train_result_prop, outfile, ensure_ascii=False, indent=4)
print(f"done process {len(all_train_result_prop)} qa from {p} part of all train prop.")
