package com.tigerobo.pai.admin.controller;

import com.tigerobo.x.pai.api.admin.auth.AdminAuthorize;
import com.tigerobo.x.pai.api.dto.ad.AdBannerDto;
import com.tigerobo.x.pai.api.vo.IdVo;
import com.tigerobo.x.pai.api.vo.ResultVO;
import com.tigerobo.x.pai.biz.admin.ad.BannerAdminService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "ad/banner/")
public class AdBannerAdminController {

    @Autowired
    private BannerAdminService bannerAdminService;
    @AdminAuthorize
    @ApiOperation(value = "banner列表")
    @PostMapping(path = {"list"}, consumes = "application/json", produces = "application/json")
    public List<AdBannerDto> getList(){
        return bannerAdminService.getList();
    }

    @AdminAuthorize
    @ApiOperation(value = "新增banner")
    @PostMapping(path = {"addBanner"}, consumes = "application/json", produces = "application/json")
    public ResultVO addBanner(@RequestBody AdBannerDto dto){
        bannerAdminService.add(dto);

        return ResultVO.success();
    }


    @AdminAuthorize
    @ApiOperation(value = "更新banner")
    @PostMapping(path = {"updateBanner"}, consumes = "application/json", produces = "application/json")
    public ResultVO updateBanner(@RequestBody AdBannerDto dto){
        bannerAdminService.update(dto);

        return ResultVO.success();
    }


    @AdminAuthorize
    @ApiOperation(value = "删除banner")
    @PostMapping(path = {"deleteBanner"}, consumes = "application/json", produces = "application/json")
    public ResultVO deleteBanner(@RequestBody IdVo idVo){
        bannerAdminService.delete(idVo);

        return ResultVO.success();
    }
}
