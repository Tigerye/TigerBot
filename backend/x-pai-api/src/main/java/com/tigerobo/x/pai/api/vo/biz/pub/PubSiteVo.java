package com.tigerobo.x.pai.api.vo.biz.pub;

import lombok.Data;

@Data
public class PubSiteVo {
    Integer id;
    String logoOss;
    String name;
    String intro;
    boolean follow;
    String role;
    Integer vip;
}
