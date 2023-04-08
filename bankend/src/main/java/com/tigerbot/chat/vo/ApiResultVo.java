package com.tigerbot.chat.vo;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.List;

@Data
public class ApiResultVo {
    private Integer status;
    private String msg;
    private Object result;
    private JSONObject appendInfo = new JSONObject();
}