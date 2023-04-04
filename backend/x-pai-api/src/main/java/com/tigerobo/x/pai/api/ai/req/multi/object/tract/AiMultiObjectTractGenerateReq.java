package com.tigerobo.x.pai.api.ai.req.multi.object.tract;

import lombok.Data;

@Data
public class AiMultiObjectTractGenerateReq {

    Integer userId;
    Long reqId;

    String inputVideo;

    String apiKey;
}
