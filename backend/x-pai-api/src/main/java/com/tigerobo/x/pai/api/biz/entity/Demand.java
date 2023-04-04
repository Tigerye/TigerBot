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
 * @description: 业务模块-业务需求详情类
 * @modified By:
 * @version: $
 */
@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(value = "业务模块-业务需求详情类")
public class Demand extends Entity {
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
    @ApiModelProperty(value = "需求期限")
    private Integer duration;
    @ApiModelProperty(value = "需求阶段: SUBMIT, REVIEW, IMPLEMENT, COMPLETED")
    @Builder.Default
    private Phase phase = Phase.ON_DEVELOP;
    private Integer scope;

    String phaseName;

    Integer contractId;

    String contractCategoryId;
    String contractSampleUrl;
    String reason;

    @ApiModelProperty(value = "测评说明")
    private String evaluation;
    String modelUuid;
    @Override
    public void setType(Type type) {
        this.type = Type.DEMAND;
    }

    @Override
    public Type getType() {
        this.type = Type.DEMAND;
        return this.type;
    }

    public Task toDefaultTask() {
        Task.Status status = null;
        switch (this.getPhase()) {
            case COMPLETED:
                status = Task.Status.COMPLETED;
                break;
            case TEST_PASS:
                status = Task.Status.IMPLEMENT;
                break;
            case WAIT_AUDIT:
            case AUDIT_PASS:
            case ON_DEVELOP:
            default:
                status = Task.Status.REVIEW;
        }

        return Task.builder()
                .uuid(this.getUuid())
                .name(this.getName())
                .intro(this.getIntro())
                .desc(this.getDesc())
                .nameEn(this.getNameEn())
                .introEn(this.getIntroEn())
                .descEn(this.getDescEn())
                .image(this.getImage())
                .budget(this.getBudget())
                .budgetValue(this.getBudgetValue())
                .startDate(this.getStartDate())
                .deliveryDate(this.getDeliveryDate())
                .status(status)
                .demandId(this.getId())
                .demandUuid(this.getUuid())
                .group(this.getGroup())
                .build();
    }

    @Getter
    @AllArgsConstructor
    public enum Phase {
//        UNKNOWN(0, "未知", "默认非公开阶段(草稿)"),
        WAIT_AUDIT(10, "待评审", "需求发布，待评审"),
        AUDIT_DECLINE(12, "评审拒绝", ""),
        AUDIT_PASS(20, "待领取", "评审通过，任务待领取"),
        ON_DEVELOP(30, "开发中", "任务已领取，开发中"),
        WAIT_TEST(40, "模型待测试", "模型已上线，客户待测试"),
        TEST_PASS(50, "签约中", "测试通过，签约及交付中"),
        HAS_SIGN_CONTRACT(60, "已签约", "合同已签约"),
        COMPLETED(90, "已完成", "交付完成"),
        CANCEL(100,"已取消","已取消");

        private final Integer val;
        private final String name;
        private final String desc;

        public static Phase valueOf(Integer val) {

            for (Phase value : values()) {
                if (value.getVal().equals(val)){
                    return value;
                }
            }
            return null;

        }

        public static Phase valueOf2(String val) {

            for (Phase value : values()) {
                if (value.toString().equalsIgnoreCase(val)){
                    return value;
                }
            }
            return null;
        }
    }


}

