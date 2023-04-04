package com.tigerobo.x.pai.biz.dto;

import com.tigerobo.x.pai.biz.serving.evaluate.entity.TextRowItem;
import lombok.Data;

import java.util.List;

@Data
public class BatchCallDto {

    String filePath;
    String style;
    List<TextRowItem> pivot;

    Integer dealTime;
    Integer dealNum;
}
