package com.tigerobo.x.pai.service.controller.ai;

import com.tigerobo.x.pai.api.ai.req.*;
import com.tigerobo.x.pai.api.ai.req.art.image.ArtImageHideReq;
import com.tigerobo.x.pai.api.ai.vo.AiArtImageVo;
import com.tigerobo.x.pai.api.auth.aspect.Authorize;
import com.tigerobo.x.pai.api.dto.ai.ArtModifierModel;
import com.tigerobo.x.pai.api.enums.ArtImageType;
import com.tigerobo.x.pai.api.vo.IdReqVo;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.ResultVO;
import com.tigerobo.x.pai.biz.ai.ImageSizeService;
import com.tigerobo.x.pai.biz.ai.art.image.ArtImageCoinCalculateService;
import com.tigerobo.x.pai.biz.ai.art.image.ArtImageOperateService;
import com.tigerobo.x.pai.biz.ai.art.image.ArtImageSearchService;
import com.tigerobo.x.pai.biz.ai.art.image.ArtImageService;
import com.tigerobo.x.pai.biz.biz.customer.ModelConsumeCheckService;
import com.tigerobo.x.pai.biz.coin.CoinPriceService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Api(value = "ai服务")
@Slf4j
@RestController
@RequestMapping(value = "/ai/artImage/")
public class AiArtImageController {

    @Autowired
    private ArtImageService artImageService;

    @Autowired
    private ArtImageSearchService artImageSearchService;

    @Autowired
    private ArtImageOperateService aiArtImageOperateService;


    @Autowired
    private ModelConsumeCheckService modelConsumeCheckService;
    @Autowired
    private ImageSizeService imageSizeService;


    @Autowired
    private CoinPriceService coinPriceService;

    @Autowired
    private ArtImageCoinCalculateService artImageCoinCalculateService;

    @ApiOperation(value = "艺术图调用算法币基础价格")
    @RequestMapping(value = "/getBaseCoinPrice", method = {POST},produces = "application/json")
    public Map<String,Object> getBaseCoinPrice() {
        Map<String,Object> data = new HashMap<>();
        final Integer artImageBaseCoinPrice = coinPriceService.getArtImageBaseCoinPrice();
        data.put("coinPrice",artImageBaseCoinPrice);

        return data;
    }


    @ApiOperation(value = "计算价格")
    @RequestMapping(value = "/calCoinPrice", method = {POST},consumes = "application/json",produces = "application/json")
    public ArtImageCoinCalculateService.CalData calCoinPrice(@RequestBody AiArtImageGenerateReq req) {


        final ArtImageCoinCalculateService.CalData calData = artImageCoinCalculateService.calData(req);

        return calData;
    }


    @ApiOperation(value = "图片尺寸列表")
    @RequestMapping(value = "/getSizeList", method = {POST})
    public Map<String,Object> getSizeList(HttpServletRequest request,@RequestBody RatioSizeReq req) {

        final ImageSizeService.ImageSizTypeVo cache = imageSizeService.getCache();

        ArtImageType imageType = ArtImageType.STABLE;
        if (req!=null){
            final ArtImageType reqImageType = ArtImageType.getByName(req.getStyleType());
            if (reqImageType !=null){
                imageType = reqImageType;
            }
        }

        List<ImageSizeService.ImageSizeVo> ratioList = null;
        if (imageType == ArtImageType.DISCO){
            ratioList = cache.getDiscoRatioList();
        }else if (imageType == ArtImageType.STABLE){
            ratioList = cache.getStableRatioList();
        }

        Map<String,Object> data = new HashMap<>();

        data.put("ratioList",ratioList);
        return data;
    }

    @Authorize
    @ApiOperation(value = "我生成的图")
    @RequestMapping(value = "/getMyImages", method = POST)
    public PageVo<AiArtImageVo> getMyImages(HttpServletRequest request, @Valid @RequestBody ArtImagePublicPageReq req) {
        return artImageService.getMyList(req);
    }

    @Authorize
    @ApiOperation(value = "我生成的图V2")
    @RequestMapping(value = "/getMyImagesV2", method = POST)
    public PageVo<AiArtImageVo> getMyImagesV2(@Valid @RequestBody ArtImagePublicPageReq req) {
        return artImageSearchService.getMyHomeList(req);
    }
//    @Authorize
    @ApiOperation(value = "用户生成的图")
    @RequestMapping(value = "/getUserImages", method = POST)
    public PageVo<AiArtImageVo> getUserImages(@Valid @RequestBody ArtImagePublicPageReq req) {
        return artImageSearchService.getUserHomeList(req);
    }

