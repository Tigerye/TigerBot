package com.tigerobo.x.pai.biz.converter;

import com.tigerobo.x.pai.api.auth.entity.Group;
import com.tigerobo.x.pai.api.biz.entity.Task;
import com.tigerobo.x.pai.biz.utils.IdGenerator;
import com.tigerobo.x.pai.dal.biz.entity.TaskDo;
import org.apache.commons.lang3.StringUtils;

public class TaskConvert {
    public static Task convert(TaskDo taskDo) {
        if (taskDo == null){
            return null;
        }
        Group group = null;
        if(StringUtils.isNotBlank(taskDo.getGroupUuid())){
            group = new Group();
            group.setId(taskDo.getGroupId());
            group.setUuid(taskDo.getGroupUuid());
        }

        final String image = taskDo.getImage();
        String coverType = "img";
        if (StringUtils.isNotBlank(image)){
            if (image.toLowerCase().endsWith(".mp4")||image.toLowerCase().contains(".mp4?")){
                coverType = "video";
            }
        }

        return Task.builder()
                .id(taskDo.getId())
                .createBy(taskDo.getCreateBy())
                .createTime(taskDo.getCreateTime())
                .updateTime(taskDo.getModelUpdateTime())
                .isDeleted(taskDo.getIsDeleted())
                .uuid(taskDo.getUuid())
                .name(taskDo.getName())
                .intro(taskDo.getIntro())
                .desc(taskDo.getDesc())
                .scope(taskDo.getScope())
                .nameEn(taskDo.getNameEn())
                .introEn(taskDo.getIntroEn())
                .descEn(taskDo.getDescEn())
                .image(taskDo.getImage())
                .coverType(coverType)
                .budget(taskDo.getBudget())
                .budgetValue(taskDo.getBudgetValue())
                .startDate(taskDo.getStartDate())
                .deliveryDate(taskDo.getDeliveryDate())
//                .style(taskDo.getStyle())
                .status(Task.Status.valueOf(taskDo.getStatus()))
                .evaluation(taskDo.getEvaluation())
                .demandId(taskDo.getDemandId())
                .demandUuid(taskDo.getDemandUuid())
                .modelId(taskDo.getModelId())
                .modelUuid(taskDo.getModelUuid())
                .groupUuid(taskDo.getGroupUuid())
                .group(group)
                .createBy(taskDo.getCreateBy())
                .appShortName(taskDo.getAppShortName())
                .score(taskDo.getScore())
                .slogan(taskDo.getSlogan())
                .build();
    }
    public static TaskDo convert(Task task) {

        if (task == null){
            return null;
        }

        String uuid = task.getUuid();
        if (uuid == null){
            uuid = IdGenerator.getId();
        }
        return TaskDo.builder()
                .id(task.getId())
                .uuid(uuid)
                .isDeleted(task.getIsDeleted())
                .name(task.getName())
                .intro(task.getIntro())
                .desc(task.getDesc())
                .nameEn(task.getNameEn() != null ? task.getNameEn() : task.getName())
                .introEn(task.getIntroEn())
                .descEn(task.getDescEn())
                .image(task.getImage())
                .budget(task.getBudget())
                .budgetValue(task.getBudgetValue())
                .startDate(task.getStartDate())
                .deliveryDate(task.getDeliveryDate())
//                .style(task.getStyle() != null ? task.getStyle() : null)
                .status(task.getStatus() != null ? task.getStatus().getVal() : null)
                .evaluation(task.getEvaluation())
                .demandId(task.getDemandId())
                .demandUuid(task.getDemandUuid())
                .modelId(task.getModelId())
                .modelUuid(task.getModelUuid())
                .slogan(task.getSlogan())
                .groupId(task.getGroup() != null ? task.getGroup().getId() : null)
                .groupUuid(task.getGroup() != null ? task.getGroup().getUuid() : null)
                .build();
    }
}
