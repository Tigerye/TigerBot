package com.tigerobo.x.pai.dal.biz.entity;

import com.tigerobo.x.pai.dal.base.BaseDo;
import lombok.Data;

import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description: 业务系统-任务关联数据表
 * @modified By:
 * @version: $
 */
@Data
//@EqualsAndHashCode(callSuper = false)
//@SuperBuilder
//@NoArgsConstructor
//@AllArgsConstructor
@Table(name = "`xpai-biz-task-dataset`", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
public class TaskDatasetDo extends BaseDo {
    private Integer taskId;
    private String taskUuid;
    private Integer datasetId;
    private String datasetUuid;
    private Integer scene;
}

