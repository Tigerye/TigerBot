package com.tigerobo.pai.biz.test.ad;

import com.alibaba.fastjson.JSON;

import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.dto.ad.AdBannerDto;
import com.tigerobo.x.pai.biz.ad.BannerService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class AdBannerServiceTest extends BaseTest {
    @Autowired
    private BannerService bannerService;


    @Test
    public void listTest(){

        List<AdBannerDto> banners = bannerService.getBanners();

        System.out.println(JSON.toJSONString(banners));
    }
}
