package com.tigerobo.x.pai.api.pay.enums;

import lombok.Getter;

@Getter
public enum  OrderStatusEnum {

    WAIT_PAY(1,"待支付"),
    HAS_PAY(2,"已支付"),
    PAY_FAIL(3,"支付失败"),
    CANCEL(5,"已取消")
    ;

    Integer status;
    String name;

    OrderStatusEnum(Integer status,String name){
        this.status = status;
        this.name = name;
    }

    public static OrderStatusEnum getByStatus(Integer status){

        if (status == null){
            return null;
        }
        for (OrderStatusEnum value : values()) {
            if (value.getStatus().equals(status)){
                return value;
            }
        }
        return null;
    }
}
