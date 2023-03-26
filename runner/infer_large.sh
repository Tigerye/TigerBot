#!/bin/bash

cd /share/home/hbuser1/weicai/transformers-bloom-inference

CUDA_VISIBLE_DEVICES=0,1,2,3 \
python -m inference_server.cli_lamma \
--model_name /share/home/hbuser1/weicai/stanford_alpaca/llama-65b-sft-alpaca-insres/checkpoint-1900 \
--model_class LlamaForCausalLM --dtype bf16 --deployment_framework ds_zero