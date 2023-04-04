package com.tigerobo.x.pai.biz.serving.execute;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tigerobo.x.pai.api.biz.entity.API;
import com.tigerobo.x.pai.api.biz.entity.Model;
import com.tigerobo.x.pai.api.dto.ApiDto;
import com.tigerobo.x.pai.api.enums.Style;
import com.tigerobo.x.pai.api.dto.model.ModelLabel;
import com.tigerobo.x.pai.api.utils.MapUtils;
import lombok.Data;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ZeroSampleLabelExecutor extends UriApiExecutor {

    Style style = Style.ZERO_SAMPLE_LABEL;

    public ZeroSampleLabelExecutor(ApiDto api) {
        super(api);
    }


    public Object execute(Map<String, Object> params) {

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
        List<String> candidateLabels = MapUtils.getList(params, "candidate_labels", String.class);

        if (CollectionUtils.isEmpty(candidateLabels)) {
            return null;
        }
        Map<String, Object> req = new HashMap<>();
        req.put("text_list", text_lists);
        req.put("candidate_labels", candidateLabels);

        JSONObject execute = (JSONObject) super.execute(req);
        if (execute == null) {
            return null;
        }



        List<List<ModelLabel>> allLabels = new ArrayList<>();

        Short status = execute.getShort("status");

        String msg = "";
        if (status != null) {
            if (status == 0) {
                JSONArray resultArray = execute.getJSONArray("result");
                if (resultArray != null) {
                    for (int i = 0; i < resultArray.size(); i++) {
                        JSONArray jsonArray = resultArray.getJSONArray(i);
                        List<ModelLabel> labels = JSONObject.parseArray(jsonArray.toJSONString(), ModelLabel.class);

                        for (ModelLabel label : labels) {
                            BigDecimal score = label.getScore();

                            if (score!=null){
                                score = score.setScale(4, RoundingMode.HALF_UP);
                                label.setScore(score);
                            }
                        }

                        allLabels.add(labels);
                    }
                }
            } else {
                msg = execute.getString("msg");
            }
        }else {
            msg="服务异常";
        }
        JSONObject outMap = new JSONObject();

        if(!CollectionUtils.isEmpty(allLabels)){
            outMap.put("status",0);
            outMap.put("result",allLabels);
        }else {
            outMap.put("status",-1);
            outMap.put("msg",msg);
        }

        return outMap;
    }


}
