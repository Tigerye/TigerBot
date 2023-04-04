package com.tigerobo.pai.biz.test.ai;

import com.alibaba.fastjson.JSON;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.ai.vo.AiArtImageVo;
import com.tigerobo.x.pai.biz.ai.art.image.ArtImageAuditPassService;
import com.tigerobo.x.pai.biz.ai.art.image.ArtImageService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ArtImageTest extends BaseTest {

    @Autowired
    private ArtImageAuditPassService artImageAuditPassService;

    @Autowired
    private ArtImageService artImageService;


    @Test
    public void reqDetailTest(){

        ThreadLocalHolder.setUserId(3);
        final AiArtImageVo vo = artImageService.getById(975);


        System.out.println(JSON.toJSONString(vo));
    }


    @Test
    public void getHistoryImageTest(){

        ThreadLocalHolder.setUserId(18);

        final List<String> historyImages = artImageService.getHistoryImages();



        System.out.println(JSON.toJSONString(historyImages));
    }
    @Test
    public void test(){

        String text = "黑丝 动漫 萝莉";
        final boolean prePass = artImageAuditPassService.isPrePass(text);

        artImageAuditPassService.addWord(text);
        final boolean isPass = artImageAuditPassService.isPrePass(text);
        System.out.println(prePass +"," +isPass);
    }
}
