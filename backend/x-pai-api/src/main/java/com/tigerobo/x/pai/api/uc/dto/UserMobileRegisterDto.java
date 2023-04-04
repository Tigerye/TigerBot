package com.tigerobo.x.pai.api.uc.dto;

import lombok.Data;

@Data
public class UserMobileRegisterDto {
    private String area;
    private String mobile;
    private String code;
    private String password;
    private String nickName;

    /**
     * 微信登录使用UcUserServiceImpl
     */
    private String wechatCode;
    private String appId;

    String accessSource;

    WechatInfo wechatInfo;
    @Data
    public static class WechatInfo{
        String nickName;
        String avatarUrl;
        String gender;
        String country;
        String province;
        String city;
        String language;

    }
}
