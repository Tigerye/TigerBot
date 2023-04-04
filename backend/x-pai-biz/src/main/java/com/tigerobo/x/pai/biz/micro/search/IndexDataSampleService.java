package com.tigerobo.x.pai.biz.micro.search;

import com.alibaba.fastjson.JSON;
import com.tigerobo.pai.search.api.base.UserBase;
import com.tigerobo.pai.search.api.dto.sample.DataSampleDto;
import com.tigerobo.pai.search.api.req.base.TypeReq;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class IndexDataSampleService {




    @Autowired
    private RpcValueFactory rpcValueFactory;
    @Autowired
    private RestTemplate restTemplate;

    String baseServicePath = "/dataSample/";

    public List<DataSampleDto> getDataSample() {
        TypeReq req = new TypeReq();
        req.setType(1);
        return getDataSample(req);
    }


    public List<DataSampleDto> getDataSample(TypeReq req) {
        String url =rpcValueFactory.searchUrl+ baseServicePath+"getSampleListByType";
        final UserBase userBase = rpcValueFactory.buildUserBase();
//        req.setUser(userBase);
        final String s = restTemplate.postForObject(url, req, String.class);
        if (StringUtils.isBlank(s)){
            return null;
        }
        return JSON.parseArray(s, DataSampleDto.class);
    }
}
