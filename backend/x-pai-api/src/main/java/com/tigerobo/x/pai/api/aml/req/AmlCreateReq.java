package com.tigerobo.x.pai.api.aml.req;

import lombok.Data;

@Data
public class AmlCreateReq {
    private String name;
    private Integer baseModelId;
}
