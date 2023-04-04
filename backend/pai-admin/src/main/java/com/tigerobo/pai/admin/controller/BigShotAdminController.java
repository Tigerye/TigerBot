package com.tigerobo.pai.admin.controller;

import com.tigerobo.x.pai.api.admin.auth.AdminAuthorize;
import com.tigerobo.x.pai.api.admin.req.AdminOnlineStatusReq;
import com.tigerobo.x.pai.api.admin.req.BigShotAdminReq;
import com.tigerobo.x.pai.api.dto.admin.BigShotDto;
import com.tigerobo.x.pai.api.vo.IdReqVo;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.ResultVO;
import com.tigerobo.x.pai.api.vo.biz.pub.PubBigShotVo;
import com.tigerobo.x.pai.biz.admin.BigShotAdminService;

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
@Api(value = "bigshot",  tags = "bigshot")
@RequestMapping("/bigshot/")
public class BigShotAdminController {
    @Autowired
    private BigShotAdminService bigShotAdminService;

    @AdminAuthorize
    @ApiOperation(value = "bigshot列表")
    @PostMapping(path = {"list"}, consumes = "application/json", produces = "application/json")
    public PageVo<BigShotDto> bigShotList(@RequestBody BigShotAdminReq req){

        return bigShotAdminService.bigShotQuery(req);
    }

    @AdminAuthorize
    @ApiOperation(value = "bigshot恢复")
    @PostMapping(path = {"recover"},consumes = "application/json", produces = "application/json")
    public ResultVO bigShotRecover(@Valid @RequestBody IdReqVo req){
        bigShotAdminService.recover(req);
        return ResultVO.success();

    }

    @AdminAuthorize
    @ApiOperation(value = "bigshot删除")
    @PostMapping(path = {"delete"},consumes = "application/json", produces = "application/json")
    public ResultVO bigShotDel(@Valid @RequestBody IdReqVo reqVo){
        bigShotAdminService.bigShotDelete(reqVo.getId());
        return ResultVO.success();

    }
}
