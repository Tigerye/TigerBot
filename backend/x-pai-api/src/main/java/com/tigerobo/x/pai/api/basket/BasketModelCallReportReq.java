package com.tigerobo.x.pai.api.basket;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class BasketModelCallReportReq {

    String accessToken;
    String apiKey;

    Integer apiNum;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date startCallTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date endCallTime;

    String memo;


}
