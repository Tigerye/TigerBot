package com.tigerobo.x.pai.biz.serving.evaluate.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description:
 * @modified By:
 * @version: $
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TextRowItem {
    @ExcelProperty(value = "文本", index = 0)
    private String text;
    @ExcelProperty(value = "结果", index = 1)
    private String resultText;
    @ExcelProperty(value = "json结果", index = 2)
    private String resultJson;
    @ExcelIgnore
    private Object result;
}