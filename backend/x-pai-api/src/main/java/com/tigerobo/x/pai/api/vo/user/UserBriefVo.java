package com.tigerobo.x.pai.api.vo.user;

import com.tigerobo.x.pai.api.auth.entity.Role;
import lombok.Data;

@Data
public class UserBriefVo {

    Integer id;
//    String uuid;
    String name;
    String avatar;

//    String groupId;
//    String groupName;
    //用于跳转
    String account;

    Role role;
}
