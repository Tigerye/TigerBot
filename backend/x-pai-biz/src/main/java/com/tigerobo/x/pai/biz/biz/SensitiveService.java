package com.tigerobo.x.pai.biz.biz;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SensitiveService {

    @Value("${pai.sensitive.keywords:}")
    private String keywords;
    @Value("${pai.sensitive.useKeywords:false}")
    private boolean useKeywords;

    public boolean sensitive(String text){

        if (!useKeywords){
            return false;
        }

        String sensitiveWords = keywords;
        if (StringUtils.isBlank(sensitiveWords)){
            return false;
        }

        final List<String> words = Arrays.stream(sensitiveWords.split(","))
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());

        for (String word : words) {
            if (text.contains(word)){
                return true;
            }
        }
        return false;
    }

}
