package com.tigerobo.x.pai.api.aml.dto.evaluate;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class EvaSentence {
    @ExcelProperty("内容")
    private String sentence;
    @ExcelProperty("得分")
    private BigDecimal score;
}
