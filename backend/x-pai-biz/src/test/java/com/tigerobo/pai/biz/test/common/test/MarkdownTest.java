package com.tigerobo.pai.biz.test.common.test;

import com.alibaba.fastjson.JSON;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class MarkdownTest {

    @Test
    public void curlMdTest(){

        String text ="curl --location --request POST 'https://pai-test.tigerobo.com/x-pai-serving/invoke?appId=94b351225d793977cc93897771e35209&apiKey=da14ff281ab1ddf6490987d349cbbcf3&accessToken=63ef4e419008169c95256bc5c9883861' \\\n     --header 'Content-Type: application/json' \\\n     --data-raw '{\"text\":\"工信部2021年1月2日发布工业互联网创新发展行动计划。\"}'"
;

        System.out.println(text);

        Map<String,Object> map = new HashMap<>();
        map.put("text","工信部2021年1月2日发布工业互联网创新发展行动计划。");

        StringBuilder builder = new StringBuilder();
        builder.append("curl --location --request POST '");
        String uri = "https://pai-test.tigerobo.com/x-pai-serving/invoke?appId=94b351225d793977cc93897771e35209&apiKey=da14ff281ab1ddf6490987d349cbbcf3&accessToken=63ef4e419008169c95256bc5c9883861";
        builder.append(uri);
        builder.append("' \\\n");
        builder.append("--header 'Content-Type: application/json' \\\n");
        builder.append("--data-raw '");

        builder.append(JSON.toJSONString(map));

        builder.append("'");

        System.out.println(builder.toString());
    }

    @Test
    public void pythonMdTest(){

        StringBuilder builder = new StringBuilder();

        builder.append("import requests").append("\n").append("\n");

        String uri = "https://pai-test.tigerobo.com/x-pai-serving/invoke?appId=94b351225d793977cc93897771e35209&apiKey=da14ff281ab1ddf6490987d349cbbcf3&accessToken=63ef4e419008169c95256bc5c9883861";

        builder.append("uri = \"").append(uri).append("\"\n");
        String input = "{\"text_list\": [\"你这人脑子指定有什么毛病\"]}";

        builder.append("input = ").append(input).append("\n\n");

        builder.append("output = requests.post(url, json=input).json()").append("\n");

        System.out.println(builder.toString());

    }
}
