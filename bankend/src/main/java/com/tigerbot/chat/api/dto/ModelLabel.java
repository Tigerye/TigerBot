package com.tigerbot.chat.api.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ModelLabel {

    String label;
    BigDecimal score;
}
