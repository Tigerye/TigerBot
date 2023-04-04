package com.tigerobo.x.pai.biz.micro.search;

import com.tigerobo.pai.search.api.dto.UserSearchApplicationDto;
import com.tigerobo.pai.search.api.req.application.UserAddApplicationReq;
import com.tigerobo.pai.search.api.req.base.IdUserReq;
import com.tigerobo.pai.search.api.req.base.PageVo;
import com.tigerobo.pai.search.api.req.base.UserReqPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SearchApplicationService {

    @Autowired
    private RpcValueFactory rpcValueFactory;
    @Autowired
    private RestTemplate restTemplate;


    public UserSearchApplicationDto add(UserAddApplicationReq req) {
        String url =rpcValueFactory.searchUrl+ "/searchApp/create";
        req.setUser(rpcValueFactory.buildUserBase());
        return restTemplate.postForObject(url, req, UserSearchApplicationDto.class);
    }


    public void delete(IdUserReq req) {
        String url =rpcValueFactory.searchUrl+ "/searchApp/delete";
        req.setUser(rpcValueFactory.buildUserBase());
        restTemplate.postForObject(url, req,String.class);
    }

    public PageVo<UserSearchApplicationDto> getUserPage( UserReqPage req) {
        String url =rpcValueFactory.searchUrl+ "/searchApp/getUserPage";
        req.setUser(rpcValueFactory.buildUserBase());
        return restTemplate.postForObject(url, req, PageVo.class);
    }

    public UserSearchApplicationDto get(IdUserReq req) {

        String url =rpcValueFactory.searchUrl+ "/searchApp/get";
        req.setUser(rpcValueFactory.buildUserBase());
        return restTemplate.postForObject(url, req, UserSearchApplicationDto.class);
    }



}
