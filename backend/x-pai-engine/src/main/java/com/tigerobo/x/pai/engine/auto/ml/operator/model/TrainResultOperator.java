package com.tigerobo.x.pai.engine.auto.ml.operator.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tigerobo.x.pai.api.aml.engine.dto.ai.model.text.classify.TextClassifyTrainItemDto;
import com.tigerobo.x.pai.api.aml.engine.dto.content.OpContent;
import com.tigerobo.x.pai.api.aml.engine.dto.train.AmlTrainInputContent;
import com.tigerobo.x.pai.api.aml.engine.dto.train.TrainResultDto;
import com.tigerobo.x.pai.biz.utils.FileReadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;
@Slf4j
public class TrainResultOperator {

    List<String> jsonKeys =  Arrays.asList("id2label","thresholds","precision",
            "recall","average_precision","confusion_matrix","predicts");

    String resultFile = "test_results.json";
    String macro = "micro";
    String macroName = "所有项目";
    int stepSize = 100;
    int defaultIndex = 49;

    String evaluationName = "evaluation/evaluation.json";
    String predictName = "evaluation/predict.json";

    List<Float> thresholdStepList = new ArrayList<>();

    private int max_confuse_matrix_num = 10;


    public TrainResultDto run(AmlTrainInputContent inputContent, OpContent opContent)throws Exception{
        TrainResultDto trainResultDto = new TrainResultDto();

        String resultPath = inputContent.getResultFilePath();
        initThresholdList();
        String json = FileReadUtil.readOneLineContent(resultPath);

        if (StringUtils.isEmpty(json)){
            return null;
        }
        JSONObject jsonObject = JSON.parseObject(json);

        LinkedHashMap<String, List<TrainResultDto.ThresholdItem>> labelThresholdMap = getThresholdMap(jsonObject);
        trainResultDto.setLabelConfidenceThresholdMap(labelThresholdMap);

        JSONObject id2label = jsonObject.getJSONObject("id2label");
        LinkedHashMap<String,String> labelMap = initLabelMap(id2label);
        trainResultDto.setLabelMap(labelMap);

        List<TrainResultDto.ConfuseLine> lineList = callConfuseMatrix(jsonObject,labelMap);
        trainResultDto.setConfuseMatrix(lineList);

        List<TrainResultDto.LabelItem> labelItems = calMatrixLabels(lineList);
        trainResultDto.setConfuseMatrixLabelList(labelItems);


        List<TrainResultDto.LabelItem> avgItems = initAvgPrecision(jsonObject,labelMap);
        trainResultDto.setAvgPrecisionList(avgItems);
        writePredict(opContent, trainResultDto, jsonObject);
        writeEvaluation(trainResultDto,opContent);
        trainResultDto.setResultPath(resultPath);
        return trainResultDto;
    }

    private List<TrainResultDto.LabelItem> calMatrixLabels(List<TrainResultDto.ConfuseLine> lineList){
        if (lineList == null){
            return new ArrayList<>();
        }
        return lineList.stream().map(line -> {
            TrainResultDto.LabelItem item = new TrainResultDto.LabelItem();
            item.setKey(line.getKey());
            item.setName(line.getName());
            return item;
        }).collect(Collectors.toList());

    }

