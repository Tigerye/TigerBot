package com.tigerobo.x.pai.service.controller.blog;

import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.biz.mine.UserIdVo;
import com.tigerobo.x.pai.api.vo.biz.pub.BigShotQueryReq;
import com.tigerobo.x.pai.api.vo.biz.pub.PubBigShotVo;
import com.tigerobo.x.pai.api.vo.biz.pub.PubSiteVo;
import com.tigerobo.x.pai.api.vo.IdNumVo;
import com.tigerobo.x.pai.api.vo.user.follow.FollowVo;
import com.tigerobo.x.pai.biz.biz.pub.MediaService;
import com.tigerobo.x.pai.biz.biz.pub.PubBigShotService;
import com.tigerobo.x.pai.biz.biz.pub.PubSiteService;
import com.tigerobo.x.pai.biz.converter.FollowBizConvert;
import com.tigerobo.x.pai.service.controller.web.XPaiWebController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/web/site/")
@Api(value = "网站源接口", tags = "网站源接口")
public class SiteController extends XPaiWebController {

    @Autowired
    private PubSiteService pubSiteService;
    @Autowired
    private PubBigShotService pubBigShotService;
    @Autowired
    private MediaService mediaService;
    @ApiOperation(value = "获取blog来源列表")
    @PostMapping(path = {"getSiteList"}, produces = "application/json")
    public List<FollowVo> getSiteList(){

        List<PubSiteVo> list = pubSiteService.getList();
        if (CollectionUtils.isEmpty(list)){
            return new ArrayList<>();
        }
        return list.stream().map(vo->FollowBizConvert.convert(vo)).collect(Collectors.toList());
    }


    @ApiOperation(value = "获取site分页列表")
    @PostMapping(path = {"getSitePage"}, produces = "application/json")
    public PageVo<FollowVo> getSitePage(@RequestBody BigShotQueryReq req){
        return pubSiteService.getPage(req);
    }

    @Deprecated
    @ApiOperation(value = "获取bigshot列表")
    @PostMapping(path = {"getBigShotList"}, produces = "application/json")
    public List<FollowVo> getBigShotList(){
        return pubBigShotService.getTopBigShotList();
    }

    @ApiOperation(value = "获取bigshot订阅列表")
    @PostMapping(path = {"getBigShotSubScriptPage"}, produces = "application/json")
    public PageVo<FollowVo> getBigShotSubScriptPage(@RequestBody BigShotQueryReq req){
        return pubBigShotService.getBigShotPage(req);
    }

    @ApiOperation(value = "获取bigshot来源详情")
    @PostMapping(path = {"bigshot/detail"}, consumes = "application/json", produces = "application/json")
    public FollowVo getBigShotDetail(@RequestBody IdNumVo req){
        Integer id = req.getId();
        FollowVo detail = pubBigShotService.getDetail(id);
        return detail;
    }

    @ApiOperation(value = "media聚合搜索")
    @PostMapping(path = {"media/search"}, consumes = "application/json", produces = "application/json")
    public List<FollowVo> mediaSearch(@RequestBody BigShotQueryReq req){
        return mediaService.search(req);
    }


    @ApiOperation(value = "获取来源详情")
    @PostMapping(path = {"detail"}, consumes = "application/json", produces = "application/json")
    public FollowVo getDetail(@RequestBody IdNumVo req){
        Integer id = req.getId();
        PubSiteVo detail = pubSiteService.getDetail(id);
        return FollowBizConvert.convert(detail);
    }

}
