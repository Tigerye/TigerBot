package com.tigerobo.x.pai.biz.serving.execute;

import com.alibaba.fastjson.JSONObject;
import com.tigerobo.x.pai.api.biz.entity.API;
import com.tigerobo.x.pai.api.biz.entity.Model;
import com.tigerobo.x.pai.api.dto.ApiDto;
import com.tigerobo.x.pai.api.enums.Style;
import com.tigerobo.x.pai.api.utils.MapUtils;
import lombok.Data;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ProduceQAExecutor extends UriApiExecutor {

    Style style = Style.TEXT_TO_QA;

    public ProduceQAExecutor(ApiDto api) {
        super(api);
    }


    public JSONObject execute(Map<String, Object> params) {

        if (CollectionUtils.isEmpty(params)) {
            return null;
        }

        List<String> text_lists = MapUtils.getList(params, "text_list", String.class);
        if (CollectionUtils.isEmpty(text_lists)) {
            text_lists = new ArrayList<>();
            String text = MapUtils.getString(params, "text", null);
            if (!StringUtils.isEmpty(text)) {
                text_lists.add(text);
            }
        }

        if (CollectionUtils.isEmpty(text_lists)) {
            return null;
        }

        Map<String, Object> req = new HashMap<>();
        req.put("text_list", text_lists);
        JSONObject execute = (JSONObject) super.execute(req);
        return execute;
    }


}
