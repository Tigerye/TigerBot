package com.tigerobo.x.pai.biz.serving.execute;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.tigerobo.x.pai.api.biz.entity.API;
import com.tigerobo.x.pai.api.biz.entity.Model;
import com.tigerobo.x.pai.api.dto.ApiDto;
import com.tigerobo.x.pai.api.enums.Style;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;


public class ContentUnderstandExecutor extends UriApiExecutor{

    Style style = Style.CONTENT_UNDERSTAND;
    public ContentUnderstandExecutor(ApiDto api) {
        super(api);
    }

    public JSONObject execute(Map<String, Object> params) {

        if (CollectionUtils.isEmpty(params)) {
            return null;
        }

        String question = (String)params.get("question");
        String context = (String)params.get("context");
        Preconditions.checkArgument(!StringUtils.isEmpty(question),"问题为空");
        Preconditions.checkArgument(!StringUtils.isEmpty(context),"阅读内容为空");

        Map<String, Object> quesReq = ImmutableMap.<String, Object>builder()
                .put("question", question)
                .put("context", context)
                .build();

        Map<String, Object> immutableMap = ImmutableMap.<String, Object>builder()
                .put("qa_list", Arrays.asList(quesReq))
                .build();

        JSONObject execute = (JSONObject)super.execute(immutableMap);
        if (execute == null) {
            return null;
        }
        JSONArray result =execute.getJSONArray("result");
        if (result == null||result.size()<=0){
            return null;
        }
        JSONObject jsonObject = result.getJSONObject(0);

        Integer start = jsonObject.getInteger("start");
        Integer end = jsonObject.getInteger("end");

        String innerText = jsonObject.getString("text");

        BigDecimal score = jsonObject.getBigDecimal("score");

        if (score!=null){
            score = score.setScale(4, RoundingMode.HALF_UP);
        }
        ContentUnderstandMo contentUnderstandMo = new ContentUnderstandMo(start,end,innerText,score);
        return JSON.parseObject(JSON.toJSONString(contentUnderstandMo));
    }

    @Override
    public Object batchExecute(Map<String, Object> params) {
        return null;
    }


    @Data
    @AllArgsConstructor
    public static class ContentUnderstandMo {
        Integer start;
        Integer end;
        String text;
        BigDecimal score;
    }


}
