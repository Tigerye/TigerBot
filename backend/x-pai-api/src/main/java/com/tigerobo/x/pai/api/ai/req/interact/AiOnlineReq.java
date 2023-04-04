package com.tigerobo.x.pai.api.ai.req.interact;

import com.tigerobo.x.pai.api.utils.Mapable;
import lombok.Data;

@Data
public class AiOnlineReq implements Mapable<String, Object> {
    String id;
    Integer bizType;

}
