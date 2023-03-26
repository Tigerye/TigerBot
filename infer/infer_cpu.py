from transformers import AutoTokenizer, AutoModelForCausalLM
import torch
import fire

tok_ins = "\n\n### Instruction:\n"
tok_inp = "\n\n### Input:\n"
tok_res = "\n\n### Response:\n"

PROMPT_DICT = {
    "prompt_input": (
        "Below is an instruction that describes a task, paired with an input that provides further context. "
        "Write a response that appropriately completes the request.\n\n"
        "### Instruction:\n{instruction}\n\n### Input:\n{input}\n\n### Response:"
    ),
    "prompt_no_input": (
        "Below is an instruction that describes a task. "
        "Write a response that appropriately completes the request.\n\n"
        "### Instruction:\n{instruction}\n\n### Response:"
    ),
}


def main(
    model_path: str="/home/yechen/models/gpt2-large-sft-alp",
    max_input_length: int=512,
    max_generate_length: int=1024,
):
    print(f"loading model: {model_path}...")
    tokenizer = AutoTokenizer.from_pretrained(
        model_path,
        cache_dir=None,
        model_max_length=max_generate_length,
        padding_side="left",
        truncation_side='left',
        padding=True,
        truncation=True,
        # use_fast=False,
    )
    if tokenizer.model_max_length is None or tokenizer.model_max_length > 1024:
        tokenizer.model_max_length = 1024
    
    model = AutoModelForCausalLM.from_pretrained(model_path, pad_token_id=tokenizer.pad_token_id)
    device = 'cpu'
    # device = torch.cuda.current_device()
    # model.to(device)
    
    generation_kwargs = {
        "penalty_alpha": 0.6,
        "top_k": 4,
        "max_length": max_generate_length,
        "eos_token_id": tokenizer.eos_token_id,
        "pad_token_id": tokenizer.pad_token_id,
        "early_stopping": True,
        "no_repeat_ngram_size": 4,
        }

    prompt_no_input = PROMPT_DICT["prompt_no_input"]

    sess_text = ""
    while True:
        raw_text = input("prompt(\"exit\" to end, \"clear\" to clear session) >>> ")
        if not raw_text:
            print('prompt should not be empty!')
            continue
        if raw_text.strip() == "exit":
            print('session ended.')
            break
        if raw_text.strip() == "clear":
            print('session cleared.')
            sess_text = ""
            continue
    
        query_text = raw_text.strip()
        sess_text += tok_ins + query_text
        input_text = prompt_no_input.format_map({'instruction': sess_text.split(tok_ins, 1)[1]})
        # print("sess_text: " + sess_text)
        # print("input_text: " + input_text)
        inputs = tokenizer(input_text, return_tensors='pt', truncation=True, max_length=max_input_length)
        inputs = {k: v.to(device) for k, v in inputs.items()}
        output = model.generate(**inputs, **generation_kwargs)
        result = tokenizer.decode(output[0], skip_special_tokens=False, spaces_between_special_tokens=False)
        answer = result.rsplit("\n\n### Response:", 1)[1].rstrip(tokenizer.eos_token)
        sess_text += tok_res + answer
    
        print("=" * 100)
        # print("result: " + result)
        print(answer)
        print("=" * 100)


if __name__ == "__main__":
    fire.Fire(main)

