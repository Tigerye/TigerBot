package com.tigerobo.x.pai.biz.micro.search;

import com.tigerobo.pai.search.api.base.UserBase;
import com.tigerobo.pai.search.api.dto.UserIndexDto;
import com.tigerobo.pai.search.api.req.UserAddIndexReq;
import com.tigerobo.pai.search.api.req.application.ApplicationPageReq;
import com.tigerobo.pai.search.api.req.base.IdUserReq;
import com.tigerobo.pai.search.api.req.base.PageVo;
import com.tigerobo.pai.search.api.req.base.UserReqPage;
import com.tigerobo.pai.search.api.req.index.IndexPageReq;
import com.tigerobo.pai.search.api.req.index.IndexUpdateReq;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class SearchIndexService {

    @Autowired
    private RestTemplate restTemplate;


    @Value("${pai.search.hostUrl:}")
    String searchUrl;


    public UserIndexDto createIndex(UserAddIndexReq req) {


        String url = searchUrl + "/index/createIndex";
        req.setUser(buildUserBase());
        final UserIndexDto map = restTemplate.postForObject(url, req, UserIndexDto.class);

        return map;
    }

    public void deleteIndex(IdUserReq req) {


        String url = searchUrl + "/index/deleteIndex";
        req.setUser(buildUserBase());
        Boolean result = restTemplate.postForObject(url, req, Boolean.class);

        Validate.isTrue(result != null && result, "删除索引失败");
    }

    public void update(IndexUpdateReq req){

        String url = searchUrl + "/index/update";
        req.setUser(buildUserBase());
        restTemplate.postForObject(url, req, Boolean.class);
    }

    public PageVo<UserIndexDto> getUserIndexPage(IndexPageReq req) {
        String url = searchUrl + "/index/getUserIndexPage";

        req.setUser(buildUserBase());
        PageVo pageVo = restTemplate.postForObject(url, req, PageVo.class);
        return pageVo;

    }

    public UserIndexDto getUserIndex(IdUserReq req) {
        req.setUser(buildUserBase());
        String url = searchUrl + "/index/getUserIndex";
        final UserIndexDto dto = restTemplate.postForObject(url, req, UserIndexDto.class);
        return dto;
    }

    private UserBase buildUserBase() {
        final Integer userId = ThreadLocalHolder.getUserId();

        if (userId != null && userId > 0) {
            return new UserBase(userId);
        }
        return null;
    }
}
