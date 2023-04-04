package com.tigerobo.x.pai.service.controller.web;

import com.tigerobo.x.pai.api.auth.aspect.Authorize;
import com.tigerobo.x.pai.api.vo.biz.DemandQueryVo;
import com.tigerobo.x.pai.api.vo.biz.DemandVo;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.WebRepVo;
import com.tigerobo.x.pai.biz.biz.service.DemandServiceImpl;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/web/")
@Api(value = "页面-列表", position = 2900, tags = "页面-列表")
public class WebController {
    @Autowired
    private DemandServiceImpl demandService;

    @ApiOperation(value = "需求-创建")
    @PostMapping(path = {"demand/create", "demand-create"}, consumes = "application/json", produces = "application/json")
    @Authorize
    public DemandVo demandCreate(@Valid @RequestBody WebRepVo requestVo) {
        Integer userId = ThreadLocalHolder.getUserId();
        return demandService.createOrUpdate(requestVo,userId);
    }

    @ApiOperation(value = "需求-列表")
    @PostMapping(path = {"demand/list", "demand-list"}, consumes = "application/json", produces = "application/json")
    public PageVo<DemandVo> demandList(@Valid @RequestBody DemandQueryVo requestVo) {
        return this.demandService.demandList(requestVo);
    }

}
