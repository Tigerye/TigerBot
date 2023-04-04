package com.tigerobo.x.pai.biz.ai.gpt.model;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GptModelService extends GptModelBaseService {

    @Value("${pai.gpt.url:http://gbox8.aigauss.com:9200/infer,http://gbox8.aigauss.com:9201/infer,http://gbox8.aigauss.com:9202/infer,http://gbox8.aigauss.com:9203/infer}")
    private List<String> gptUrlList;

    public void setGptUrlList(String text){
        if (StringUtils.isBlank(text)){
            return;
        }
        gptUrlList = Arrays.stream(text.split(","))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
    }

    @Override
    protected List<String> getConfigUrls() {
        return gptUrlList;
    }
}
