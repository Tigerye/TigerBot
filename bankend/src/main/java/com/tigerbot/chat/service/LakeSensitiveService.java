package com.tigerbot.chat.service;

import com.tigerbot.chat.api.dto.ModelLabel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Slf4j
@Service
public class LakeSensitiveService {


    @Value("${alg.user.appId}")
            private String appId;

    @Value("${alg.model.contentAudit.apiKey}")
    private String apiKey;


    @Value("${alg.user.accessToken}")
    private String accessToken;


    @Value("${pai.sensitive.isUseAml:true}")
    boolean useAml= false;


    @Value("${pai.sensitive.score:0.8}")
    String scoreLimit = "0.8";

    @Value("${pai.gpt.useSensitive:false}")
    private boolean useSensitive;
    @Value("${pai.sensitive.threshold:0.8}")
    private Float threshold;

    private final String normalLabel = "阅读";
    List<String> speLabels = Arrays.asList("其他有害", "淫秽色情","事故");

    
    @Autowired
    private AlgoletService algoletService;
    public boolean isSensitive(String text){
        if (!useSensitive){
            return false;
        }
        try {
            String label = getLabel(text);
            return StringUtils.isNotBlank(label);
        }catch (Exception ex){
            log.error("",ex);
        }
        return false;
    }

    public String getLabel(String text) {
        final ModelLabel label = getModelLabel(text);

        return getLabelName(label);
    }

    private ModelLabel getModelLabel(String text) {
        final ModelLabel label = doGetLabel(text);

        if (label == null||StringUtils.isBlank(label.getLabel())){
            return null;
        }

        final BigDecimal score = label.getScore();

        if (score.compareTo(new BigDecimal(threshold))<0){
            return null;
        }

        final String labelText = label.getLabel();
        if (speLabels.contains(labelText)){
            return label;
        }
        return null;
    }

    private String getLabelName(ModelLabel label) {
        if (label == null) {
            return null;
        }
        final String labelName = label.getLabel();
        if (normalLabel.equals(labelName)){
            return null;
        }
        final BigDecimal score = label.getScore();

        if (score.compareTo(new BigDecimal(scoreLimit)) < 0) {
            return null;
        }
//
        return labelName;
    }

    public ModelLabel doGetLabel(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("text", text);
        return algoletService.getAuditResult(text);
    }

}
