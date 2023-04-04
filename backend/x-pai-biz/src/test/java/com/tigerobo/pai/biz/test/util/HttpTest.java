package com.tigerobo.pai.biz.test.util;

import com.alibaba.fastjson.JSON;
import com.tigerobo.x.pai.biz.utils.http.HttpReqUtil;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HttpTest {

    @Test
    public void urlTest(){


        String url = "http://gbox6.aigauss.com:9207/infer";
        Map<String,Object> data = new HashMap<>();

        String text = "南非的首都在哪里";
        data.put("text_list", Arrays.asList(text));
//        data.put("")


        final String s = HttpReqUtil.reqPost(url, JSON.toJSONString(data), 40*1000, "chatGpt-error");

        final Object result = JSON.parseObject(s).getJSONArray("result").get(0);
        System.out.println(result);
    }
}