    private void writePredict(OpContent opContent, TrainResultDto trainResultDto, JSONObject jsonObject) throws IOException {
        JSONArray predicts = jsonObject.getJSONArray("predicts");

        String predictPath = opContent.getOutputRoot()+predictName;
        initParentFile(predictPath);
        try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(predictPath)))){
            for (int i = 0; i < predicts.size(); i++) {
                JSONArray jsonArray = predicts.getJSONArray(i);
                writer.write(JSON.toJSONString(jsonArray));
                writer.newLine();
            }
            writer.flush();
        }
        trainResultDto.setPredictPath(predictPath);
    }

    private void initParentFile(String path){
        File file = new File(path);
        if (file.getParentFile().exists()){
            return;
        }
        file.getParentFile().mkdirs();
    }

    private void writeEvaluation(TrainResultDto resultDto, OpContent opContent) throws IOException {
        String outputRoot = opContent.getOutputRoot();

        String evaluationPath = outputRoot+evaluationName;

        File file = new File(evaluationPath);
        if (!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(evaluationPath)))){
            resultDto.setEvaluationPath(evaluationPath);
            writer.write(JSON.toJSONString(resultDto));
            writer.flush();
        }

    }

    private LinkedHashMap<String,String> initLabelMap(JSONObject id2label){
        TreeMap<Integer,String> map = new TreeMap<>();


        for (Map.Entry<String, Object> entry : id2label.entrySet()) {
            String key = entry.getKey();
            String value = (String)entry.getValue();
            map.put(Integer.parseInt(key),value);
        }

        LinkedHashMap<String,String> linkedHashMap = new LinkedHashMap<>();
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            linkedHashMap.put(String.valueOf(entry.getKey()),entry.getValue());
        }
        return linkedHashMap;
    }

    private List<TrainResultDto.LabelItem> initAvgPrecision(JSONObject jsonObject,LinkedHashMap<String,String> id2label){

        JSONObject averagePrecision = jsonObject.getJSONObject("average_precision");

        BigDecimal macroValue = averagePrecision.getBigDecimal(macro);
        if (macroValue!=null){
            macroValue = macroValue.setScale(5,BigDecimal.ROUND_UP);
        }

        List<TrainResultDto.LabelItem> avgList = new ArrayList<>();

        TrainResultDto.LabelItem macroItem = new TrainResultDto.LabelItem();
        macroItem.setKey(macro);
        macroItem.setAvgPrecision(macroValue);
        macroItem.setName(macroName);
        avgList.add(macroItem);
        for (Map.Entry<String, String> entry : id2label.entrySet()) {

            String key = entry.getKey();
            String name =(String) entry.getValue();
            BigDecimal value = averagePrecision.getBigDecimal(key);
            if (value!=null){
                value = value.setScale(5,RoundingMode.HALF_UP);
            }

            TrainResultDto.LabelItem item = new TrainResultDto.LabelItem();
            item.setName(name);
            item.setKey(key);
            item.setAvgPrecision(value);
            avgList.add(item);
        }
        return avgList;
    }

    private List<TrainResultDto.ConfuseLine> callConfuseMatrix(JSONObject jsonObject,LinkedHashMap<String,String> labelMap){

        JSONArray confusionMatrix = jsonObject.getJSONArray("confusion_matrix");

        if (confusionMatrix == null){
            return new ArrayList<>();
        }
        List<TrainResultDto.ConfuseLine> lineList = new ArrayList<>();

        for (int i = 0; i < confusionMatrix.size(); i++) {

            String key = String.valueOf(i);
            String label = labelMap.get(key);
            JSONArray jsonArray = confusionMatrix.getJSONArray(i);

            List<TrainResultDto.ConfuseItem> items = new ArrayList<>();
            int lineCount = 0;
            for (int j = 0; j < jsonArray.size(); j++) {

                Integer count = Optional.of(jsonArray.getInteger(j)).orElse(0);
                String targetKey = String.valueOf(j);
                String targetLabel = labelMap.get(targetKey);
                lineCount += count;
                TrainResultDto.ConfuseItem confuseItem = new TrainResultDto.ConfuseItem();
                confuseItem.setCount(count);
                confuseItem.setTargetKey(targetKey);
                confuseItem.setTargetLabel(targetLabel);
                items.add(confuseItem);
            }
            if (lineCount>0){
                for (TrainResultDto.ConfuseItem item : items) {
                    int count = item.getCount();
                    BigDecimal divide = new BigDecimal(count * 100).divide(new BigDecimal(lineCount), 2, RoundingMode.HALF_UP);
                    int rate = divide.setScale(0,RoundingMode.HALF_UP).intValue();
                    item.setRate(rate);
                    item.setAccurateRate(divide);
                }
            }
            TrainResultDto.ConfuseLine confuseLine = new TrainResultDto.ConfuseLine();
            confuseLine.setKey(key);
            confuseLine.setName(label);
            confuseLine.setLineCount(lineCount);
            confuseLine.setChildren(items);
            lineList.add(confuseLine);
        }

//        return lineList;
        List<TrainResultDto.ConfuseLine> resultLineList = cutConfuseMatrix(lineList);
        return resultLineList;
    }

    private List<TrainResultDto.ConfuseLine> cutConfuseMatrix(List<TrainResultDto.ConfuseLine> preLineList){
        boolean cut = false;
        if (!cut){
            return preLineList;
        }
        if (preLineList.size()<=max_confuse_matrix_num){
            return preLineList;
        }

        Map<String,BigDecimal> keyRateMap = new LinkedHashMap<>();
        for (int i = 0; i < preLineList.size(); i++) {
            TrainResultDto.ConfuseLine confuseLine = preLineList.get(i);

            List<TrainResultDto.ConfuseItem> children = confuseLine.getChildren();
            String key = confuseLine.getKey();

            for (TrainResultDto.ConfuseItem child : children) {
                if (key.equals(child.getTargetKey())){

                    keyRateMap.put(key,child.getAccurateRate());
                    break;
                }
            }
        }

        List<String> keys = keyRateMap.entrySet().stream().sorted(Map.Entry.comparingByValue()).limit(max_confuse_matrix_num)
                .map(Map.Entry::getKey).collect(Collectors.toList());

        List<TrainResultDto.ConfuseLine> targetLines = new ArrayList<>();
        for (TrainResultDto.ConfuseLine preLine : preLineList) {
            String key = preLine.getKey();
            if (!keys.contains(key)){
                continue;
            }


            List<TrainResultDto.ConfuseItem> children = preLine.getChildren();
            List<TrainResultDto.ConfuseItem> targetChildren = new ArrayList<>();

            for (TrainResultDto.ConfuseItem child : children) {
                if (keys.contains(child.getTargetKey())){
                    targetChildren.add(child);
                }
            }
            TrainResultDto.ConfuseLine useLine = new TrainResultDto.ConfuseLine();
            useLine.setKey(key);
            useLine.setName(preLine.getName());
            useLine.setChildren(targetChildren);
            useLine.setLineCount(preLine.getLineCount());

            targetLines.add(useLine);
        }
        return targetLines;
    }

    private LinkedHashMap<String, List<TrainResultDto.ThresholdItem>> getThresholdMap(JSONObject jsonObject) {
        JSONObject id2label = jsonObject.getJSONObject("id2label");//id->label
        for (Map.Entry<String, Object> entry : id2label.entrySet()) {
            String key = entry.getKey();
            int id = Integer.parseInt(key);
            String label = (String)entry.getValue();
        }

        JSONObject thresholds = jsonObject.getJSONObject("thresholds");
        Map<String, List<Integer>> labelStepIndexMap = labelThresholdMap(id2label, thresholds);

        JSONObject precision = jsonObject.getJSONObject("precision");
        Map<String, List<BigDecimal>> labelPrecisionValueMap = getLabelValueMap(labelStepIndexMap, precision);
        JSONObject recall = jsonObject.getJSONObject("recall");
        Map<String, List<BigDecimal>> labelRecallValueMap = getLabelValueMap(labelStepIndexMap, recall);


        LinkedHashMap<String,List<TrainResultDto.ThresholdItem>> map = new LinkedHashMap<>();
        for (Map.Entry<String, List<BigDecimal>> entry : labelPrecisionValueMap.entrySet()) {

            String key = entry.getKey();
            List<BigDecimal> precisionList = entry.getValue();
            List<BigDecimal> recallList = labelRecallValueMap.get(key);

            List<TrainResultDto.ThresholdItem> thresholdItemList = new ArrayList<>();

            for (int i = 0; i < thresholdStepList.size(); i++) {
                Float threshold = thresholdStepList.get(i);
                BigDecimal precisionValue = precisionList.get(i);
                BigDecimal recallValue = recallList.get(i);

                TrainResultDto.ThresholdItem thresholdItem = new TrainResultDto.ThresholdItem();
                thresholdItem.setPrecision(precisionValue);
                thresholdItem.setRecall(recallValue);
                BigDecimal value = new BigDecimal(threshold).setScale(2, RoundingMode.HALF_UP);
                thresholdItem.setThreshold(value);
                thresholdItemList.add(thresholdItem);
            }

            map.put(key,thresholdItemList);
        }
        return map;
    }
    private Map<String, List<BigDecimal>> getLabelValueMap(Map<String, List<Integer>> labelStepIndexMap, JSONObject precision) {
        Map<String,List<BigDecimal>> labelValueMap = new LinkedHashMap<>();


        for (Map.Entry<String, List<Integer>> indexEntry : labelStepIndexMap.entrySet()) {

            String key = indexEntry.getKey();
            List<Integer> indexList = indexEntry.getValue();
            JSONArray jsonArray = precision.getJSONArray(key);

            List<BigDecimal> values = new ArrayList<>();

            for (Integer i : indexList) {
                try {
                    BigDecimal bigDecimal = jsonArray.getBigDecimal(i);
                    BigDecimal value = bigDecimal ==null?BigDecimal.ZERO:bigDecimal.setScale(5,RoundingMode.HALF_UP);
                    values.add(value);
                }catch (Exception ex){
                    log.error("",ex);
                    throw new RuntimeException(ex);
                }
            }
            labelValueMap.put(key,values);
        }
        return labelValueMap;
    }

    private List<TextClassifyTrainItemDto> getTestList(BufferedReader reader) throws IOException {
        String line = null;
        List<TextClassifyTrainItemDto> itemDtos = new ArrayList<>();
        while ((line = reader.readLine())!=null){
            TextClassifyTrainItemDto item = JSON.parseObject(line, TextClassifyTrainItemDto.class);
            if (item!=null){
                itemDtos.add(item);
            }
        }
        return itemDtos;
    }


    private Map<String,List<Integer>> labelThresholdMap(JSONObject id2label,JSONObject thresholds){
        Map<String,List<Integer>> labelStepIndexMap = new LinkedHashMap<>();
        for (String labelKey : id2label.keySet()) {
            JSONArray thresholdJsonArray = thresholds.getJSONArray(labelKey);
            List<Integer> indexList = getIndexList(thresholdJsonArray);
            labelStepIndexMap.put(labelKey,indexList);
        }
        JSONArray macroArray = thresholds.getJSONArray(macro);
        List<Integer> indexList = getIndexList(macroArray);
        labelStepIndexMap.put(macro,indexList);
        return labelStepIndexMap;
    }

    private void initThresholdList(){
        float step = 0.01f;
        float item = 0.01f;
        for (int i = 0;i<100;i++){
            thresholdStepList.add(item);
            item+=step;
        }
    }

    private List<Integer> getIndexList(JSONArray thresholdJsonArray){

        List<Integer> stepIndexList = new ArrayList<>();
        int preIndex = 0;
        for (int i = 0; i < thresholdStepList.size(); i++) {
            Float current = thresholdStepList.get(i);

            int nearestItem = findNearestItem(current, preIndex, thresholdJsonArray);
            stepIndexList.add(nearestItem);
        }
        return stepIndexList;
    }

    private int findNearestItem(Float current,int startIndex,JSONArray jsonArray){

        BigDecimal value = new BigDecimal(current);
        BigDecimal pre = null;
        int index = 0;
        boolean findBig = false;
        try {
            for (int i = startIndex; i < jsonArray.size(); i++) {
                BigDecimal item = jsonArray.getBigDecimal(i);
                if (item.compareTo(value) > 0) {
                    findBig = true;
                    break;
                } else {
                    pre = item;
                    index = i;
                }
            }
            if (!findBig) {
                index = jsonArray.size();
            }
        }catch (Exception ex){

            System.out.println("");
            throw new RuntimeException(ex);
        }
        return index;
    }
}
