package com.tigerobo.x.pai.api.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class UserDto {

    protected Integer id;


    protected String uuid;

    protected String name;
    protected String intro;
    protected String desc;
    //    protected String nameEn;
//    protected String introEn;
//    protected String descEn;
//    protected String image;

    private String account;
    private String mobile;
    private String avatar;
    private String wechat;
    //    private String email;
    private String website;

    private Integer roleType;

    private String areaCode;
    private String wechatName;
    String amlUrl;
    String amlArea;
    /**
     * 来源
     */
    String accessSource;
    String ip;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected Date createTime;
    protected Boolean isDeleted;

    Boolean isBlackUser;
}
