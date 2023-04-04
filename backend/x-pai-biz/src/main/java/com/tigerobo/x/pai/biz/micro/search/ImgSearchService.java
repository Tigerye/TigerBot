package com.tigerobo.x.pai.biz.micro.search;

import com.tigerobo.pai.search.api.req.search.ImgSearchResponse;
import com.tigerobo.pai.search.api.req.search.PaiSearchResponse;
import com.tigerobo.pai.search.api.req.search.img.IQReq;
import com.tigerobo.x.pai.api.enums.ModelCallSourceEnum;
import com.tigerobo.x.pai.biz.serving.ApiCountService;
import com.tigerobo.x.pai.biz.serving.ApiServiceImpl;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ImgSearchService extends SearchApiBaseService{



    @Autowired
    private ApiCountService apiCountService;

    @Autowired
    private ApiServiceImpl apiService;
    public ImgSearchResponse search(IQReq req) {
        return doSearch(req, ThreadLocalHolder.getUserId(),ModelCallSourceEnum.PAGE_EXECUTE.getType());
    }

    public ImgSearchResponse apiSearch(IQReq req, Integer userId) {

        return doSearch(req,userId, ModelCallSourceEnum.INVOKE.getType());
    }


    private ImgSearchResponse doSearch(IQReq req, Integer userId, Integer source) {

        log.info("IMG-SEARCH-userId:{}",userId);
        req.setUser(buildUserBase(userId));
        final long start = System.currentTimeMillis();

        final ImgSearchResponse searchResponse = innerSearch(req);

        final long delta = System.currentTimeMillis()-start;

        final String searchApiUuid = envService.getSearchApiUuid();
        if (StringUtils.isNotBlank(searchApiUuid)){
            apiCountService.incrApiCall(searchApiUuid);

            final String query = req.getQuery();
            final Integer indexId = req.getIndexId();
            apiService.add2es(searchApiUuid,delta,userId,source, query,"",String.valueOf(indexId));
        }

        return searchResponse;
    }


    private ImgSearchResponse innerSearch(IQReq req) {
        String url = searchUrl + "/img/search";
        final ImgSearchResponse response = restTemplate.postForObject(url, req, ImgSearchResponse.class);

        return response;
    }

}
