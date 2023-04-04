package com.tigerobo.x.pai.api.biz.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.Maps;
import com.tigerobo.x.pai.api.auth.entity.Group;
import com.tigerobo.x.pai.api.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description: 业务模块-算法模型详情类
 * @modified By:
 * @version: $
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(value = "业务模块-算法模型详情类")
public class Model {


    protected Integer id;
    protected String uuid;

    protected String name;
//    protected String nameEn;
    protected String intro;
//    protected String introEn;
    protected String desc;
//    protected String descEn;
    protected String image = null;

    protected String coverType = "img";

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected Date updateTime;
    protected Boolean isDeleted = false;

    protected String createBy;
    //    @JsonSerialize
    protected Group group;

    private String groupUuid;


    //    @ApiModelProperty(value = "受限等级")
//    private Limited limited = Limited.UNRESTRICTED_PUBLIC;
//    @ApiModelProperty(value = "模型仓库地址：包括branch,Tag")
//    private String repoAddr;
    @ApiModelProperty(value = "API调用地址")
    private String apiUri;
    @ApiModelProperty(value = "演示样式")
    private String style;
//    @ApiModelProperty(value = "状态: PREPARE, SUBMIT, COMPLETED")
//    private Status status = Status.PREPARE;
//    @ApiModelProperty(value = "模型隶属")
//    private Subject subject = Subject.UNKNOWN;

//
//    @Getter
//    @AllArgsConstructor
//    public enum Status {
//        UNKNOWN(0, "未知", "默认非公开阶段(草稿)"),
//        PREPARE(10, "预备", "创建模型"),
//        SUBMIT(20, "提交", "提交结果"),
//        ACCEPTANCE(70, "验收", "模型验收"),
//        SETTLEMENT(80, "结算", "交易结算"),
//        COMPLETED(90, "完成", "模型完成");
//
//        private final Integer val;
//        private final String name;
//        private final String desc;
//
//        public static Status valueOf(int val) {
//            switch (val) {
//                case 10:
//                    return PREPARE;
//                case 20:
//                    return SUBMIT;
//                case 70:
//                    return ACCEPTANCE;
//                case 80:
//                    return SETTLEMENT;
//                case 90:
//                    return COMPLETED;
//                case 0:
//                default:
//                    return UNKNOWN;
//            }
//        }
//    }

}
