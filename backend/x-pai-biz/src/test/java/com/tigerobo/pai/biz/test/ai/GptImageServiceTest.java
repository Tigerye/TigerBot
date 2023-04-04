package com.tigerobo.pai.biz.test.ai;

import com.alibaba.fastjson.JSON;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.biz.ai.gpt.ChatImageService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class GptImageServiceTest extends BaseTest {

    @Autowired
    private ChatImageService gptImageService;

    @Test
    public void imgTest(){

        String text = "瀑布";
        final long start = System.currentTimeMillis();
        final List<String> list = gptImageService.produceImages(text);
        final long end = System.currentTimeMillis();
        System.out.println("delta:"+(end-start));
        System.out.println(JSON.toJSONString(list));

    }
}
