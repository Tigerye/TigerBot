package com.tigerobo.x.pai.api.uc.dto;

import lombok.Data;

@Data
public class ConfirmCodeDto {
    String area;
    String mobile;
    String code;
    String identityCode;
}
