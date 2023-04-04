package com.tigerobo.x.pai.service.controller.search;


import com.tigerobo.pai.search.api.dto.app.AppSceneIndexDto;
import com.tigerobo.pai.search.api.req.application.AppSceneIndexAddReq;
import com.tigerobo.pai.search.api.req.base.IdUserReq;

import com.tigerobo.x.pai.biz.micro.search.AppSceneIndexService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/appScene/index/")
@Api(value = "搜索应用场景下索引配置", position = 2)
public class AppSceneIndexController {

    @Autowired
    private AppSceneIndexService appSceneIndexService;
    @ApiOperation(value = "新增*修改", position = 2)
    @PostMapping(path = "/add", consumes = "application/json", produces = "application/json")
    public void add(@RequestBody AppSceneIndexAddReq req) {
        appSceneIndexService.add(req);
    }
    @ApiOperation(value = "删除", position = 3)
    @PostMapping(path = "/delete", consumes = "application/json", produces = "application/json")
    public void delete(@RequestBody IdUserReq req) {
        appSceneIndexService.delete(req);
    }
    @ApiOperation(value = "获取场景下索引列表", position = 4)
    @PostMapping(path = "/getSceneIndexList", consumes = "application/json", produces = "application/json")
    public List<AppSceneIndexDto> getSceneIndexList(@RequestBody IdUserReq req) {
        return appSceneIndexService.getSceneIndexList(req);
    }

}
