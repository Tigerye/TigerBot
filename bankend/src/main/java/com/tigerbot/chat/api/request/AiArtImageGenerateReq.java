package com.tigerbot.chat.api.request;

import lombok.Data;

import java.util.List;

@Data
public class AiArtImageGenerateReq {

    String styleType;

    List<ArtImageParams> inputParam;

    Integer sizeId;
    Integer width;
    Integer height;

    //消耗积分
    Integer coinTotal = 1;
//    @ApiModelProperty(value = "图片权重")
    Float imageStrength;
//    @ApiModelProperty(value = "输入文本权重,默认值为7， 范围集合为 [1, 20]" +
//            "（较高的值使生成的图像更接近你的输入的文本，值过高会“过度生成”图像）")
    Float promptWeight;

//    @ApiModelProperty(value = "图片生成步骤数")
    Integer steps;

//    @ApiModelProperty(value = "生成轮次")
    Integer nIter;
//    @ApiModelProperty(value = "图片生成步骤数")
    Integer seed;
//    @ApiModelProperty(value = "模型版本")
    String modelVersion;
    @Data
    public static class ArtImageParams{
        String text;
        List<String> modifiers;
        Float weight;
    }
}
