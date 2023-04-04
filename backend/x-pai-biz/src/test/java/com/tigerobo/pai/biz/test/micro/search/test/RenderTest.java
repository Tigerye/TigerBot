package com.tigerobo.pai.biz.test.micro.search.test;

import com.alibaba.fastjson.JSON;
import com.tigerobo.pai.search.api.dto.UserSearchApplicationDto;
import com.tigerobo.pai.search.api.dto.render.RenderTemplateDto;
import com.tigerobo.pai.search.api.dto.render.index.IndexRenderDto;
import com.tigerobo.pai.search.api.req.base.IdUserReq;
import com.tigerobo.pai.search.api.req.base.PageVo;
import com.tigerobo.pai.search.api.req.render.RenderPageReq;
import com.tigerobo.pai.search.api.req.render.RenderTemplateReq;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.biz.micro.search.SearchApplicationService;
import com.tigerobo.x.pai.biz.micro.search.SearchRenderService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class RenderTest extends BaseTest {

    @Autowired
    private SearchRenderService searchRenderService;


    @Autowired
    public SearchApplicationService searchApplicationService;

    @Test
    public void getTest() {
        RenderPageReq renderPageReq = new RenderPageReq();
        renderPageReq.setIndexId(2);
        final PageVo<IndexRenderDto> indexList = searchRenderService.getRenderList(renderPageReq);
        System.out.println(JSON.toJSONString(indexList));
    }

    @Test
    public void getRenderTest() {
        RenderPageReq renderPageReq = new RenderPageReq();
        renderPageReq.setIndexId(2);
        RenderTemplateReq req = new RenderTemplateReq();
        req.setSceneType(2);
        final List<RenderTemplateDto> renderTemplate = searchRenderService.getRenderTemplateByType(req);
        System.out.println(JSON.toJSONString(renderTemplate));
    }

    @Test
    public void appTest() {

        IdUserReq reqVo = new IdUserReq();
        reqVo.setId(1);
        final UserSearchApplicationDto userSearchApplicationDto = searchApplicationService.get(reqVo);

        System.out.println(userSearchApplicationDto);


    }
}
