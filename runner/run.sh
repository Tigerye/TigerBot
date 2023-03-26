#!/bin/bash
#JSUB -q hb
#JSUB -hosts 1
#JSUB -m gpu23 
### 输出日志格式，%J 表示作业的 id，输出的日志格式为 output.作业 id。
#JSUB -o output.%J
#JSUB -e output.%J
### 指定运行程序的名称为 test (可选参数)
#JSUB -J gpt2-3.5b-zh
# 加载环境，此处加载 anaconda 环境以及通过 anaconda 创建的名为 pytorch 的环境
export MODULEPATH=/share/home/deploy/apps/modulefiles:$MODULEPATH
export  PATH=/share/home/hbuser1/soft/pdsh1/bin
module load anaconda/2021.11
module load cuda/11.6.0
module load gcc/9.4.0-gcc-4.8.5-64b
source activate py38
export PYTHONUNBUFFERED=1
export NCCL_DEBUG=INFO
export NCCL_IB_DISABLE=0
nvcc --version >> nvcc.log
which nvcc >> nvcc.log
### 获取节点主机名,将节点主机名保存到 hostfile.[作业ID]文件中
HOSTFILE="hostfile.${JH_JOBID}"
echo "${JH_HOSTS}"
array=(${JH_HOSTS//,/ })
for var in "${array[@]}";
do
   if [[ "${var}" == "64" ]]; then
      continue
   fi
   let k=k+1
   host[$k]="${var}"
   ### slots=4 表示每个节点使用GPU数量
   echo "$var slots=4" >> "${HOSTFILE}"
done

#程序输出的日志名称。${JH_JOBID} 为作业 ID
JOB_LOG="train_${JH_JOBID}.log"
#建议采用 “>>” 方式将计算日志重定向到指定文件中，便于实时查看计算日志。
deepspeed --hostfile=./hostfile.${JH_JOBID}   --num_nodes=2 /share/home/hbuser1/yechen/stanford_alpaca/train_sft.py \
--deepspeed /share/home/hbuser1/weicai/ds_config_zero3.json \
--model_name_or_path "/share/home/hbuser1/yechen/models/Wenzhong2.0-GPT2-3.5B-chinese" \
--data_path "/share/home/hbuser1/yechen/lm/zh/sft/crosswoz_val.json" \
--bf16 True \
--output_dir "/share/home/hbuser1/yechen/models/gpt2-zh-3.5b-sft-insres_prop" \
--model_max_length 1024 \
--num_train_epochs 1 \
--per_device_train_batch_size 32 \
--per_device_eval_batch_size 32 \
--gradient_checkpointing \
--evaluation_strategy "no" \
--save_strategy "steps" \
--save_steps 2000 \
--save_total_limit 1 \
--learning_rate 2e-5 \
--weight_decay 0. \
--warmup_ratio 0.03 \
--lr_scheduler_type "cosine" \
--logging_steps 1 \
--tf32 True >> "${JOB_LOG}" 2>&1