package com.tigerobo.x.pai.biz.micro.search;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.tigerobo.pai.search.api.base.UserBase;
import com.tigerobo.pai.search.api.dto.UserSearchApplicationDto;
import com.tigerobo.pai.search.api.dto.render.RenderTemplateDto;
import com.tigerobo.pai.search.api.dto.render.index.IndexRenderDto;
import com.tigerobo.pai.search.api.req.application.UserAddApplicationReq;
import com.tigerobo.pai.search.api.req.base.IdUserReq;
import com.tigerobo.pai.search.api.req.base.PageVo;
import com.tigerobo.pai.search.api.req.base.UserReqPage;
import com.tigerobo.pai.search.api.req.render.RenderPageReq;
import com.tigerobo.pai.search.api.req.render.RenderTemplateReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class SearchRenderService {


    @Autowired
    private RpcValueFactory rpcValueFactory;
    @Autowired
    private RestTemplate restTemplate;


    public  List<RenderTemplateDto> getRenderTemplate() {
        String url =rpcValueFactory.searchUrl+ "/render/getRenderTemplate";


//        req.setUser(rpcValueFactory.buildUserBase());
        final String s = restTemplate.postForObject(url, null, String.class);

        return JSON.parseArray(s, RenderTemplateDto.class);
    }

    public  List<RenderTemplateDto> getRenderTemplateByType(RenderTemplateReq req) {
        String url =rpcValueFactory.searchUrl+ "/render/getRenderTemplateByType";
//        req.setUser(rpcValueFactory.buildUserBase());
        final String s = restTemplate.postForObject(url, req, String.class);

        return JSON.parseArray(s, RenderTemplateDto.class);
    }
    public  RenderTemplateDto getRenderTemplateById(IdUserReq req) {
        String url =rpcValueFactory.searchUrl+ "/render/getRenderTemplateById";


        req.setUser(rpcValueFactory.buildUserBase());
        return restTemplate.postForObject(url, null, RenderTemplateDto.class);
    }


    public IndexRenderDto addOrUpdate(IndexRenderDto req) {
        String url =rpcValueFactory.searchUrl+ "/render/addOrUpdate";
        final UserBase userBase = rpcValueFactory.buildUserBase();
        req.setUser(userBase);
        return restTemplate.postForObject(url, req, IndexRenderDto.class);
    }

    public PageVo<IndexRenderDto> getRenderList(RenderPageReq req) {
        String url =rpcValueFactory.searchUrl+ "/render/getIndexList";
        req.setUser(rpcValueFactory.buildUserBase());
        final String s = restTemplate.postForObject(url, req, String.class);

        return JSON.parseObject(s, new TypeReference<PageVo<IndexRenderDto>>(){});
    }

    public IndexRenderDto getRenderDetail(IdUserReq req) {
        String url =rpcValueFactory.searchUrl+ "/render/getRenderDto";
        req.setUser(rpcValueFactory.buildUserBase(true));
        return restTemplate.postForObject(url, req, IndexRenderDto.class);
    }

    public void delete(IdUserReq req) {
        String url =rpcValueFactory.searchUrl+ "/render/delete";
        req.setUser(rpcValueFactory.buildUserBase());
        restTemplate.postForObject(url, req, String.class);
    }
}
