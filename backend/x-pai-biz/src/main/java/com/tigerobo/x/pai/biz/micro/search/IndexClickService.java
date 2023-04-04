package com.tigerobo.x.pai.biz.micro.search;

import com.alibaba.fastjson.JSON;
import com.tigerobo.pai.search.api.base.UserBase;
import com.tigerobo.pai.search.api.dto.app.AppSceneIndexDto;
import com.tigerobo.pai.search.api.dto.click.ClickDto;
import com.tigerobo.pai.search.api.dto.click.ClickNumDto;
import com.tigerobo.pai.search.api.req.application.AppSceneIndexAddReq;
import com.tigerobo.pai.search.api.req.base.IdUserReq;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class IndexClickService {


    @Autowired
    private RpcValueFactory rpcValueFactory;
    @Autowired
    private RestTemplate restTemplate;

    String baseServicePath = "/click/";


    public ClickDto getAppClickTotal(IdUserReq req) {
        String url =rpcValueFactory.searchUrl+ baseServicePath+"getAppClickTotal";
        final UserBase userBase = rpcValueFactory.buildUserBase();
        req.setUser(userBase);
        return restTemplate.postForObject(url, req, ClickDto.class);
    }



    public ClickDto getIndexClickTotal(IdUserReq req) {
        String url =rpcValueFactory.searchUrl+ baseServicePath+"getIndexClickTotal";
        final UserBase userBase = rpcValueFactory.buildUserBase();
        req.setUser(userBase);
        return restTemplate.postForObject(url, req, ClickDto.class);
    }


    public List<ClickNumDto> getIndexClickTrend(IdUserReq req) {
        String url =rpcValueFactory.searchUrl+ baseServicePath+"getIndexClickTrend";
        final UserBase userBase = rpcValueFactory.buildUserBase();
        req.setUser(userBase);
        final String s = restTemplate.postForObject(url, req, String.class);
        if (StringUtils.isBlank(s)){
            return new ArrayList();
        }

        return JSON.parseArray(s,ClickNumDto.class);
    }

    public List<ClickNumDto> getAppClickTrend(IdUserReq req) {
        String url =rpcValueFactory.searchUrl+ baseServicePath+"getAppClickTrend";
        final UserBase userBase = rpcValueFactory.buildUserBase();
        req.setUser(userBase);
        final String s = restTemplate.postForObject(url, req, String.class);
        if (StringUtils.isBlank(s)){
            return new ArrayList();
        }

        return JSON.parseArray(s,ClickNumDto.class);
    }

}
