package com.tigerobo.x.pai.api.uc.dto;

import lombok.Data;

@Data
public class ChangeMobileDto {

    private String area;
    private String mobile;
    private String code;

    private String changeToken;
}
