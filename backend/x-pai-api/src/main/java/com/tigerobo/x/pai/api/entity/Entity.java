package com.tigerobo.x.pai.api.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.base.Objects;
import com.tigerobo.x.pai.api.auth.entity.Group;
import io.swagger.annotations.ApiModel;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;
//
//import static org.springframework.data.elasticsearch.annotations.FieldType.Long;
//import static org.springframework.data.elasticsearch.annotations.FieldType.Object;
//import static org.springframework.data.elasticsearch.annotations.FieldType.*;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description:
 * @modified By:
 * @version: $
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "业务模块-实体基础类")
public class Entity {

    protected Integer id;
    protected String uuid;
    @Builder.Default
    protected Type type = Type.UNKNOWN;

    protected String name;
    protected String nameEn;
    protected String intro;
    protected String introEn;
    protected String desc;
    protected String descEn;
    protected String image = null;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected Date updateTime;
    protected Boolean isDeleted = false;

    protected String createBy;
    //    @JsonSerialize
    protected Group group;

    private String groupUuid;


    @Getter
    @AllArgsConstructor
    public enum Type {
        UNKNOWN(0, "未知", Module.UNKNOWN),
        // AUTH
        USER(1010, "用户", Module.AUTH),
        GROUP(1020, "用户组", Module.AUTH),


        // BIZ
        DEMAND(2010, "需求", Module.BIZ),
        TASK(2020, "任务", Module.BIZ),
        MODEL(2030, "模型", Module.BIZ),
//        BASE_MODEL(2031, "基础模型", Module.BIZ),
        DATASET(2040, "数据集", Module.BIZ),
//        SUBMISSION(2050, "提交", Module.BIZ),
//        APPLICATION(2090, "应用", Module.BIZ),
        TAG(2100, "标签", Module.BIZ),
//        INDEX(2200, "指标", Module.BIZ),
        // SERVING
        API(3010, "API", Module.SERVING),

        // REPO
        // ENGINE
//        TEMPLATE(5010, "模板", Module.ENGINE);
        ;
        private final Integer val;
        private final String name;
        private final Module module;

    }

}
