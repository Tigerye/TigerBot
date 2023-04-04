package com.tigerobo.x.pai.service.controller.search;

import com.tigerobo.pai.search.api.dto.render.RenderTemplateDto;
import com.tigerobo.pai.search.api.dto.render.index.IndexRenderDto;
import com.tigerobo.pai.search.api.req.base.IdUserReq;
import com.tigerobo.pai.search.api.req.base.PageVo;
import com.tigerobo.pai.search.api.req.render.RenderPageReq;

import com.tigerobo.pai.search.api.req.render.RenderTemplateReq;
import com.tigerobo.x.pai.api.auth.aspect.Authorize;
import com.tigerobo.x.pai.biz.micro.search.SearchRenderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/searchRender/")
@Api(value = "搜索渲染UI", position = 1, tags = "搜索渲染UI")
public class RenderController {


    @Autowired
    private SearchRenderService searchRenderService;


    @ApiOperation(value = "渲染模板列表", position = 50)
    @PostMapping(path = "getRenderTemplate",produces = "application/json")
    public List<RenderTemplateDto> getRenderTemplate(){
        return searchRenderService.getRenderTemplate();
    }


    @ApiOperation(value = "渲染模板列表by类型", position = 50)
    @PostMapping(path = "getRenderTemplateByType",produces = "application/json")
    public List<RenderTemplateDto> getRenderTemplateByType(@RequestBody RenderTemplateReq req){
        return searchRenderService.getRenderTemplateByType(req);
    }


    @ApiOperation(value = "渲染模板详情", position = 50)
    @PostMapping(path = "getRenderTemplateById",produces = "application/json")
    public RenderTemplateDto getRenderTemplateById(@RequestBody IdUserReq req){
        return searchRenderService.getRenderTemplateById(req);
    }

    @Authorize
    @ApiOperation(value = "添加或修改索引渲染ui", position = 50)
    @PostMapping(path = "addOrUpdate",produces = "application/json")
    public IndexRenderDto addOrUpdate(@RequestBody IndexRenderDto indexRenderDto){
        return searchRenderService.addOrUpdate(indexRenderDto);
    }

    @Authorize
    @ApiOperation(value = "获取索引渲染ui列表", position = 50)
    @PostMapping(path = "getIndexList",produces = "application/json")
    public PageVo<IndexRenderDto> getIndexList(@RequestBody RenderPageReq indexRenderDto){
        final PageVo<IndexRenderDto> userPage = searchRenderService.getRenderList(indexRenderDto);
        return userPage;
    }


    @Authorize
    @ApiOperation(value = "删除渲染配置")
    @PostMapping(path = "delete",produces = "application/json")
    public void delete(@RequestBody IdUserReq req){
        searchRenderService.delete(req);
    }



    //    @Authorize
    @ApiOperation(value = "获取render详情", position = 50)
    @PostMapping(path = "getRenderDetail",produces = "application/json")
    public IndexRenderDto getRenderDetail(@RequestBody IdUserReq idUserReq){
        return searchRenderService.getRenderDetail(idUserReq);
    }



}
