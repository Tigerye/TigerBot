package com.tigerobo.x.pai.api.biz.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
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
 * @description:
 * @modified By:
 * @version: $
 */
@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(value = "业务模块-任务数据集")
public class Dataset{


    protected Integer id;
    protected String uuid;

    protected String type ="DATASET";

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



    @ApiModelProperty(value = "文件存储路径")
    private String filePath;
    @ApiModelProperty(value = "数据集格式: 默认txt")
    private Format format = Format.TXT;
    @ApiModelProperty(value = "数据集类型(非数据库字段)")
    private Scene scene = null;


    @Getter
    @AllArgsConstructor
    public enum Format {
        OTHERS("OTHERS"),
        TXT("TXT"),
        CSV("CSV"),
        TSV("TSV"),
        EXCEL("EXCEL");

        private final String val;

        public static Format valueOf2(String val) {
            if (val == null)
                return OTHERS;
            switch (val.toLowerCase()) {
                case "txt":
                    return TXT;
                case "csv":
                    return CSV;
                case "tsv":
                    return TSV;
                case "excel":
                    return EXCEL;
                default:
                    return OTHERS;
            }
        }
    }

    @Getter
    @AllArgsConstructor
    public enum Scene {
        TRAIN(10, "训练"),
        VALIDATION(20, "验证"),
        TEST(30, "测试"),
        OTHER(90, "其他");

        private final Integer val;
        private final String name;

        public static Scene valueOf(int val) {
            switch (val) {
                case 10:
                    return TRAIN;
                case 20:
                    return VALIDATION;
                case 30:
                    return TEST;
                case 90:

                default:
                    return OTHER;
            }
        }
    }
}
