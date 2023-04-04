package com.tigerobo.x.pai.biz.serving;

import com.tigerobo.x.pai.biz.biz.process.ApiProcessor;
import com.tigerobo.x.pai.biz.biz.service.ApiBaseService;
import com.tigerobo.x.pai.dal.biz.dao.ApiDao;
import com.tigerobo.x.pai.dal.biz.entity.ApiDo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
@Service
public class ApiSumService {


    @Autowired
    private ApiBaseService apiBaseService;
    @Autowired
    private ApiProcessor apiProcessor;

    public Integer getApiTotal() {

        List<ApiDo> onlineListCache = apiBaseService.getOnlineListCache();

        if (CollectionUtils.isEmpty(onlineListCache)) {
            return 0;
        }

        Integer get = onlineListCache.parallelStream().map(api -> {
            return this.apiProcessor.doGetTotal(api.getModelUuid(), api.getAmlRelIds());
        }).reduce((a, b) -> a + b).get();

        return get;
    }

}
