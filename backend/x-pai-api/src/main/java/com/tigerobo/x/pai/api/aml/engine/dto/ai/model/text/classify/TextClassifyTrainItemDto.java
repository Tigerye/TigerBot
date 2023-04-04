package com.tigerobo.x.pai.api.aml.engine.dto.ai.model.text.classify;

import com.tigerobo.x.pai.api.aml.engine.dto.train.AmlTrainItemDto;
import lombok.Data;

@Data
public class TextClassifyTrainItemDto extends AmlTrainItemDto {
    protected String sentence1;
    protected String label;
}
