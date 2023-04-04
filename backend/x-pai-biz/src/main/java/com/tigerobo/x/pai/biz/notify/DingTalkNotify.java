package com.tigerobo.x.pai.biz.notify;

import com.google.common.collect.ImmutableMap;
import com.tigerobo.x.pai.biz.utils.JacksonUtil;
import com.tigerobo.x.pai.biz.utils.http.HttpReqUtil;
import org.springframework.util.StringUtils;

import java.util.Map;

public class DingTalkNotify {

    private static final String algUrl = "https://oapi.dingtalk.com/robot/send?access_token=1797ee8b6f572d2611e2ee5d6c8173bda36f010f8a667e98f58dd8d79bcadc83";

    public static void notice(String message){
        noticeAlgolet(message+",algolet");
    }
    public static boolean noticeAlgolet(String message){

        Map<String,Object> map = ImmutableMap.<String,Object>builder()
                .put("msgtype","text")
                .put("text",ImmutableMap.builder().put("content",message).build())
                .put("at",ImmutableMap.builder().put("isAtAll",false).build())
                .build();
        String res = HttpReqUtil.reqPost(algUrl, JacksonUtil.bean2Json(map),5000);
        if (!StringUtils.isEmpty(res)&&res.contains("ok")){
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        noticeAlgolet("我是algolet机器人");
    }


}
