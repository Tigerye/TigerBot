package com.tigerobo.x.pai.service.controller.ai;

import com.tigerobo.x.pai.api.ai.req.AiArtDetailReq;
import com.tigerobo.x.pai.api.ai.req.ArtImageOnlineReq;
import com.tigerobo.x.pai.api.ai.req.ArtImagePublicPageReq;

import com.tigerobo.x.pai.api.ai.vo.AiMultiObjectTrackVo;
import com.tigerobo.x.pai.api.auth.aspect.Authorize;
import com.tigerobo.x.pai.api.vo.IdReqVo;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.ResultVO;


import com.tigerobo.x.pai.biz.ai.multi.object.track.AiMultiObjectTrackOperateService;
import com.tigerobo.x.pai.biz.ai.multi.object.track.MultiObjectTrackService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Api(value = "ai-multi_object_track", description = "多目标跟踪")
@Slf4j
@RestController
@RequestMapping(value = "/ai/multi_object_track")
public class AiMultiObjectTrackController {

    @Autowired
    private MultiObjectTrackService spatioActionService;


    @Autowired
    private AiMultiObjectTrackOperateService aiMultiObjectTrackOperateService;


    @Authorize
    @ApiOperation(value = "我的视频")
    @RequestMapping(value = "/getMyVideos", method = POST)
    public PageVo<AiMultiObjectTrackVo> getMyVideos(HttpServletRequest request, @Valid @RequestBody ArtImagePublicPageReq req) {
        return spatioActionService.getMyList(req);
    }

    @ApiOperation(value = "公开的列表")
    @RequestMapping(value = "/getPublishList", method = POST)
    public PageVo<AiMultiObjectTrackVo> getPublishList(HttpServletRequest request, @Valid @RequestBody ArtImagePublicPageReq req) {
        return spatioActionService.getPublishList(req);
    }

    @ApiOperation(value = "reqId查看详情")
    @RequestMapping(value = "/getDetailByReqId", method = POST)
    public AiMultiObjectTrackVo getDetailByReqId(HttpServletRequest request, @Valid @RequestBody AiArtDetailReq req) {

        String appKey = request.getParameter("appKey");
        String appId = request.getParameter("appId");

        return spatioActionService.getByReqId(appId,req.getReqId());
    }

    @ApiOperation(value = "id查看详情")
    @RequestMapping(value = "/getDetailById", method = POST)
    public AiMultiObjectTrackVo getDetailByReqId(HttpServletRequest request, @Valid @RequestBody IdReqVo req) {
        return spatioActionService.getDetail(req);
    }

    @Authorize
    @ApiOperation(value = "offline")
    @RequestMapping(value = "/offline", method = POST)
    public ResultVO offline(HttpServletRequest request, @Valid @RequestBody IdReqVo reqVo) {

        aiMultiObjectTrackOperateService.offline(reqVo.getId(),null);

        return ResultVO.success();
    }


    @Authorize
    @ApiOperation(value = "failRetry")
    @RequestMapping(value = "/failRetry", method = POST)
    public ResultVO failRetry(HttpServletRequest request, @Valid @RequestBody IdReqVo reqVo) {

        aiMultiObjectTrackOperateService.failRetry(reqVo.getId());

        return ResultVO.success();
    }

    @Authorize
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = POST)
    public ResultVO delete(HttpServletRequest request, @Valid @RequestBody IdReqVo reqVo) {

        aiMultiObjectTrackOperateService.delete(reqVo.getId(),null);

        return ResultVO.success();
    }

    @Authorize
    @ApiOperation(value = "online")
    @RequestMapping(value = "/online", method = POST)
    public ResultVO online(HttpServletRequest request, @Valid @RequestBody ArtImageOnlineReq reqVo) {
        aiMultiObjectTrackOperateService.online(reqVo,null);
        return ResultVO.success();
    }


}
