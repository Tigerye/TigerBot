package com.tigerobo.x.pai.api.ai.req.style.transfer;

import lombok.Data;

import java.util.List;

@Data
public class AiStyleTransferGenerateReq {

    Integer userId;
    Long reqId;

    String contentImage;

    String styleImage;

    Integer styleImageId;

    String apiKey;

    Integer width;
    Integer height;

//    Integer preserveColor;
    Integer totalProgress;
}
