package com.tigerobo.x.pai.api.dto.lake;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LakeLabelDto {

    String label;
    BigDecimal score;
}
