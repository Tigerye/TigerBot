package com.tigerobo.x.pai.api.enums;

import lombok.Getter;

@Getter
public enum  UserRoleTypeEnum {
    SUPER_MAN(1,"核心权限"),
    AML(2,"自主训练"),
    ;
    private Integer bit;
    private String text;
    UserRoleTypeEnum(Integer bit,String text){
        this.bit =bit;
        this.text = text;
    }

    public static UserRoleTypeEnum getByType(Integer type){
        if (type == null){
            return null;
        }
        for (UserRoleTypeEnum value : values()) {
            Integer bit = value.getBit();
//            if (value.getType().equals(type)){
//                return value;
//            }
        }
        return null;

    }

    public static boolean hasRole(int type ,UserRoleTypeEnum roleTypeEnum){
        int i = type & roleTypeEnum.getBit();
        return i>0;
    }

    public static void main(String[] args) {
        System.out.println(hasRole(1,UserRoleTypeEnum.SUPER_MAN));
    }
}
