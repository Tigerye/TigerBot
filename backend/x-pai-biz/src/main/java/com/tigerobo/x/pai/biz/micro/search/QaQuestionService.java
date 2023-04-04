package com.tigerobo.x.pai.biz.micro.search;

import com.alibaba.fastjson.JSON;
import com.tigerobo.pai.search.api.base.UserBase;
import com.tigerobo.pai.search.api.dto.app.AppSceneDto;
import com.tigerobo.pai.search.api.dto.qa.QaQuestionDto;
import com.tigerobo.pai.search.api.model.AppSceneModel;
import com.tigerobo.pai.search.api.req.application.AppSceneAddReq;
import com.tigerobo.pai.search.api.req.base.IdUserReq;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class QaQuestionService {


    @Autowired
    private RpcValueFactory rpcValueFactory;
    @Autowired
    private RestTemplate restTemplate;

    String baseServicePath = "/qa/";


    public List<QaQuestionDto> getQuestionList(IdUserReq req) {
        String url =rpcValueFactory.searchUrl+ baseServicePath+"getQuestionList";
        final UserBase userBase = rpcValueFactory.buildUserBase();
        req.setUser(userBase);
        final String s = restTemplate.postForObject(url, req, String.class);

        if (StringUtils.isBlank(s)){
            return null;
        }
        return JSON.parseArray(s, QaQuestionDto.class);
    }


}
