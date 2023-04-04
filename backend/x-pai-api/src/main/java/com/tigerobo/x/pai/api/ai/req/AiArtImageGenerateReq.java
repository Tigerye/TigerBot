package com.tigerobo.x.pai.api.ai.req;

import com.tigerobo.x.pai.api.enums.ArtImageType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class AiArtImageGenerateReq {

    @ApiModelProperty(value = "stable,disco")
    String styleType = ArtImageType.STABLE.toString();

    Integer userId;
//    Long reqId;

//
//    String text;
//    List<String> modifiers;

    List<ArtImageParams> inputParam;

    String image;


//    String apiKey;

//    String outImageSize;
//
    Integer sizeId;
    Integer width;
    Integer height;
//
//    Integer totalProgress;

    Integer coinTotal = 0;

    boolean useFree;


    @ApiModelProperty(value = "图片权重")
    Float imageStrength;
    @ApiModelProperty(value = "输入文本权重,默认值为7， 范围集合为 [1, 20]" +
            "（较高的值使生成的图像更接近你的输入的文本，值过高会“过度生成”图像）")
    Float promptWeight;

    @ApiModelProperty(value = "图片生成步骤数")
    Integer steps;

    @ApiModelProperty(value = "生成轮次")
    Integer nIter;
    @ApiModelProperty(value = "图片生成步骤数")
    Integer seed;
    @ApiModelProperty(value = "模型版本")
    String modelVersion;
    @Data
    public static class ArtImageParams{
        String text;
        List<String> modifiers;
        Float weight;
    }
}
