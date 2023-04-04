package com.tigerobo.x.pai.service.controller.ad;

import com.tigerobo.x.pai.api.dto.ad.AdBannerDto;
import com.tigerobo.x.pai.biz.ad.BannerService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(path = "ad/banner/")
public class AdBannerController {

    @Autowired
    private BannerService bannerService;
    @ApiOperation(value = "banner列表")
    @RequestMapping(value = "/getBanners", method = POST)
    public List<AdBannerDto> getBanners(HttpServletRequest request) {
        return bannerService.getBanners();
    }

}
