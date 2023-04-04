package com.tigerobo.x.pai.api.vo.biz.pub;

import lombok.Data;

@Data
public class PubBigShotVo {
    Integer id;
    String name;
    String logo;
    String platformName = "twitter";
    boolean follow;

    String intro;
    private String alias;

    Integer vip;
}
