package com.tigerobo.x.pai.dal.biz.entity.eye;

import lombok.Data;

@Data
public class CallModelUserPo {

    private String userAccount;
    private Integer userId;
    private String userName;
    private Integer num;
    private String mobile;
    private String wechatName;
    private String groupUuid;
    private String groupName;
    int admin;
}
