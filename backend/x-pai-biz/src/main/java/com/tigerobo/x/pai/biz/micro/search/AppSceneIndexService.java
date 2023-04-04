package com.tigerobo.x.pai.biz.micro.search;

import com.alibaba.fastjson.JSON;
import com.tigerobo.pai.search.api.base.UserBase;
import com.tigerobo.pai.search.api.dto.app.AppSceneDto;
import com.tigerobo.pai.search.api.dto.app.AppSceneIndexDto;
import com.tigerobo.pai.search.api.model.AppSceneModel;
import com.tigerobo.pai.search.api.req.application.AppSceneAddReq;
import com.tigerobo.pai.search.api.req.application.AppSceneIndexAddReq;
import com.tigerobo.pai.search.api.req.base.IdUserReq;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class AppSceneIndexService {


    @Autowired
    private RpcValueFactory rpcValueFactory;
    @Autowired
    private RestTemplate restTemplate;

    String baseServicePath = "/app/scene/index/";


    public void add(AppSceneIndexAddReq req) {
        String url =rpcValueFactory.searchUrl+ baseServicePath+"add";
        final UserBase userBase = rpcValueFactory.buildUserBase();
        req.setUser(userBase);
        restTemplate.postForObject(url, req, String.class);
    }

    public void delete(IdUserReq req) {
        String url =rpcValueFactory.searchUrl+ baseServicePath+"delete";
        final UserBase userBase = rpcValueFactory.buildUserBase();
//        req.setUser(userBase);
        restTemplate.postForObject(url, req, String.class);
    }

    public List<AppSceneIndexDto> getSceneIndexList(IdUserReq req) {
        String url =rpcValueFactory.searchUrl+ baseServicePath+"getSceneIndexList";
        final UserBase userBase = rpcValueFactory.buildUserBase();
//        req.setUser(userBase);
        final String s = restTemplate.postForObject(url, req, String.class);
        if (StringUtils.isBlank(s)){
            return null;
        }
        return JSON.parseArray(s,AppSceneIndexDto.class);
    }

}
