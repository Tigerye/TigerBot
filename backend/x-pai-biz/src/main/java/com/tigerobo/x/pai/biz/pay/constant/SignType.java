package com.tigerobo.x.pai.biz.pay.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: xjx
 * @date: 2020/5/9 4:57 PM
 */
@AllArgsConstructor
public enum SignType {

    MD5("MD5"),
    HMACSHA256("HMAC-SHA256");

    @Getter
    private String code;

    public static SignType getSignType(String code) {
        SignType[] signTypes = values();
        for (SignType signType : signTypes) {
            if (signType.getCode().equals(code)) {
                return signType;
            }
        }

        throw new IllegalArgumentException("unknow SignType code " + code);
    }
}
