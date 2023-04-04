package com.tigerobo.x.pai.api.aml.dto.scene;

import com.tigerobo.x.pai.api.vo.RequestVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AmlCreateDto extends RequestVo {
    private String name;
    private Integer baseModelId;
    private String createBy;

    @ApiModelProperty(value = "0-全量训练，1-增量训练")
    private Integer trainType;
    @ApiModelProperty(value = "父级modelId")
    private Integer parentModelId;
}
