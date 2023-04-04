package com.tigerobo.x.pai.api.ai.req.photo;

import lombok.Data;

@Data
public class PhotoFixReq {

    Long reqId;
    Integer userId;
    String apiKey;
    String inferUrl;
    String inputPhotoUrl;

    boolean appendColor;

}
