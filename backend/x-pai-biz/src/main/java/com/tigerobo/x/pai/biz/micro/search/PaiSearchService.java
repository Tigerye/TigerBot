package com.tigerobo.x.pai.biz.micro.search;

import com.tigerobo.pai.search.api.base.UserBase;
import com.tigerobo.pai.search.api.req.search.PaiSearchRequest;
import com.tigerobo.pai.search.api.req.search.PaiSearchResponse;
import com.tigerobo.x.pai.api.enums.ModelCallSourceEnum;
import com.tigerobo.x.pai.api.serving.service.ApiService;
import com.tigerobo.x.pai.biz.base.EnvService;
import com.tigerobo.x.pai.biz.serving.ApiCountService;
import com.tigerobo.x.pai.biz.serving.ApiServiceImpl;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class PaiSearchService extends BaseUserCombineService{

    @Autowired
    private RestTemplate restTemplate;

    //    @Value("${pai.search.host:http://pai-test.tigerobo.com/search-service}")
    @Value("${pai.search.hostUrl:}")
    String searchUrl;

    @Autowired
    private EnvService envService;

    @Autowired
    private ApiCountService apiCountService;

    @Autowired
    private ApiServiceImpl apiService;
    public PaiSearchResponse search(PaiSearchRequest req) {
        return doSearch(req, ThreadLocalHolder.getUserId(),ModelCallSourceEnum.PAGE_EXECUTE.getType());
    }

    public PaiSearchResponse apiSearch(PaiSearchRequest req, Integer userId) {

        return doSearch(req,userId, ModelCallSourceEnum.INVOKE.getType());
    }


    private PaiSearchResponse doSearch(PaiSearchRequest req, Integer userId,Integer source) {

        req.setUser(buildUserBase(userId));
        final long start = System.currentTimeMillis();

        final PaiSearchResponse searchResponse = innerSearch(req);

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


    private PaiSearchResponse innerSearch(PaiSearchRequest req) {
        String url = searchUrl + "/search";
        final PaiSearchResponse response = restTemplate.postForObject(url, req, PaiSearchResponse.class);

        return response;
    }

}
