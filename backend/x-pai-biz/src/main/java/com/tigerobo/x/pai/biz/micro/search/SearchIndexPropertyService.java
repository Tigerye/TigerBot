package com.tigerobo.x.pai.biz.micro.search;

import com.alibaba.fastjson.JSONObject;
import com.tigerobo.pai.search.api.dto.PaiIndexPropertyDto;
import com.tigerobo.pai.search.api.dto.PropertyShowDto;
import com.tigerobo.pai.search.api.req.base.IdUserReq;
import com.tigerobo.pai.search.api.req.property.PropertyCommitReq;
import com.tigerobo.pai.search.api.req.property.PropertyShowReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class SearchIndexPropertyService extends BaseUserCombineService{
    @Value("${pai.search.hostUrl:}")
    String searchUrl;
    @Autowired
    private RestTemplate restTemplate;

    public List<PaiIndexPropertyDto> getIndexProperties(IdUserReq req){
        String url =searchUrl+ "/indexProperty/getIndexProperties";
        req.setUser(buildUserBase());
        final PaiIndexPropertyDto[] paiIndexPropertyDtos =
                restTemplate.postForObject(url, req, PaiIndexPropertyDto[].class);

        if (paiIndexPropertyDtos!=null&&paiIndexPropertyDtos.length>0){
            return Arrays.asList(paiIndexPropertyDtos);
        }
        return new ArrayList<>();
    }

    public JSONObject commitProperties(PropertyCommitReq req){
        String url =searchUrl+ "/indexProperty/commitProperties";
        req.setUser(buildUserBase());
        return restTemplate.postForObject(url, req, JSONObject.class);
    }


    public List<PaiIndexPropertyDto> getShowProperties(IdUserReq req){

        String url =searchUrl+ "/indexProperty/getIndexShowProperties";
        req.setUser(buildUserBase());
        final PaiIndexPropertyDto[] propertyShowDtos = restTemplate.postForObject(url, req, PaiIndexPropertyDto[].class);


        return propertyShowDtos == null?new ArrayList<>():Arrays.asList(propertyShowDtos);
    }


    public List getSupportDataTypeList(){

        String url =searchUrl+ "/indexProperty/getSupportDataTypeList";
        final List supportDataTypeList = restTemplate.postForObject(url, null, List.class);


        return supportDataTypeList;
    }


    public void commitShowProperties(PropertyShowReq req){

        String url =searchUrl+ "/indexProperty/commitShowProperties";
        req.setUser(buildUserBase());
        restTemplate.postForObject(url, req, String.class);

    }

}
