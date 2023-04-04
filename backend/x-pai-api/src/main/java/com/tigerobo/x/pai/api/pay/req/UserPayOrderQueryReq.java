package com.tigerobo.x.pai.api.pay.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tigerobo.x.pai.api.vo.biz.PageReqVo;
import lombok.Data;

import java.util.Date;

@Data
public class UserPayOrderQueryReq extends PageReqVo {


    String keyword;
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    Date startPayTime;
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    Date endPayTime;

}
