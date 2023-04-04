package com.tigerobo.x.pai.biz.batch.offline;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tigerobo.x.pai.api.enums.Style;
import com.tigerobo.x.pai.biz.serving.Executable;
import com.tigerobo.x.pai.biz.serving.evaluate.entity.EntityResult;
import com.tigerobo.x.pai.biz.serving.evaluate.entity.LabelResult;
import com.tigerobo.x.pai.biz.serving.evaluate.entity.TextRowItem;
import com.tigerobo.x.pai.biz.serving.execute.TextCorrectExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
@Component
public class BatchResultHandle {


    public void dealResult(BatchContext context){
        Executable apiExecutor = context.getEnv().getExecutor();
        Object apiResult = context.env.apiResult;
        Style style = Style.getByValue(apiExecutor.getApiStyle());

        final List<TextRowItem> list = context.getResult().getList();

        if (style == Style.TEXT_CORRECT) {

            List<TextCorrectExecutor.TextCorrectMo> correctList =
                    (List<TextCorrectExecutor.TextCorrectMo>) apiResult;

            if (CollectionUtils.isEmpty(correctList) && correctList.size() >= list.size()) {
                return;
            }
            for (int i = 0; i < list.size(); i++) {
                TextRowItem textRowItem = list.get(i);
                TextCorrectExecutor.TextCorrectMo textCorrectMo = correctList.get(i);
                if (textCorrectMo != null) {
                    textRowItem.setResultText(textCorrectMo.getFixText());
                    textRowItem.setResult(textCorrectMo);
                }
            }

        } else {

            if (apiResult == null){
                return;
            }
            JSONObject jsonObject = (JSONObject) apiResult;
            JSONArray result = jsonObject.getJSONArray("result");
            if (result == null) {
                JSONObject data = jsonObject.getJSONObject("data");
                if (data != null) {
                    result = data.getJSONArray("result");
                }
            }
            if (result == null || result.isEmpty()) {
                return;
            }

            // 处理 TEXT_TO_LABE

            if (style == Style.TEXT_TO_LABEL) {
                List<LabelResult> resultList = JSON.parseArray(JSON.toJSONString(result), LabelResult.class);
                for (int idx = 0; idx < list.size(); idx++) {
                    TextRowItem rowItem = list.get(idx);
                    LabelResult labelResult = resultList.get(idx);
                    LabelResult labels = labelResult;
                    rowItem.setResultText(labels.toText());
                    rowItem.setResultJson(JSON.toJSONString(labelResult));
                    rowItem.setResult(labelResult);
                }
            }
            // 处理 TEXT_TO_TEXT
            if (style == Style.TEXT_TO_TEXT) {
                List<String> resultList = JSON.parseArray(JSON.toJSONString(result), String.class);
                for (int idx = 0; idx < list.size(); idx++) {
                    TextRowItem rowItem = list.get(idx);
                    rowItem.setResultText(resultList.get(idx));
                    rowItem.setResult(resultList.get(idx));
                }
            }
            // 处理 TEXT_TO_ENTITY
            if (style == Style.TEXT_TO_ENTITY||style == Style.TEXT_TO_ENTITY_REVIEW) {
                List<EntityResult> resultList = JSON.parseArray(JSON.toJSONString(result), EntityResult.class);
                for (int idx = 0; idx < list.size(); idx++) {
                    TextRowItem rowItem = list.get(idx);
                    String text = rowItem.getText();

                    EntityResult entityResult = resultList.get(idx);
                    rowItem.setResultText(entityResult.toText(text));
                    rowItem.setResult(resultList.get(idx));
                }
            }
        }


        final List<TextRowItem> resultPivot = context.getResult().getResultPivot();

        final int pivotSize = context.getProcess().getPivotSize();

        // 设置前端数据透视
        for (int idx = 0; idx < list.size() && resultPivot.size() < pivotSize; idx++) {
            resultPivot.add(list.get(idx));
        }

        // 写入结果文件
        context.getEnv().getExcelWriteSugar().write(list);

        list.clear();
    }
}
