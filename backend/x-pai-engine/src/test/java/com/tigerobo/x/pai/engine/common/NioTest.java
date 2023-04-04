package com.tigerobo.x.pai.engine.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.Map;

public class NioTest {

    @Test
    public void test(){
        Paths.get("");
    }

    @Test
    public void charTest(){

        String text = "\u9ad8\u7ba1\u8d1f\u9762";
        System.out.println(text);
    }

    @Test
    public void jsonTest(){
        String json = "{\n" +
                "    \"\\u4e0a\\u5e02\\u9000\\u5e02\": 0,\n" +
                "    \"\\u4e1a\\u52a1\\u53d8\\u52a8\": 1,\n" +
                "    \"\\u4e8f\\u635f\\u76c8\\u5229\": 2,\n" +
                "    \"\\u4ea7\\u54c1\\u4fe1\\u606f\": 3,\n" +
                "    \"\\u4ea7\\u54c1\\u8d28\\u91cf\": 4,\n" +
                "    \"\\u4fb5\\u6743\\u6284\\u88ad\": 5,\n" +
                "    \"\\u4fe1\\u7528\\u4fe1\\u8a89\": 6,\n" +
                "    \"\\u503a\\u52a1\\u62b5\\u62bc\": 7,\n" +
                "    \"\\u505c\\u4e1a\\u6682\\u505c\": 8,\n" +
                "    \"\\u505c\\u4e1a\\u7834\\u4ea7\": 9,\n" +
                "    \"\\u5077\\u7a0e\\u6f0f\\u7a0e\": 10,\n" +
                "    \"\\u516c\\u53f8\\u4fe1\\u606f\": 11,\n" +
                "    \"\\u5176\\u5b83\\u516c\\u544a\": 12,\n" +
                "    \"\\u52b3\\u52a1\\u7ea0\\u7eb7\": 13,\n" +
                "    \"\\u5408\\u4f5c\\u7ecf\\u8425\": 14,\n" +
                "    \"\\u5408\\u540c\\u7ea0\\u7eb7\": 15,\n" +
                "    \"\\u5458\\u5de5\\u60c5\\u51b5\": 16,\n" +
                "    \"\\u5784\\u65ad\\u4fe1\\u606f\": 17,\n" +
                "    \"\\u589e\\u6301\\u51cf\\u6301\": 18,\n" +
                "    \"\\u5b89\\u5168\\u4e8b\\u4ef6\": 19,\n" +
                "    \"\\u6210\\u679c\\u5956\\u9879\": 20,\n" +
                "    \"\\u6295\\u8d44\\u878d\\u8d44\": 21,\n" +
                "    \"\\u6536\\u8d2d\\u91cd\\u7ec4\": 22,\n" +
                "    \"\\u65b0\\u54c1\\u5347\\u7ea7\": 23,\n" +
                "    \"\\u6d89\\u8bc9\\u8fdd\\u89c4\": 24,\n" +
                "    \"\\u73af\\u5883\\u4fdd\\u62a4\": 25,\n" +
                "    \"\\u751f\\u4ea7\\u8bbe\\u5907\": 26,\n" +
                "    \"\\u76f8\\u5173\\u63d0\\u53ca\": 27,\n" +
                "    \"\\u7834\\u4ea7\\u6e05\\u7b97\": 28,\n" +
                "    \"\\u7ecf\\u8425\\u4e1a\\u52a1\": 29,\n" +
                "    \"\\u80a1\\u4ef7\\u53d8\\u52a8\": 30,\n" +
                "    \"\\u80a1\\u6743\\u53d8\\u52a8\": 31,\n" +
                "    \"\\u9020\\u5047\\u6b3a\\u8bc8\": 32,\n" +
                "    \"\\u91cd\\u5927\\u4ea4\\u6613\": 33,\n" +
                "    \"\\u9ad8\\u7ba1\\u53d8\\u52a8\": 34,\n" +
                "    \"\\u9ad8\\u7ba1\\u8d1f\\u9762\": 35\n" +
                "  }";

        JSONObject jsonObject = JSON.parseObject(json);

        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            String key = entry.getKey();
            System.out.println(key);
        }

    }

}