    @Deprecated
    @ApiOperation(value = "剩余调用次数")
    @RequestMapping(value = "/getRemainCallCount", method = POST)
    public Integer getRemainCallCount(HttpServletRequest request) {
        final Integer userId = ThreadLocalHolder.getUserId();
        return modelConsumeCheckService.countUserArtImgRemainCall(userId);
    }

    @ApiOperation(value = "剩余调用次数")
    @RequestMapping(value = "/getRemainCallCountV2", method = POST)
    public Map<String,Integer> getRemainCallCountV2(HttpServletRequest request) {
        final Integer userId = ThreadLocalHolder.getUserId();
        final int total = modelConsumeCheckService.countUserTotal(userId);
        final int used = modelConsumeCheckService.countUsed(userId);
        Map<String,Integer> map = new HashMap<>();

        int remain = Math.max(0,total-used);
        map.put("remain",remain);
        map.put("todayMax",total);
        return map;
    }

//    @Authorize
//    @ApiOperation(value = "图片热门列表")
//    @RequestMapping(value = "/getHotImages", method = POST)
//    public PageVo<AiArtImageVo> getHotImages(HttpServletRequest request, @Valid @RequestBody ArtImagePublicPageReq req) {
//        return aiArtService.getHotList(req);
//    }

    @ApiOperation(value = "公开的图列表")
    @RequestMapping(value = "/getPublishList", method = POST)
    public PageVo<AiArtImageVo> getPublishList(HttpServletRequest request, @Valid @RequestBody ArtImagePublicPageReq req) {
        return artImageService.getPublishList(req);
    }

    @ApiOperation(value = "reqId查看详情")
    @RequestMapping(value = "/getDetailByReqId", method = POST)
    public AiArtImageVo getDetailByReqId(HttpServletRequest request, @Valid @RequestBody AiArtDetailReq req) {

        String appKey = request.getParameter("appKey");
        String appId = request.getParameter("appId");

        return artImageService.getByReqId(appId,req.getReqId());
    }

    @ApiOperation(value = "id查看详情")
    @RequestMapping(value = "/getDetailById", method = POST)
    public AiArtImageVo getDetailByReqId(HttpServletRequest request, @Valid @RequestBody IdReqVo req) {
        return artImageService.getDetail(req);
    }


    @Deprecated
    @ApiOperation(value = "查看modifier列表V2")
    @RequestMapping(value = "/getModifierListV2", method = POST)
    public List<ArtModifierModel> getModifierListV2(HttpServletRequest request) {
        return artImageService.getModifierWithImgList();
    }

    @Authorize
    @ApiOperation(value = "offline")
    @RequestMapping(value = "/offline", method = POST)
    public ResultVO offline(HttpServletRequest request, @Valid @RequestBody IdReqVo reqVo) {

        aiArtImageOperateService.offline(reqVo.getId(),null);

        return ResultVO.success();
    }


    @Authorize
    @ApiOperation(value = "failRetry")
    @RequestMapping(value = "/failRetry", method = POST)
    public ResultVO failRetry(HttpServletRequest request, @Valid @RequestBody IdReqVo reqVo) {

        aiArtImageOperateService.failRetry(reqVo.getId());

        return ResultVO.success();
    }

    @Authorize
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = POST)
    public ResultVO delete(HttpServletRequest request, @Valid @RequestBody IdReqVo reqVo) {

        aiArtImageOperateService.delete(reqVo.getId(),null);

        return ResultVO.success();
    }

    @Authorize
    @ApiOperation(value = "online")
    @RequestMapping(value = "/online", method = POST)
    public ResultVO online(HttpServletRequest request, @Valid @RequestBody ArtImageOnlineReq reqVo) {
        aiArtImageOperateService.online(reqVo,null);
        return ResultVO.success();
    }


    @Authorize
    @ApiOperation(value = "选择主图")
    @RequestMapping(value = "/chooseProcessAsMain", method = POST)
    public ResultVO chooseProcessAsMain(@Valid @RequestBody ArtImageChooseMainReq reqVo) {
        aiArtImageOperateService.chooseMain(reqVo);
        return ResultVO.success();
    }


    @Authorize
    @ApiOperation(value = "设置隐藏")
    @RequestMapping(value = "/setHide", method = POST)
    public ResultVO setHide(@Valid @RequestBody ArtImageHideReq reqVo) {
        aiArtImageOperateService.hide(reqVo);
        return ResultVO.success();
    }



    @Authorize
    @ApiOperation(value = "获取历史图谱")
    @RequestMapping(value = "/getHistoryImages", method = POST)
    public List<String> getHistoryImages() {
        return artImageService.getHistoryImages();
    }



    @Data
    public static  class RatioSizeReq{
        /**
         * @see ArtImageType
         */
        @ApiModelProperty(value = "stable,disco")
        String styleType;
    }

}
