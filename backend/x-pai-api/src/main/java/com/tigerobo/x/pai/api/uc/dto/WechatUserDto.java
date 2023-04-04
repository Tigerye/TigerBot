package com.tigerobo.x.pai.api.uc.dto;

import lombok.Data;

@Data
public class WechatUserDto {

    String openId;
    String unionId;
    String nickName;
    String headImgUrl;
    String sex;
    String city;
    String province;
    String country;
    String language;
    String privilege;
    String refreshToken;
    Boolean subscribe;
    //小程序code返回
    private String sessionKey;
}
