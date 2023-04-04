package com.tigerobo.x.pai.biz.aml.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.tigerobo.x.pai.api.aml.constant.AmlEngineConstant;
import com.tigerobo.x.pai.api.aml.dto.ModelDataItem;
import com.tigerobo.x.pai.api.aml.dto.evaluate.EvaSentence;
import com.tigerobo.x.pai.api.aml.dto.evaluate.EvaluationConfidenceDto;
import com.tigerobo.x.pai.api.aml.engine.dto.train.TrainResultDto;
import com.tigerobo.x.pai.api.aml.enums.EvaluationDataTypeEnum;
import com.tigerobo.x.pai.api.aml.req.AmlConfidenceEvaluationReq;
import com.tigerobo.x.pai.api.entity.Pair;
import com.tigerobo.x.pai.api.enums.AmlStatusEnum;
import com.tigerobo.x.pai.biz.aml.EvaluationDataTypeUtil;
import com.tigerobo.x.pai.biz.biz.OssService;
import com.tigerobo.x.pai.biz.io.ExcelWriteSugar;
import com.tigerobo.x.pai.dal.aml.dao.AmlModelDao;
import com.tigerobo.x.pai.dal.aml.entity.AmlModelDo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Slf4j
@Service
public class AmlEvaluationServiceImpl {


    @Autowired
    private AmlModelDao amlModelDao;

    @Autowired
    private OssService ossService;
    public List<TrainResultDto.LabelItem> viewStatisticEvaluation(Integer modelId) {
        AmlModelDo model = amlModelDao.getById(modelId);
        if (model == null) {
            return null;
        }
        Byte status = model.getStatus();
        if (!AmlStatusEnum.TRAIN_SUCCESS.getStatus().equals(status)) {
            return null;
        }
        String resultPath = model.getResultPath();

        JSONObject jsonObject = JSON.parseObject(resultPath);
        String evaluationPath = jsonObject.getString(AmlEngineConstant.evaluationPathKey);
        TrainResultDto trainResult = getTrainResult(evaluationPath);
        if (trainResult == null) {
            return null;
        }

//        LinkedHashMap<String, String> labelMap = trainResult.getLabelMap();
        List<TrainResultDto.LabelItem> avgPrecisionList = trainResult.getAvgPrecisionList();
        return avgPrecisionList;
    }

