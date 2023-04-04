package com.tigerobo.x.pai.service.controller.search;

import com.tigerobo.pai.search.api.dto.click.ClickDto;
import com.tigerobo.pai.search.api.dto.click.ClickNumDto;
import com.tigerobo.pai.search.api.req.base.IdUserReq;

import com.tigerobo.x.pai.biz.micro.search.IndexClickService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/indexClickData/")
@Api(value = "搜索统计数据", position = 1)
public class IndexClickDataController {


    @Autowired
    private IndexClickService indexClickService;

    @ApiOperation(value = "应用点击总数", position = 2)
    @PostMapping(path = "/getAppClickTotal", consumes = "application/json", produces = "application/json")
    public ClickDto getAppClickTotal(@RequestBody IdUserReq req) {
        return indexClickService.getAppClickTotal(req);
    }
    @ApiOperation(value = "索引点击总数", position = 2)
    @PostMapping(path = "/getIndexClickTotal", consumes = "application/json", produces = "application/json")
    public ClickDto getIndexClickTotal(@RequestBody IdUserReq req) {
        return indexClickService.getIndexClickTotal(req);
    }



    @ApiOperation(value = "索引趋势列表", position = 2)
    @PostMapping(path = "/getIndexClickTrend", consumes = "application/json", produces = "application/json")
    public List<ClickNumDto> getIndexClickTrend(@RequestBody IdUserReq req) {
        return indexClickService.getIndexClickTrend(req);
    }
    @ApiOperation(value = "应用趋势列表", position = 2)
    @PostMapping(path = "/getAppClickTrend", consumes = "application/json", produces = "application/json")
    public List<ClickNumDto> getAppClickTrend(@RequestBody IdUserReq req) {
        return indexClickService.getAppClickTrend(req);
    }

}
