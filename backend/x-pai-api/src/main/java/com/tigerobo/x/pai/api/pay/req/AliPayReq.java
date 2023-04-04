package com.tigerobo.x.pai.api.pay.req;

import lombok.Data;

@Data
public class AliPayReq {
    Long orderNo;
    String callbackUrl;
}
