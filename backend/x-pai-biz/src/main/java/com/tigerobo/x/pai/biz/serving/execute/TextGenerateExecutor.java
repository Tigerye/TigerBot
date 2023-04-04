package com.tigerobo.x.pai.biz.serving.execute;

import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
public class TextGenerateExecutor extends UriApiExecutor {

    Style style = Style.TEXT_GENERATE;

    public TextGenerateExecutor(ApiDto api) {
        super(api);
    }


    public List<String> batchExecute(Map<String, Object> params) {

        if (CollectionUtils.isEmpty(params)) {
            return null;
        }

        List<String> textLists = MapUtils.getList(params, "text_list", String.class);

        if (CollectionUtils.isEmpty(textLists)) {
            return new ArrayList();
        }
        Map<String, Object> req = new HashMap<>();
        req.put("text_list", textLists);


        final Integer max_length = MapUtil.getInt(params, "max_length",256);

        req.put("max_length",max_length);

        String post = RestUtil.post(getApiUri(), req);
        JSONObject execute = JSON.parseObject(post);
        return getList(execute);
    }

    private List<String> getList(JSONObject execute ) {

//        JSONObject execute = (JSONObject) super.execute(req);
        if (execute == null) {
            return new ArrayList();
        }

        JSONArray result = execute.getJSONArray("result");
        if (result == null || result.size() <= 0) {
            return new ArrayList();
        }


        List<String> list = new ArrayList<>();
        for (int i = 0; i < result.size(); i++) {
            list.add(result.getString(i));
        }

        return list;
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

        final Integer max_length = MapUtil.getInt(params, "max_length",256);

        req.put("max_length",max_length);
        JSONObject execute = (JSONObject) super.execute(req);
        return execute;
    }



}