    public PageInfo<EvaSentence> viewEvaluationTypePage(Integer modelId, AmlConfidenceEvaluationReq req) {
        String labelKey = req.getLabelKey();
        String labelName = req.getLabelName();

        Preconditions.checkArgument(req.getId()!=null,"未传id");
        if (StringUtils.isBlank(labelKey) || AmlEngineConstant.MICRO.equals(labelKey)) {
            return null;
        }
        String evaluationDataType = req.getEvaluationDataType();
        EvaluationDataTypeEnum targetDataType = EvaluationDataTypeEnum.getByType(evaluationDataType);

        if (targetDataType == null) {
            return null;
        }

        AmlModelDo model = amlModelDao.getById(modelId);
        if (model == null) {
            return null;
        }
        Byte status = model.getStatus();
        if (!AmlStatusEnum.TRAIN_SUCCESS.getStatus().equals(status)) {
            return null;
        }
        String resultPath = model.getResultPath();
        JSONObject jsonObject = JSON.parseObject(resultPath);
        String trainPredictPath = jsonObject.getString(AmlEngineConstant.trainPredictPathKey);


        if (StringUtils.isBlank(trainPredictPath)) {
            return null;
        }

        String workEnv = model.getWorkEnv();
        JSONObject workMap = JSON.parseObject(workEnv);
        String testPath = workMap.getString("test_file");
        if (StringUtils.isBlank(testPath)) {
            return null;
        }

        Integer pageNum = req.getPageNum();
        Integer pageSize = req.getPageSize();

        int offset = (pageNum - 1) * pageSize;
        if (offset < 0) {
            offset = 0;
        }

        try {
            List<EvaSentence> evaSentences = readPredict(labelName, req.getThreshold(),
                    testPath, trainPredictPath, targetDataType, offset, pageSize);
            List<EvaSentence> resultList = new ArrayList<>();
            boolean hasMore = false;
            if (!CollectionUtils.isEmpty(evaSentences)) {
                if (evaSentences.size() > pageSize) {
                    resultList = evaSentences.subList(0, pageSize);
                    hasMore = true;
                } else {
                    resultList = evaSentences;
                }
            }
            PageInfo<EvaSentence> pageInfo = new PageInfo<>(resultList);
            pageInfo.setHasNextPage(hasMore);
            pageInfo.setPageNum(req.getPageNum());
            pageInfo.setPageSize(req.getPageSize());
            return pageInfo;
        } catch (Exception e) {
            log.error("viewEvaluationTypePage:param:{},{},{},{}", req.getId(), req.getLabelName(),
                    req.getEvaluationDataType(), req.getThreshold(), e);
        }
        return null;

    }
    public String downloadEvaluationType(Integer modelId, AmlConfidenceEvaluationReq req)  {
        String outFile = innerDownloadEvaluationType(modelId, req);
        if (StringUtils.isEmpty(outFile)){
            return null;
        }
        File file = new File(outFile);
        if (!file.exists()){
            return null;
        }
        String ossPath = "tmp/aml/evaluation/label/"+file.getName();
        try {
            return ossService.uploadXls(Files.readAllBytes(Paths.get(outFile)), ossPath);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
    public String innerDownloadEvaluationType(Integer modelId, AmlConfidenceEvaluationReq req) {
        String labelKey = req.getLabelKey();
        String labelName = req.getLabelName();

        if (StringUtils.isBlank(labelKey) || AmlEngineConstant.MICRO.equals(labelKey)) {
            return null;
        }
        String evaluationDataType = req.getEvaluationDataType();
        EvaluationDataTypeEnum targetDataType = EvaluationDataTypeEnum.getByType(evaluationDataType);

        if (targetDataType == null) {
            return null;
        }

        AmlModelDo model = amlModelDao.getById(modelId);
        if (model == null) {
            return null;
        }
        Byte status = model.getStatus();
        if (!AmlStatusEnum.TRAIN_SUCCESS.getStatus().equals(status)) {
            return null;
        }
        String resultPath = model.getResultPath();
        JSONObject jsonObject = JSON.parseObject(resultPath);
        String trainPredictPath = jsonObject.getString(AmlEngineConstant.trainPredictPathKey);

        if (StringUtils.isBlank(trainPredictPath)) {
            return null;
        }

        String workEnv = model.getWorkEnv();
        JSONObject workMap = JSON.parseObject(workEnv);
        String testPath = workMap.getString("test_file");
        if (StringUtils.isBlank(testPath)) {
            return null;
        }
        String cleanLabelName = labelName.trim().replaceAll("\\s","");
        String outPath = "/tmp/aml/evaluation/"+req.getId()+"/"+cleanLabelName+"_"+targetDataType.getName()+".xlsx";
        try {

            return doDownloadPredict(labelName, req.getThreshold(),
                    testPath, trainPredictPath, targetDataType, outPath);
        } catch (Exception e) {
            log.error("downloadEvaluationType:param:{},{},{},{}", req.getId(), req.getLabelName(),
                    req.getEvaluationDataType(), req.getThreshold(), e);
        }
        return null;

    }
    private String doDownloadPredict(String labelName, BigDecimal threshold, String testPath, String trainPredictPath,
                                   EvaluationDataTypeEnum targetDataTypeEnum, String outPath) throws Exception {

        File testFile = new File(testPath);
        if (!testFile.exists()) {
            return null;
        }
        File predictFile = new File(trainPredictPath);
        if (!predictFile.exists()) {
            return null;
        }
        int id = 0;
        ExcelWriteSugar<EvaSentence> writeSugar = new ExcelWriteSugar<>();

        try (BufferedReader testReader = new BufferedReader(new InputStreamReader(new FileInputStream(testFile)));
             BufferedReader predictReader = new BufferedReader(new InputStreamReader(new FileInputStream(trainPredictPath)))
        ) {
            String testLine = null;
            String predictLine = null;

            List<EvaSentence> tmpList = new ArrayList<>();
            String sheetName = labelName + "_" + targetDataTypeEnum.getName();
            writeSugar.initWriter(outPath, EvaSentence.class, sheetName);
            while ((testLine = testReader.readLine()) != null && (predictLine = predictReader.readLine()) != null) {

                ModelDataItem modelDataItem = JSON.parseObject(testLine, ModelDataItem.class);
                if (modelDataItem == null) {
                    break;
                }
                EvaSentence evaSentence = getEvaSentence(labelName, threshold, targetDataTypeEnum, modelDataItem, predictLine);

                if (evaSentence == null){
                    continue;
                }
                tmpList.add(evaSentence);

                if (tmpList.size() >= 20) {
                    writeSugar.write(tmpList);
                    tmpList = new ArrayList<>();
                }
            }
            if (tmpList.size() > 0) {
                writeSugar.write(tmpList);
            }
        } finally {
            writeSugar.finish();
        }
        return outPath;
    }

    private List<EvaSentence> readPredict(String labelName, BigDecimal threshold, String testPath, String trainPredictPath,
                                          EvaluationDataTypeEnum targetDataTypeEnum, int offset, int size) throws Exception {

        File testFile = new File(testPath);
        if (!testFile.exists()) {
            return null;
        }
        File predictFile = new File(trainPredictPath);
        if (!predictFile.exists()) {
            return null;
        }
        List<EvaSentence> evaSentences = new ArrayList<>();
        int count = 0;
        try (BufferedReader testReader = new BufferedReader(new InputStreamReader(new FileInputStream(testFile)));
             BufferedReader predictReader = new BufferedReader(new InputStreamReader(new FileInputStream(trainPredictPath)))
        ) {
            String testLine = null;
            String predictLine = null;

            while ((testLine = testReader.readLine()) != null && (predictLine = predictReader.readLine()) != null) {

                ModelDataItem modelDataItem = JSON.parseObject(testLine, ModelDataItem.class);
                if (modelDataItem == null) {
                    break;
                }
                EvaSentence evaSentence = getEvaSentence(labelName, threshold, targetDataTypeEnum, modelDataItem, predictLine);

                if (evaSentence == null) {
                    continue;
                }
                count++;
                if (offset > (count - 1) || (count - 1) > (offset + size)) {
                    continue;
                }

                evaSentences.add(evaSentence);
                if (evaSentences.size() > size) {
                    break;
                }
            }
        }
        return evaSentences;
    }

    private EvaSentence getEvaSentence(String labelName, BigDecimal threshold,
                                       EvaluationDataTypeEnum targetDataTypeEnum,
                                       ModelDataItem modelDataItem, String predictLine) {
        boolean testReal = labelName.equals(modelDataItem.getLabel());
        String testSentence = modelDataItem.getSentence1();
        Pair<String, BigDecimal> predictItem = getPredictItem(predictLine, labelName);
        if (predictItem == null) {
            return null;
        }

        boolean predict = predictItem.getV2().compareTo(threshold) >= 0;

        EvaluationDataTypeEnum itemType = EvaluationDataTypeUtil.calEvaluationDataTypeEnum(testReal, predict);

        if (targetDataTypeEnum != itemType) {
            return null;
        }

        EvaSentence sentence = new EvaSentence();
        sentence.setSentence(testSentence);
        sentence.setScore(predictItem.getV2().setScale(5, RoundingMode.HALF_UP));
        return sentence;
    }


    private Pair<String, BigDecimal> getPredictItem(String predictLine, String label) {
        if (StringUtils.isBlank(predictLine)) {
            return null;
        }
        JSONArray predictJson = JSON.parseArray(predictLine);
        if (predictJson == null) {
            return null;
        }
        for (int j = 0; j < predictJson.size(); j++) {
            JSONObject jsonObject = predictJson.getJSONObject(j);
            BigDecimal score = jsonObject.getBigDecimal("score");

            String predictLabel = jsonObject.getString("label");
            if (score == null || StringUtils.isBlank(predictLabel)) {
                continue;
            }
            if (label.equals(predictLabel)) {
                return new Pair<>(predictLabel, score);
            }
        }
        return null;
    }


    public EvaluationConfidenceDto viewConfidenceEvaluation(Integer modelId, String labelKey) {
        AmlModelDo model = amlModelDao.getById(modelId);
        if (model == null) {
            return null;
        }
        Byte status = model.getStatus();
        if (!AmlStatusEnum.TRAIN_SUCCESS.getStatus().equals(status)) {
            return null;
        }
        String resultPath = model.getResultPath();

        JSONObject jsonObject = JSON.parseObject(resultPath);
        String evaluationPath = jsonObject.getString(AmlEngineConstant.evaluationPathKey);
        TrainResultDto trainResult = getTrainResult(evaluationPath);
        if (trainResult == null) {
            return null;
        }

        LinkedHashMap<String, List<TrainResultDto.ThresholdItem>> thresholdMap = trainResult.getLabelConfidenceThresholdMap();


        if (StringUtils.isBlank(labelKey)) {
            labelKey = AmlEngineConstant.MICRO;
        }
        EvaluationConfidenceDto confidenceDto = new EvaluationConfidenceDto();
        if (AmlEngineConstant.MICRO.equals(labelKey)) {
            List<TrainResultDto.ConfuseLine> confuseMatrix = trainResult.getConfuseMatrix();
            confidenceDto.setConfuseMatrix(confuseMatrix);

            confidenceDto.setLabelMap(trainResult.getLabelMap());
        }
        List<TrainResultDto.ThresholdItem> thresholdItems = thresholdMap.get(labelKey);
        confidenceDto.setThresholdItems(thresholdItems);

        List<TrainResultDto.LabelItem> avgPrecisionList = trainResult.getAvgPrecisionList();
        if (!CollectionUtils.isEmpty(avgPrecisionList)) {
            String usekey = labelKey;
            TrainResultDto.LabelItem precision = avgPrecisionList.stream().filter(item -> usekey.equals(item.getKey())).findFirst().get();
            if (precision != null) {
                BigDecimal avgPrecision = precision.getAvgPrecision();
                confidenceDto.setAvgPrecision(avgPrecision);
            }
        }

        if (thresholdItems != null && thresholdItems.size() > 50) {
            TrainResultDto.ThresholdItem thresholdItem = thresholdItems.get(49);
            confidenceDto.setDefaultItem(thresholdItem);
        }

        return confidenceDto;
    }

    private TrainResultDto getTrainResult(String evaluationPath) {

        String json = readFile(evaluationPath);
        if (StringUtils.isBlank(json)) {
            return null;
        }

        return JSON.parseObject(json, TrainResultDto.class);
    }

    private String readFile(String path) {
        if (StringUtils.isBlank(path)) {
            return null;
        }
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }

        String json = null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)))) {

            json = reader.readLine();
        } catch (Exception ex) {
            log.error("readFile:path:{}", path, ex);
            return null;
        }
        return json;
    }
}
