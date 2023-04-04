package com.tigerobo.x.pai.service.controller.ai;

import com.tigerobo.x.pai.api.ai.base.IAiUserInteract;
import com.tigerobo.x.pai.api.ai.req.AiArtDetailReq;
import com.tigerobo.x.pai.api.ai.req.ArtImageChooseMainReq;
import com.tigerobo.x.pai.api.ai.req.ArtImageOnlineReq;
import com.tigerobo.x.pai.api.ai.req.ArtImagePublicPageReq;
import com.tigerobo.x.pai.api.ai.req.interact.AiOnlineReq;
import com.tigerobo.x.pai.api.ai.req.interact.UserInteractPublicPageReq;
import com.tigerobo.x.pai.api.ai.vo.AiArtImageVo;
import com.tigerobo.x.pai.api.auth.aspect.Authorize;
import com.tigerobo.x.pai.api.utils.Mapable;
import com.tigerobo.x.pai.api.vo.IdReqVo;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.ResultVO;
import com.tigerobo.x.pai.biz.ai.AiUserInteractService;
import com.tigerobo.x.pai.biz.ai.art.image.ArtImageOperateService;
import com.tigerobo.x.pai.biz.ai.art.image.ArtImageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Api(value = "ai用户交互数据", description = "ai用户交互数据")
@Slf4j
@RestController
@RequestMapping(value = "/ai/userInteract/")
public class AiUserInteractController {

    @Autowired
    private AiUserInteractService  aiUserInteractService;

    @Authorize
    @ApiOperation(value = "我的交互记录")
    @RequestMapping(value = "/getMine", method = POST)
    public PageVo<IAiUserInteract> getMine(HttpServletRequest request, @Valid @RequestBody UserInteractPublicPageReq req) {

        return aiUserInteractService.getMyList(req);
    }


    @ApiOperation(value = "公开列表")
    @RequestMapping(value = "/getPublishList", method = POST)
    public PageVo getPublishList(HttpServletRequest request, @Valid @RequestBody UserInteractPublicPageReq req) {
        return aiUserInteractService.getNewPublishList(req);
    }

    @ApiOperation(value = "id查看详情")
    @RequestMapping(value = "/getDetailById", method = POST)
    public IAiUserInteract getDetailByReqId(HttpServletRequest request, @Valid @RequestBody BizId req) {
        return aiUserInteractService.getDetail(req.getId(),req.getBizType());
    }

    @Authorize
    @ApiOperation(value = "offline")
    @RequestMapping(value = "/offline", method = POST)
    public ResultVO offline(HttpServletRequest request, @Valid @RequestBody BizId reqVo) {

        aiUserInteractService.offline(reqVo.getId(),reqVo.getBizType());

        return ResultVO.success();
    }


    @Authorize
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = POST)
    public ResultVO delete(HttpServletRequest request, @Valid @RequestBody BizId reqVo) {

        aiUserInteractService.delete(reqVo.getId(),reqVo.getBizType());

        return ResultVO.success();
    }

    @Authorize
    @ApiOperation(value = "online")
    @RequestMapping(value = "/online", method = POST)
    public ResultVO online(HttpServletRequest request, @Valid @RequestBody AiOnlineReq reqVo) {
        aiUserInteractService.online(reqVo);
        return ResultVO.success();
    }


    @Authorize
    @ApiOperation(value = "failRetry")
    @RequestMapping(value = "/failRetry", method = POST)
    public ResultVO failRetry(HttpServletRequest request, @Valid @RequestBody BizId reqVo) {

        aiUserInteractService.failRetry(reqVo.getId(),reqVo.getBizType());

        return ResultVO.success();
    }

    @Data
    private static class BizId {
        String id;
        Integer bizType;
    }

}
