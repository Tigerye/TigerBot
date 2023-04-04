package com.tigerobo.x.pai.api.biz.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.tigerobo.x.pai.api.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description: 业务模块-需求任务详情类
 * @modified By:
 * @version: $
 */
@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(value = "业务模块-需求任务详情类")
public class Task extends Entity {

    //img,video
    private String coverType = "img";
    @ApiModelProperty(value = "预算")
    private String budget;
    @ApiModelProperty(value = "预算金额")
    private Double budgetValue;
    @ApiModelProperty(value = "开始日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startDate;
    @ApiModelProperty(value = "交付日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date deliveryDate;
//    @ApiModelProperty(value = "模型样式")
//    private String style;
    @ApiModelProperty(value = "状态: SUBMIT, IMPLEMENT, COMPLETED")
    @Builder.Default
    private Status status = Status.REVIEW;
    @ApiModelProperty(value = "测评说明")
    private String evaluation;
    @ApiModelProperty(value = "隶属需求ID")
    private Integer demandId;
    @ApiModelProperty(value = "隶属需求UUID")
    private String demandUuid;

    @ApiModelProperty(value = "最佳模型ID")
    private Integer modelId;
    @ApiModelProperty(value = "最佳模型UUID")
    private String modelUuid;

    String appShortName;

    Integer score;


    String slogan;
    Integer scope;

    public Model toDefaultModel(){
        return Model.builder()
//                .type(this.getType())
                .name(this.getName())
                .intro(this.getIntro())
                .desc(this.getDesc())
//                .nameEn(this.getNameEn())
//                .introEn(this.getIntroEn())
//                .descEn(this.getDescEn())
                .image(this.getImage())
//                .style(this.getStyle())
                .build();
    }


    @Override
    public void setType(Type type) {
        this.type = Type.TASK;
    }

    @Override
    public Type getType() {
        this.type = Type.TASK;
        return this.type;
    }

    @Getter
    @AllArgsConstructor
    public enum Status {
        UNKNOWN(0, "其他", "默认非公开草稿阶段"),
        PREPARE(10, "预备", "任务讨论、咨询、询价等(预留)"),
        SUBMIT(20, "提交", "任务提交(预留)"),
        REVIEW(30, "审核", "任务审核(预留)"),
        DATESET(40, "数据", "数据预处理(预留)"),
        IMPLEMENT(50, "执行", "需求执行"),
        MODIFY(60, "变更", "需求执行变更(预留)"),
        ACCEPTANCE(70, "验收", "需求验收"),
        SETTLEMENT(80, "结算", "交易结算"),
        COMPLETED(90, "完成", "需求完成");

        private final Integer val;
        private final String name;
        private final String desc;

        public static Task.Status valueOf(int val) {
            switch (val) {
                case 10:
                    return PREPARE;
                case 20:
                    return SUBMIT;
                case 30:
                    return REVIEW;
                case 40:
                    return DATESET;
                case 50:
                    return IMPLEMENT;
                case 60:
                    return MODIFY;
                case 70:
                    return ACCEPTANCE;
                case 80:
                    return SETTLEMENT;
                case 90:
                    return COMPLETED;
                case 0:
                default:
                    return UNKNOWN;
            }
        }
    }
}
