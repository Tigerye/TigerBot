package com.tigerobo.x.pai.biz.batch.offline;

import cn.hutool.core.text.csv.CsvData;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvRow;
import cn.hutool.core.text.csv.CsvUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.tigerobo.x.pai.api.enums.Style;
import com.tigerobo.x.pai.api.exception.APIException;
import com.tigerobo.x.pai.api.exception.ResultCode;
import com.tigerobo.x.pai.biz.io.ExcelWriteSugar;
import com.tigerobo.x.pai.biz.serving.Evaluable;
import com.tigerobo.x.pai.biz.serving.Executable;
import com.tigerobo.x.pai.biz.serving.evaluate.entity.EntityResult;
import com.tigerobo.x.pai.biz.serving.evaluate.entity.LabelResult;
import com.tigerobo.x.pai.biz.serving.evaluate.entity.TextRowItem;
import com.tigerobo.x.pai.biz.serving.execute.TextCorrectExecutor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
//import org.jsoup.internal.StringUtil;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description:
 * @modified By:
 * @version: $
 */
@Data
@Slf4j
public class TextEvaluator implements Evaluable {
    private final static int BATCH_SIZE_DEFAULT = 1;
    private final static int PIVOT_SIZE_DEFAULT = 10;
    ExcelWriteSugar<TextRowItem> excelWriteSugar;

    private Executable apiExecutor;


    private List<TextRowItem> resultPivot;
    private volatile int recordCnt;
    private int batchSize = BATCH_SIZE_DEFAULT;
    private int pivotSize = PIVOT_SIZE_DEFAULT;

    public static TextEvaluator create(Executable apiExecutor) {
        return create(apiExecutor, BATCH_SIZE_DEFAULT, PIVOT_SIZE_DEFAULT);
    }

    public static TextEvaluator create(Executable apiExecutor, int batchSize, int pivotSize) {
        Style style = Style.getByValue(apiExecutor.getApiStyle());
        if (!style.isSupportBatch()) {
            throw new APIException(ResultCode.VALIDATE_FAILED, "不支持批量测试", null);
        }

        TextEvaluator evaluator = new TextEvaluator();
        evaluator.setApiExecutor(apiExecutor);
        evaluator.setBatchSize(batchSize < 1 || batchSize > 1000 ? BATCH_SIZE_DEFAULT : batchSize);
        evaluator.setPivotSize(pivotSize < 1 || pivotSize > 100 ? PIVOT_SIZE_DEFAULT : pivotSize);
        evaluator.setList(new ArrayList<>(evaluator.getBatchSize()));
        evaluator.setResultPivot(new ArrayList<>(evaluator.getPivotSize()));
        return evaluator;
    }

    private List<TextRowItem> list;


    private void batchProcess() {
        if (recordCnt>=500000L){
//            return;
        }
        try {
            doBatchProcess();
        } catch (Exception e) {
            log.error("modelKey:{}",apiExecutor.getApiKey(), e);
            throw new IllegalArgumentException("批量处理");
        }finally {
            list.clear();
        }
    }

    private void doBatchProcess() throws Exception {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        Style style = Style.getByValue(apiExecutor.getApiStyle());
        if (style == null) {
            return;
        }
        Map<String, Object> params = Maps.newHashMap();
        params.put("text_list", list.stream().map(TextRowItem::getText).collect(Collectors.toList()));
        Object object = this.apiExecutor.batchExecute(params);


        if (style == Style.TEXT_CORRECT) {

            List<TextCorrectExecutor.TextCorrectMo> correctList = (List<TextCorrectExecutor.TextCorrectMo>) object;

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

            if (object == null){
                return;
            }
            JSONObject jsonObject = (JSONObject) object;
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

        // 设置前端数据透视
        for (int idx = 0; idx < this.list.size() && resultPivot.size() < this.getPivotSize(); idx++) {
            this.resultPivot.add(this.list.get(idx));
        }
        // 写入结果文件
//        this.excelWriter.write(this.list, this.writeSheet);

        excelWriteSugar.write(this.list);
        this.recordCnt += list.size();
        this.list.clear();
    }

    //2
    @Override
    public Object evaluate(File inputFile, File outputFile) {
        try {
            // 创建输出SheetWriter
            initExcelWriter(outputFile);
            // 读取Excel文件，并遍历处理
            String name = inputFile.getName();
            int index = name.lastIndexOf(".");
            String fileType = null;
            if (index > 0 && index < (name.length() - 1)) {
                fileType = name.substring(index + 1);
            }
            if ("csv".equalsIgnoreCase(fileType)) {
                dealOnCsv(inputFile);
            } else {
                EasyExcel.read(inputFile, new ExcelListener()).headRowNumber(1).sheet(0).doRead();
            }

        } finally {
            after();
        }
        return null;
    }

    private void dealOnCsv(File inputFile) {
        CsvReader reader = CsvUtil.getReader();
        CsvData data = reader.read(inputFile);

        if (data != null) {
            int i = 0;
            for (CsvRow item : data) {
                i++;
                if (i <= 1) {
                    continue;
                }

                int size = item.size();
                if (size <= 0) {
                    continue;
                }
                String content = item.get(0);
                if (StringUtils.isBlank(content)) {
                    continue;
                }
                TextRowItem rowItem = new TextRowItem();
                rowItem.setText(content);
                list.add(rowItem);
                if (list.size() >= getBatchSize()) {

                    batchProcess();
                    log.info("...csv-process: {} -> {}", recordCnt - getBatchSize(), recordCnt);
                }
            }
            if (list.size() > 0) {
                batchProcess();
                log.info("...csv-process: {} -> {}", recordCnt - getBatchSize(), recordCnt);
            }
        }
    }

    private void initExcelWriter(File outputFile) {
        excelWriteSugar = new ExcelWriteSugar<>();
        excelWriteSugar.initWriter(outputFile.getPath(), TextRowItem.class, "模型结果");
    }

    private void after() {

        if (excelWriteSugar != null) {
            excelWriteSugar.finish();
        }
    }

    private class ExcelListener extends AnalysisEventListener<LinkedHashMap<Integer, String>> {

        int index = 0;

        @Override
        public void invoke(LinkedHashMap<Integer, String> input, AnalysisContext context) {

            String sentence = input.get(0);
            if (StringUtils.isBlank(sentence)) {
                index++;
                return;
            }

            index++;
            TextRowItem item = new TextRowItem();
            item.setText(sentence);
            list.add(item);
            if (list.size() >= getBatchSize()) {

                batchProcess();
                if (recordCnt%100==0){
                    log.info("...xls-process: {} -> {}", recordCnt - getBatchSize(), recordCnt);
                }

            }
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            try {
                batchProcess();
            } catch (Exception ex) {
                log.error("", ex);
                throw new IllegalArgumentException("服务异常");
            }
            log.info("...xls-total process: {}", recordCnt);
        }
    }

}
