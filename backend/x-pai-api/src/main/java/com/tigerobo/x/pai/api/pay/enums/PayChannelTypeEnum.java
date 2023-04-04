package com.tigerobo.x.pai.api.pay.enums;

import lombok.Getter;

@Getter
public enum PayChannelTypeEnum {
    NOT_SET(0, "没设置"),
    WECHAT_SCAN(1, "微信二维码"),
    WECHAT_H5(2, "微信H5"),
    WECHAT_APP(3, "微信app"),
    ALIPAY_SCAN(4, "支付宝二维码"),
    ALIPAY_H5(5, "支付宝H5"),
    ALIPAY_APP(6, "支付宝app"),
    APPLY_PAY(7, "苹果"),
    WECHAT_PUB(8, "微信公众号");;

    Integer type;
    String name;

    PayChannelTypeEnum(Integer type, String name) {
        this.type = type;
        this.name = name;
    }

    public static PayChannelTypeEnum getByType(Integer type) {

        if (type == null) {
            return null;
        }

        for (PayChannelTypeEnum value : values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }
        return null;

    }
}
