package com.tigerobo.x.pai.api.aml.dto;

import com.tigerobo.x.pai.api.enums.Style;
import lombok.Data;

@Data
public class AmlBaseModelDto {
    private Integer id;
    private String name;
    private String intro;
    private String img;
    /**
     * @see Style
     *  */
    private String style;
}
