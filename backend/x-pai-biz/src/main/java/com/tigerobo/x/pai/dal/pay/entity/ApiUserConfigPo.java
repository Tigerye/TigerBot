package com.tigerobo.x.pai.dal.pay.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "api_user_config")
public class ApiUserConfigPo {

    @Id
    Integer id;
    Integer userId;
    Integer warnType;
    Boolean checkQuota;
    Boolean stopCall;
    Boolean quotaOver;
    Boolean whiteUser;
    Boolean isDeleted;
}
