package com.tigerobo.x.pai.api.vo;


import com.tigerobo.x.pai.api.enums.BusinessEnum;

public interface IBusinessDetailVo {
    default Integer getUserId(){return null;};

    int getBizType();

    default String getBizName(){
        final int bizType = getBizType();
        final BusinessEnum businessEnum = BusinessEnum.getByType(bizType);
        return businessEnum == null?"":businessEnum.getText();
    }
}
