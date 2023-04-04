package com.tigerobo.x.pai.biz.serving.execute;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tigerobo.x.pai.api.biz.entity.API;
import com.tigerobo.x.pai.api.biz.entity.Model;
import com.tigerobo.x.pai.api.dto.ApiDto;
import com.tigerobo.x.pai.api.enums.Style;
import com.tigerobo.x.pai.api.utils.MapUtils;
import com.tigerobo.x.pai.biz.utils.RestUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

@Data
public class TextCorrectExecutor extends UriApiExecutor {

    Style style = Style.TEXT_CORRECT;

    public TextCorrectExecutor(ApiDto api) {
        super(api);
    }


    public List<TextCorrectMo> batchExecute(Map<String, Object> params) {

        if (CollectionUtils.isEmpty(params)) {
            return null;
        }

        List<String> textLists = MapUtils.getList(params, "text_list", String.class);

        if (CollectionUtils.isEmpty(textLists)) {
            return new ArrayList();
        }
        Map<String, Object> req = new HashMap<>();
        req.put("text_list", textLists);

        String post = RestUtil.post(getApiUri(), req);
        JSONObject execute = JSON.parseObject(post);
//        JSONObject execute = (JSONObject) super.execute(req);

        if (execute == null) {
            return new ArrayList();
        }

        JSONArray result = execute.getJSONArray("result");
        if (result == null || result.size() <= 0) {
            return new ArrayList();
        }

        List<TextCorrectMo> targetList = new ArrayList<>();

        for (int x = 0; x < result.size(); x++) {
            JSONArray jsonArray = result.getJSONArray(0);
            String fixText = textLists.get(x);
            List<TextCorrectItemMo> list = new ArrayList<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                Integer start = jsonObject.getInteger("start");
                Integer end = jsonObject.getInteger("end");

                String innerText = jsonObject.getString("text");

                String correctedText = jsonObject.getString("corrected_text");
                String operation = jsonObject.getString("operation");
                String operationDetail = jsonObject.getString("operation_detail");
                if (start == null || end == null || innerText == null || correctedText == null) {
                    continue;
                }
                TextCorrectItemMo itemMo = new TextCorrectItemMo(start, end, innerText, correctedText,operation,operationDetail);
                list.add(itemMo);

                String preText = fixText.substring(0, start);
                String endText = fixText.substring(end);
                fixText = preText + correctedText + endText;
            }
            TextCorrectMo textCorrectMo = new TextCorrectMo();
            textCorrectMo.setFixText(fixText);
            textCorrectMo.setItems(list);

            targetList.add(textCorrectMo);
        }
        return targetList;
    }

    public JSONObject execute(Map<String, Object> params) {

        if (CollectionUtils.isEmpty(params)) {
            return null;
        }

        List<String> text_lists = MapUtils.getList(params, "text_list", String.class);

        String text = null;
        if (text_lists != null && text_lists.size() > 0) {
            text = text_lists.get(0);
        }
        if (StringUtils.isEmpty(text)) {
            text = MapUtils.getString(params, "text", null);
        }
        if (StringUtils.isEmpty(text)) {
            return null;
        }

        Map<String, Object> req = new HashMap<>();
        req.put("text_list", Arrays.asList(text));

        JSONObject execute = (JSONObject) super.execute(req);

        if (execute == null) {
            return null;
        }
        JSONArray result = execute.getJSONArray("result");
        if (result == null || result.size() <= 0) {
            return null;
        }
        JSONArray jsonArray = result.getJSONArray(0);
//        String fixText = text;
        List<TextCorrectItemMo> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            Integer start = jsonObject.getInteger("start");
            Integer end = jsonObject.getInteger("end");

            String innerText = jsonObject.getString("text");

            String correctedText = jsonObject.getString("corrected_text");
            String operation = jsonObject.getString("operation");
            String operationDetail = jsonObject.getString("operation_detail");

            if (start == null || end == null || innerText == null || correctedText == null) {
                continue;
            }
            TextCorrectItemMo itemMo = new TextCorrectItemMo(start, end, innerText, correctedText,operation,operationDetail);
            list.add(itemMo);

//            String preText = fixText.substring(0, start);
//            String endText = fixText.substring(end);
//            fixText = preText + correctedText + endText;
        }
        TextCorrectMo textCorrectMo = new TextCorrectMo();
//        textCorrectMo.setFixText(fixText);
        textCorrectMo.setFixText("");
        textCorrectMo.setItems(list);

        return JSON.parseObject(JSON.toJSONString(textCorrectMo));
    }


    @Data
    public static class TextCorrectMo {
        List<TextCorrectItemMo> items;
        private String fixText;
    }

    @Data
    @AllArgsConstructor
    public static class TextCorrectItemMo {
        Integer start;
        Integer end;
        String text;
        String correctedText;
        String operation;
        String operationDetail;
    }


}
