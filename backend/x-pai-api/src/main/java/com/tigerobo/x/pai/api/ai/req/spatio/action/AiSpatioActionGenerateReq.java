package com.tigerobo.x.pai.api.ai.req.spatio.action;

import lombok.Data;

@Data
public class AiSpatioActionGenerateReq {

    Integer userId;
    Long reqId;

    String inputVideo;

    String apiKey;
}
