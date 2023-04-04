package com.tigerobo.x.pai.engine.auto.ml.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tigerobo.x.pai.api.aml.constant.AmlEngineConstant;
import com.tigerobo.x.pai.api.aml.constant.AmlLakeConstant;
import com.tigerobo.x.pai.api.aml.engine.dto.content.OpContent;
import com.tigerobo.x.pai.api.aml.engine.dto.train.AmlTrainInputContent;
import com.tigerobo.x.pai.api.aml.engine.dto.train.TrainResultDto;
import com.tigerobo.x.pai.api.enums.AmlStatusEnum;
import com.tigerobo.x.pai.dal.aml.dao.AmlInfoDao;
import com.tigerobo.x.pai.dal.aml.dao.AmlModelDao;
import com.tigerobo.x.pai.dal.aml.entity.AmlInfoDo;
import com.tigerobo.x.pai.dal.aml.entity.AmlModelDo;
import com.tigerobo.x.pai.engine.auto.ml.operator.model.TrainResultOperator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 1,请求训练
 * 2，遍历训练状态
 * 3，解析训练结果，回写调用接口
 */
@Slf4j
@Service
public class AmlTrainOnProcessService {

    @Autowired
    private AmlModelDao amlModelDao;

    @Autowired
    private AmlInfoDao amlInfoDao;
    @Value("${engine.aml.nas.work.root}")
    private String OUT_ROOT_PATH;// = "/mnt/xpai/engine/test/aml/";


    public void deal(AmlModelDo amlModelDo)throws Exception{

        String workEnv = amlModelDo.getWorkEnv();
        JSONObject jsonObject = JSON.parseObject(workEnv);
        String outputDir = jsonObject.getString("output_dir");

        String finishValue = getFinishValue(outputDir);
        log.info("train-result-value:{},=0?",finishValue);
        if (StringUtils.isNotBlank(finishValue)){
            if (finishValue.equals("0")){
                processResult(amlModelDo, outputDir);
            }else {
                updateFail(amlModelDo);
            }
        }else {
            Integer epoch = getEpoch(outputDir);

            updateEpoch(amlModelDo,epoch);
        }
    }

    private void processResult(AmlModelDo amlModelDo, String outputDir) throws Exception {

        String workPathDir = getWorkPath(amlModelDo.getCreateBy(), amlModelDo.getId());

        TrainResultOperator operator = new TrainResultOperator();

        AmlTrainInputContent inputContent = new AmlTrainInputContent();
        inputContent.setOutputDir(outputDir);

        inputContent.setResultFilePath(outputDir+AmlLakeConstant.TRAIN_RESULT_CONTENT_FILE);

        OpContent content = new OpContent();
        content.setOutputRoot(workPathDir);
        TrainResultDto resultDto = operator.run(inputContent, content);



        updateFinish(amlModelDo,resultDto);
    }


