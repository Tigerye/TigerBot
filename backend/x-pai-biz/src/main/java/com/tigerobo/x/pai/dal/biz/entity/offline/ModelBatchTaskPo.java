package com.tigerobo.x.pai.dal.biz.entity.offline;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "model_batch_task")
public class ModelBatchTaskPo {

    @Id
    @KeySql(useGeneratedKeys = true)
    Integer id;
    Long reqId;
    Integer userId;
    String bizId;
    String bizName;
    Integer bizType;
    String inputPath;
    String outPath;
    Boolean hasAppendCallNum;
    Integer status;
    Integer dealNum;
    Integer totalNum;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;
    Boolean isDeleted;
    Integer dealTime;
    String errMsg;
    String result;
    String fileName;
    String ip;
}
