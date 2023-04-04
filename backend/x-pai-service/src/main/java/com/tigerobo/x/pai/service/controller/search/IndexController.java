package com.tigerobo.x.pai.service.controller.search;


import com.tigerobo.pai.search.api.dto.UserIndexDto;
import com.tigerobo.pai.search.api.req.UserAddIndexReq;
import com.tigerobo.pai.search.api.req.application.ApplicationPageReq;
import com.tigerobo.pai.search.api.req.base.IdUserReq;
import com.tigerobo.pai.search.api.req.base.PageVo;
import com.tigerobo.pai.search.api.req.base.UserReq;
import com.tigerobo.pai.search.api.req.base.UserReqPage;

import com.tigerobo.pai.search.api.req.index.IndexPageReq;
import com.tigerobo.pai.search.api.req.index.IndexUpdateReq;
import com.tigerobo.x.pai.api.auth.aspect.Authorize;
import com.tigerobo.x.pai.api.vo.ResultVO;
import com.tigerobo.x.pai.biz.micro.search.SearchIndexService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/index/")
@Api(value = "索引", tags = "索引")
public class IndexController {

    @Autowired
    private SearchIndexService searchIndexService;

    @ApiOperation(value = "索引-创建索引", tags = "索引-创建索引")
    @Authorize
    @PostMapping(path = "/createIndex", consumes = "application/json", produces = "application/json")
    public UserIndexDto createIndex(@RequestBody UserAddIndexReq req) {
        return searchIndexService.createIndex(req);
    }

    @ApiOperation(value = "索引-删除索引", tags = "索引-创建索引")
    @Authorize
    @PostMapping(path = "/deleteIndex", consumes = "application/json", produces = "application/json")
    public ResultVO deleteIndex(@RequestBody IdUserReq req) {
        searchIndexService.deleteIndex(req);
        return ResultVO.success();
    }


    @ApiOperation(value = "索引-更新索引")
    @Authorize
    @PostMapping(path = "/update", consumes = "application/json", produces = "application/json")
    public ResultVO update(@RequestBody IndexUpdateReq req) {
        searchIndexService.update(req);
        return ResultVO.success();
    }
    @ApiOperation(value = "索引-获取用户索引列表", tags = "索引-获取用户索引列表")
//    @Authorize
    @PostMapping(path = "/getUserIndexPage", consumes = "application/json", produces = "application/json")
    public PageVo<UserIndexDto> getUserIndexPage(@RequestBody IndexPageReq req) {
        return searchIndexService.getUserIndexPage(req);
    }

    @ApiOperation(value = "索引-获取用户索引", tags = "索引-获取用户索引")
//    @Authorize
    @PostMapping(path = "/getUserIndex", consumes = "application/json", produces = "application/json")
    public UserIndexDto getUserIndex(@RequestBody IdUserReq req) {
        return searchIndexService.getUserIndex(req);
    }



    //todo userIndexListPage;

    //deleteIndex
}
