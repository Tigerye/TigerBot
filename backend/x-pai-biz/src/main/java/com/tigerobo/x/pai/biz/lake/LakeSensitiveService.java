package com.tigerobo.x.pai.biz.lake;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tigerobo.x.pai.api.dto.model.ModelLabel;
import com.tigerobo.x.pai.biz.serving.evaluate.entity.LabelResult;
import com.tigerobo.x.pai.biz.utils.RestUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LakeSensitiveService {

    String apiKey = "e2dc52a544af544b6e6b515b01d3d5b8";//hupu

    String apiUGCKey = "db506ea3f0d441208ed53e692a805d3e";//互联网ugc

    String url = "http://pai.tigerobo.com/x-pai-serving/invoke?appId=408031b1b5024c688ea19c904d8de44f&apiKey=" + apiKey + "&accessToken=7a1a5d8ee26b3c56970882194688cb51";
    @Value("${pai.sensitive.amlUrl:https://pai.tigerobo.com/x-pai-serving/aml/invoke?appId=ba78b5ba8483a0a40ad0b480b82b2916&apiKey=610933&accessToken=6dca8f4d1e0cef4cc5806a61cbf25f9f}")
    String amlUrl = "https://pai.tigerobo.com/x-pai-serving/aml/invoke?appId=ba78b5ba8483a0a40ad0b480b82b2916&apiKey=610933&accessToken=6dca8f4d1e0cef4cc5806a61cbf25f9f";

    @Value("${pai.sensitive.isUseAml:true}")
    boolean useAml= true;


    @Value("${pai.sensitive.score:0.8}")
    String scoreLimit = "0.85";


    List<String> senWords = Arrays.asList("习近平","毛泽东","江泽民","邓小平");

    List<String> labels = Arrays.asList("政治有害", "低俗擦边", "淫秽色情", "生态不良", "其他有害", "事故");

    List<String> speLabels = Arrays.asList("其他有害", "淫秽色情","事故");
//    List<String> labels = Arrays.asList("政治有害","淫秽色情");

    public String getLabel(String text) {
        return getHupuLabel(text);
    }

    public String getHupuLabel(String text) {
        final ModelLabel label = getHupuModelLabel(text);

        return getLabelName(label);
    }

    private ModelLabel getHupuModelLabel(String text) {
        final ModelLabel label = doGetLabel(text, amlUrl);

        if (label == null||StringUtils.isBlank(label.getLabel())){
            return null;
        }

        final BigDecimal score = label.getScore();

        if (score.compareTo(new BigDecimal("0.8"))<0){
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
        if ("阅读".equals(labelName)){
            return null;
        }
        final BigDecimal score = label.getScore();

        if (score.compareTo(new BigDecimal(scoreLimit)) < 0) {
            return null;
        }
//
        return labelName;
    }

    public ModelLabel doGetLabel(String text, String url) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("text", text);

        String resp = RestUtil.post(url, map);
        if (StringUtils.isBlank(resp)) {
            return null;
        }
        JSONObject jsonObject = JSON.parseObject(resp);
        JSONObject data = jsonObject.getJSONObject("data");

        if (data == null) {
            return null;
        }
        JSONArray result = data.getJSONArray("result");
        if (result == null||result.isEmpty()) {
            return null;
        }
        if (useAml){
            final JSONArray jsonArray = result.getJSONArray(0);

            if (jsonArray == null||jsonArray.isEmpty()) {
                return null;
            }
            return jsonArray.getObject(0,ModelLabel.class);
        }
        return result.getObject(0, ModelLabel.class);
    }

    public static void main(String[] args) {
        LakeSensitiveService sensitiveService = new LakeSensitiveService();
        System.out.println(sensitiveService.getHupuLabel("习近平在哪里"));
    }

}
