package com.tigerobo.x.pai.biz.ai.gpt;

import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.xm.similarity.text.CosineSimilarity;
import org.xm.similarity.text.TextSimilarity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GansuChatService {

    @Value("${pai.qaGpt.gs.sentence:}")
    private String gsSentence;
    @Value("${pai.qaGpt.gs.userIds:}")
    private String gsUserIds;
    @Value("${pai.qaGpt.gs.keywords:}")
    private String gsKeywords;
    @Value("#{${pai.api.gs.qaMap}}")
    Map<String, String> gsQaMap = new HashMap<>();

    public String getResult(String text) {
        if (!useGsExample(text)){
            return null;
        }
        return getFromGsQaResult(text);
    }

    private boolean useGsExample(String query) {

        if (StringUtils.isBlank(gsSentence)) {
            return false;
        }

        final Integer userId = ThreadLocalHolder.getUserId();
        if (userId != null && userId > 0 && StringUtils.isNotBlank(gsUserIds)) {

            final String[] split = gsUserIds.split("[,，]");

            if (Arrays.stream(split).collect(Collectors.toList()).contains(userId.toString())) {
                return true;
            }
        }
        if (StringUtils.isNotBlank(gsKeywords)) {
            final String[] keywords = gsKeywords.split("[,，]");
            for (String keyword : keywords) {
                if (StringUtils.isNotBlank(keyword) && query.contains(keyword)) {
                    return true;
                }
            }
        }
        return false;
    }


    private String getFromGsQaResult(String query) {
        final Map<String, String> qaMap = gsQaMap;

        if (qaMap == null || qaMap.isEmpty()) {
            return null;
        }

        LinkedHashMap<String, Double> keyScore = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : qaMap.entrySet()) {

            TextSimilarity cosSimilarity = new CosineSimilarity();

            double score = cosSimilarity.getSimilarity(entry.getKey(), query);
            keyScore.put(entry.getKey(), score);
        }


        final Map.Entry<String, Double> entry = keyScore.entrySet().stream().filter(e -> e.getValue() != null && e.getValue() > 0.55d).max(Map.Entry.<String, Double>comparingByValue()).orElse(null);


        if (entry != null) {
            log.info("gs -qa ,key-{},v-{}", entry.getKey(), entry.getValue());

            return qaMap.get(entry.getKey());
        } else {
            return null;
        }
//        samleTest();
    }

}
