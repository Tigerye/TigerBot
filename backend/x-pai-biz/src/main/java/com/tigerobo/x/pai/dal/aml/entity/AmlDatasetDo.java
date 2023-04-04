package com.tigerobo.x.pai.dal.aml.entity;

import com.tigerobo.x.pai.dal.base.ComDo;
import lombok.Data;

import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

//@NoArgsConstructor
@Data
//@SuperBuilder
@Table(name = "`aml_dataset`", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
public class AmlDatasetDo extends ComDo {
    Integer amlId;
    Integer baseModelId;
    String path;
    String fileType;
    Byte status;
    String statistic;
    String parsePath;

    Integer allItemCount;
    Integer labeledCount;
    Integer unlabeledCount;

    Integer trainCount;
    Integer validationCount;
    Integer testCount;

    //模式使用参数
    String preTrainModelName;

    String msg;

}
