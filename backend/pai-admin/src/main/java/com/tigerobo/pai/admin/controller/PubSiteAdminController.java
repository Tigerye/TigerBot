package com.tigerobo.pai.admin.controller;

import com.tigerobo.x.pai.api.admin.auth.AdminAuthorize;
import com.tigerobo.x.pai.api.admin.req.AdminOnlineStatusReq;
import com.tigerobo.x.pai.api.admin.req.BigShotAdminReq;
import com.tigerobo.x.pai.api.admin.req.IdListReq;
import com.tigerobo.x.pai.api.admin.req.PubSiteAdminReq;
import com.tigerobo.x.pai.api.dto.admin.PubSiteDto;
import com.tigerobo.x.pai.api.vo.IdReqVo;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.ResultVO;
import com.tigerobo.x.pai.api.vo.biz.pub.PubSiteVo;
import com.tigerobo.x.pai.biz.admin.PubSiteAdminService;
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
@Api(value = "pubsite",  tags = "pubsite")
@RequestMapping("/pubsite/")
public class PubSiteAdminController {
    @Autowired
    private PubSiteAdminService pubSiteAdminService;
    @AdminAuthorize
    @ApiOperation(value = "pubsite列表")
    @PostMapping(path = {"list"}, consumes = "application/json", produces = "application/json")
    public PageVo<PubSiteDto> pubSiteList(@RequestBody PubSiteAdminReq req){
        return pubSiteAdminService.pubSiteQuery(req);
    }

    @AdminAuthorize
    @ApiOperation(value = "pubsite恢复")
    @PostMapping(path = {"recover"},consumes = "application/json", produces = "application/json")
    public ResultVO pubSiteRecover(@Valid @RequestBody IdReqVo req){
        pubSiteAdminService.recover(req);
        return ResultVO.success();

    }

    @AdminAuthorize
    @ApiOperation(value = "pubsite删除")
    @PostMapping(path = {"delete"},consumes = "application/json", produces = "application/json")
    public ResultVO pubSiteDel(@Valid @RequestBody IdReqVo reqVo){
        pubSiteAdminService.pubSiteDelete(reqVo.getId());
        return ResultVO.success();

    }
}