    private void updateFinish(AmlModelDo amlModelDo,TrainResultDto resultDto){


        Map<String,Object> map = new HashMap<>();

        String evaluationPath = resultDto.getEvaluationPath();
        String resultPath = resultDto.getResultPath();
        String predictPath = resultDto.getPredictPath();
        map.put(AmlEngineConstant.evaluationPathKey,evaluationPath);
        map.put(AmlEngineConstant.trainResultPathKey,resultPath);
        map.put(AmlEngineConstant.trainPredictPathKey,predictPath);
        String resultPathData = JSON.toJSONString(map);
        BigDecimal avgPrecision = null;
        List<TrainResultDto.LabelItem> avgPrecisionList = resultDto.getAvgPrecisionList();
        if (avgPrecisionList!=null&&avgPrecisionList.size()>0){
            avgPrecision = avgPrecisionList.get(0).getAvgPrecision();
        }

        List<TrainResultDto.ThresholdItem> thresholdItems = resultDto.getLabelConfidenceThresholdMap().get(AmlEngineConstant.MICRO);

        TrainResultDto.ThresholdItem midThresholdItem = thresholdItems.stream()
                .filter(thresholdItem -> thresholdItem.getThreshold().compareTo(new BigDecimal("0.5")) == 0)
                .findFirst()
                .orElseGet(null);


        AmlModelDo updateModel = new AmlModelDo();
        updateModel.setId(amlModelDo.getId());
        updateModel.setStatus(AmlStatusEnum.TRAIN_SUCCESS.getStatus());
        updateModel.setResultPath(resultPathData);
        updateModel.setModelFinishTime(new Date());
        updateModel.setServiceStatus(0);
        if (avgPrecision!=null){
            updateModel.setAvgPrecision(avgPrecision);
        }
        if (midThresholdItem!=null){
            updateModel.setPrecision(midThresholdItem.getPrecision());
            updateModel.setRecall(midThresholdItem.getRecall());
        }


        amlModelDao.update(updateModel);

        AmlInfoDo amlInfoDo = new AmlInfoDo();
        amlInfoDo.setId(amlModelDo.getAmlId());
        amlInfoDo.setStatus(AmlStatusEnum.TRAIN_SUCCESS.getStatus());

        amlInfoDao.updateByPrimaryKey(amlInfoDo);
    }

    private void updateFail(AmlModelDo amlModelDo){


        AmlModelDo updateModel = new AmlModelDo();
        updateModel.setId(amlModelDo.getId());
        updateModel.setStatus(AmlStatusEnum.TRAIN_FAIL.getStatus());
        updateModel.setModelFinishTime(new Date());
        updateModel.setErrMsg("模型训练失败");
        amlModelDao.update(updateModel);

        AmlInfoDo amlInfoDo = new AmlInfoDo();
        amlInfoDo.setId(amlModelDo.getAmlId());
        amlInfoDo.setStatus(AmlStatusEnum.TRAIN_FAIL.getStatus());


        amlInfoDao.updateByPrimaryKey(amlInfoDo);
    }


    private void updateEpoch(AmlModelDo modelDo,Integer epoch){
        if (epoch==null){
            return;
        }
        epoch +=1;
        if (modelDo.getEpoch()!=null&&epoch.equals(modelDo.getEpoch())){
            return;
        }
        log.info("model-update-epoch:id{},name:{},epoch:{}",modelDo.getId(),modelDo.getName(),epoch);
        AmlModelDo updateModel = new AmlModelDo();
        updateModel.setId(modelDo.getId());
        updateModel.setEpoch(epoch);
        amlModelDao.update(updateModel);
    }
    private String getFinishValue(String outputDir){

        String resultPath = outputDir + AmlLakeConstant.TRAIN_RESULT_END_FILE;

        File file = new File(resultPath);
        String line = null;
        if(file.exists()&&file.length()>0){
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))){
                line = reader.readLine();
            }catch (IOException ex){
                log.error("opProcess:{}",resultPath,ex);
            }
        }
        return line;
    }

    private Integer getEpoch(String outputDir){
        String trainFile = outputDir + AmlLakeConstant.TRAIN_EPOCH_LOG_FILE;

        File file = new File(trainFile);

        if (!file.exists()){
            return null;
        }
        Integer value = null;
        try(ReversedLinesFileReader reader = new ReversedLinesFileReader(file, Charset.defaultCharset())){

            String line = null;

            while ((line = reader.readLine())!=null){
                if (StringUtils.isNotBlank(line)){
                    String[] split = line.split(",");
                    String first = split[0];
                    if (first.matches("\\d{1,2}")){
                        value = Integer.parseInt(first);
                    }
                }
                if (value!=null){
                    break;
                }
            }
        }catch (Exception ex){
            log.error("getEpoch:{}",trainFile,ex);
        }
        return value;
    }

    private void parseResult(){
        String resultPath = "";
    }


    private String getWorkPath(String userId,Integer modelId){
        return OUT_ROOT_PATH+"u"+userId + "/train/"+modelId+"/";
    }

}
