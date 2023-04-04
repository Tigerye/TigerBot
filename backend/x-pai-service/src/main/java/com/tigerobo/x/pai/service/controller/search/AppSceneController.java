package com.tigerobo.x.pai.service.controller.search;


import com.tigerobo.pai.search.api.dto.app.AppSceneDto;
import com.tigerobo.pai.search.api.model.AppSceneModel;
import com.tigerobo.pai.search.api.req.application.AppSceneAddReq;
import com.tigerobo.pai.search.api.req.base.IdUserReq;

import com.tigerobo.x.pai.biz.micro.search.AppSceneService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/appScene/")
@Api(value = "搜索应用场景实例", position = 1)
public class AppSceneController {

    @Autowired
    private AppSceneService appSceneService;

    @ApiOperation(value = "新增*修改", position = 2)
    @PostMapping(path = "/addOrUpdate", consumes = "application/json", produces = "application/json")
    public AppSceneDto addOrUpdate(@RequestBody AppSceneAddReq req) {
        return appSceneService.addOrUpdate(req);
    }

    @ApiOperation(value = "删除", position = 3)
    @PostMapping(path = "/delete", consumes = "application/json", produces = "application/json")
    public void delete(@RequestBody IdUserReq req) {
        appSceneService.delete(req);
    }

    @ApiOperation(value = "获取应用场景列表", position = 4)
    @PostMapping(path = "/getAppSceneList", consumes = "application/json", produces = "application/json")
    public List<AppSceneModel> getAppSceneList(@RequestBody IdUserReq req) {
        return appSceneService.getAppSceneList(req);
    }
    @ApiOperation(value = "获取应用场景详情", position = 4)
    @PostMapping(path = "/getSceneDetail", consumes = "application/json", produces = "application/json")
    public AppSceneModel getSceneDetail(@RequestBody IdUserReq req) {
        return appSceneService.getBySceneId(req);
    }



    //todo userIndexListPage;

    //deleteIndex
}
