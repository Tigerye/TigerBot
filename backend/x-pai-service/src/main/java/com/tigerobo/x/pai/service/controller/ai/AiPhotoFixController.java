package com.tigerobo.x.pai.service.controller.ai;

import com.tigerobo.x.pai.api.ai.req.AiArtDetailReq;
import com.tigerobo.x.pai.api.ai.req.photo.PhotoFixPageReq;
import com.tigerobo.x.pai.api.ai.vo.PhotoFixVo;
import com.tigerobo.x.pai.api.auth.aspect.Authorize;
import com.tigerobo.x.pai.api.vo.IdReqVo;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.ResultVO;
import com.tigerobo.x.pai.biz.ai.photo.PhotoFixOperateService;
import com.tigerobo.x.pai.biz.ai.photo.PhotoFixService;
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

@Api(value = "ai修复照片", description = "ai服务-修复照片")
@Slf4j
@RestController
@RequestMapping(value = "/ai/photoFix/")
public class AiPhotoFixController {

    @Autowired
    private PhotoFixService photoFixService;

    @Autowired
    private PhotoFixOperateService photoFixOperateService;

    @Authorize
    @ApiOperation(value = "我生成的照片")
    @RequestMapping(value = "/getMyPhotos", method = POST)
    public PageVo<PhotoFixVo> getMyPhotos(HttpServletRequest request, @Valid @RequestBody PhotoFixPageReq req) {
        return photoFixService.getMyList(req);
    }


    @ApiOperation(value = "reqId查看详情")
    @RequestMapping(value = "/getDetailByReqId", method = POST)
    public PhotoFixVo getDetailByReqId(HttpServletRequest request, @Valid @RequestBody AiArtDetailReq req) {
        return photoFixService.getByReqId(req.getReqId());
    }

    @ApiOperation(value = "id查看详情")
    @RequestMapping(value = "/getDetailById", method = POST)
    public PhotoFixVo getDetailByReqId(HttpServletRequest request, @Valid @RequestBody IdReqVo req) {
        return photoFixService.getDetail(req);
    }
//
//    @Authorize
//    @ApiOperation(value = "offline")
//    @RequestMapping(value = "/offline", method = POST)
//    public ResultVO offline(HttpServletRequest request, @Valid @RequestBody IdReqVo reqVo) {
//
//        aiArtImageOperateService.offline(reqVo.getId());
//
//        return ResultVO.success();
//    }
//
//
//    @Authorize
//    @ApiOperation(value = "failRetry")
//    @RequestMapping(value = "/failRetry", method = POST)
//    public ResultVO failRetry(HttpServletRequest request, @Valid @RequestBody IdReqVo reqVo) {
//
//        aiArtImageOperateService.failRetry(reqVo.getId());
//
//        return ResultVO.success();
//    }
//
    @Authorize
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = POST)
    public ResultVO delete(HttpServletRequest request, @Valid @RequestBody IdReqVo reqVo) {

        photoFixOperateService.delete(reqVo.getId());

        return ResultVO.success();
    }
//
//    @Authorize
//    @ApiOperation(value = "online")
//    @RequestMapping(value = "/online", method = POST)
//    public ResultVO online(HttpServletRequest request, @Valid @RequestBody ArtImageOnlineReq reqVo) {
//        aiArtImageOperateService.online(reqVo);
//        return ResultVO.success();
//    }




}
