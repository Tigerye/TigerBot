package com.tigerobo.x.pai.api.dto.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ModelLabel {

    String label;
    BigDecimal score;
}
