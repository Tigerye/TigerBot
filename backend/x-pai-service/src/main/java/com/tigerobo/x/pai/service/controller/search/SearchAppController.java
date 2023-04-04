package com.tigerobo.x.pai.service.controller.search;


import com.tigerobo.pai.search.api.dto.UserSearchApplicationDto;
import com.tigerobo.pai.search.api.req.application.UserAddApplicationReq;
import com.tigerobo.pai.search.api.req.base.IdUserReq;
import com.tigerobo.pai.search.api.req.base.PageVo;
import com.tigerobo.pai.search.api.req.base.UserReqPage;

import com.tigerobo.x.pai.api.auth.aspect.Authorize;
import com.tigerobo.x.pai.api.vo.ResultVO;
import com.tigerobo.x.pai.biz.micro.search.SearchApplicationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/searchApp/")
@Api(value = "搜索应用", position = 1, tags = "搜索应用")
public class SearchAppController {

    @Autowired
    private SearchApplicationService searchApplicationService;

    @Authorize
    @ApiOperation(value = "创建应用", position = 50)
    @PostMapping(path = "/create", consumes = "application/json", produces = "application/json")
    public UserSearchApplicationDto add(@RequestBody UserAddApplicationReq req) {
        return searchApplicationService.add(req);
    }
    @Authorize
    @ApiOperation(value = "删除应用", position = 50)
    @PostMapping(path = "/delete", consumes = "application/json", produces = "application/json")
    public ResultVO delete(@RequestBody IdUserReq req) {
        searchApplicationService.delete(req);
        return ResultVO.success();
    }

    @Authorize
    @ApiOperation(value = "获取应用列表", position = 50)
    @PostMapping(path = "/getUserPage", consumes = "application/json", produces = "application/json")
    public PageVo<UserSearchApplicationDto> getUserPage(@RequestBody UserReqPage req) {
        return searchApplicationService.getUserPage(req);
    }
    @Authorize
    @ApiOperation(value = "查看应用详情", position = 50)
    @PostMapping(path = "/get", consumes = "application/json", produces = "application/json")
    public UserSearchApplicationDto get(@RequestBody IdUserReq req) {
        return searchApplicationService.get(req);
    }
}
