package com.tigerobo.x.pai.engine.auto.ml.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Preconditions;
import com.tigerobo.x.pai.api.aml.dto.ModelDataItem;
import com.tigerobo.x.pai.api.aml.engine.dto.ai.model.text.classify.TextClassifyStatisticDto;
import com.tigerobo.x.pai.api.aml.engine.dto.lake.SingleLabelClassificationLakeReq;
import com.tigerobo.x.pai.api.aml.enums.DatasetMetricEnum;
import com.tigerobo.x.pai.api.enums.AmlStatusEnum;
import com.tigerobo.x.pai.dal.aml.dao.AmlDatasetDao;
import com.tigerobo.x.pai.dal.aml.dao.AmlInfoDao;
import com.tigerobo.x.pai.dal.aml.dao.AmlModelDao;
import com.tigerobo.x.pai.dal.aml.entity.AmlDatasetDo;
import com.tigerobo.x.pai.dal.aml.entity.AmlInfoDo;
import com.tigerobo.x.pai.dal.aml.entity.AmlModelDo;
import com.tigerobo.x.pai.dal.auth.dao.UserDao;
import com.tigerobo.x.pai.dal.auth.entity.UserDo;
import com.tigerobo.x.pai.engine.auto.ml.util.AmlDataParseUtil;
import com.tigerobo.x.pai.engine.lake.LakeService;
import com.tigerobo.x.pai.biz.utils.NasFileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AmlStartTrainService {

    @Autowired
    private AmlModelDao amlModelDao;
    @Autowired
    private AmlDatasetDao amlDatasetDao;
    @Autowired
    private AmlInfoDao amlInfoDao;

    @Value("${pai.env.aml.prefix}")
    private String prefix;

    @Value("${engine.aml.nas.work.root}")
    private String OUT_ROOT_PATH;// = "/mnt/xpai/engine/test/aml/";

    @Value("${pai.lake.train.url}")
    String trainUrl ;//= "http://gbox8.aigauss.com:9696/train/config_path";
    @Autowired
    private LakeService lakeService;

    @Autowired
    private UserDao userDao;
    public int countOnProcess(){
      return amlModelDao.countOnTrainProcessList();
    }
    public List<AmlModelDo> getWaitDealList(){
        return amlModelDao.getWaitTrainList();
    }

    public void process(AmlModelDo modelDo)throws Exception{

        Integer dataId = modelDo.getDataId();
        AmlDatasetDo dataset = amlDatasetDao.getById(dataId);
        AmlInfoDo amlInfo = amlInfoDao.loadById(modelDo.getAmlId());
        Validate.isTrue(amlInfo!=null,"自主训练信息为空");


        Preconditions.checkArgument(dataset!=null,"数据集不存在");

        String statistic = dataset.getStatistic();
        TextClassifyStatisticDto tcs = AmlDataParseUtil.getTCS(statistic);
        Preconditions.checkArgument(tcs!=null,"统计信息为空");


        List<TextClassifyStatisticDto.Item> metricList = tcs.getMetricList();
        Preconditions.checkArgument(!CollectionUtils.isEmpty(metricList),"统计信息-指标数据为空");

        String createBy = modelDo.getCreateBy();
        UserDo user = null;
        if (createBy!=null&&createBy.matches("\\d+")){
            int userId = Integer.parseInt(createBy);
            user = userDao.getById(userId);
        }


        Map<String, TextClassifyStatisticDto.Item> metricMap = metricList.stream().collect(Collectors.toMap(m -> m.getKey(), m -> m));
        TextClassifyStatisticDto.Item testItem = metricMap.get(DatasetMetricEnum.TEST.getKey());
        TextClassifyStatisticDto.Item trainItem = metricMap.get(DatasetMetricEnum.TRAIN.getKey());
        TextClassifyStatisticDto.Item validationItem = metricMap.get(DatasetMetricEnum.VALIDATION.getKey());

        Preconditions.checkArgument(testItem!=null&& !StringUtils.isEmpty(testItem.getPath()),"测试文件为空");
        Preconditions.checkArgument(trainItem!=null&& !StringUtils.isEmpty(trainItem.getPath()),"测试文件为空");
        Preconditions.checkArgument(validationItem!=null&& !StringUtils.isEmpty(validationItem.getPath()),"测试文件为空");

        String testDemoLine = getTestDemoLine(testItem.getPath());
        Preconditions.checkArgument(!StringUtils.isEmpty(testDemoLine),"测试文件没有数据");

        String workPath = getWorkPath(modelDo.getCreateBy(), modelDo.getId());
        String outPath = workPath +"output/";

        NasFileUtil.delDir(outPath);

        NasFileUtil.chmodDir(outPath);

//        CmdOperator.getInstance().chmod(workPath);

        String modelName = prefix + modelDo.getId();
        SingleLabelClassificationLakeReq req = new SingleLabelClassificationLakeReq();
        req.setTest_file(testItem.getPath());
        req.setValidation_file(validationItem.getPath());
        req.setTrain_file(trainItem.getPath());
        req.setOutput_dir(outPath);
        req.setModel_name(modelName);
        if (user!=null&& org.apache.commons.lang3.StringUtils.isNotBlank(user.getAmlArea())){
            log.info("startTrain:modelId:{},area:{}",modelDo.getId(),user.getAmlArea());
            req.setModel_area(user.getAmlArea());
        }
//
//        if (!StringUtils.isEmpty(dataset.getPreTrainModelName())){
//            log.warn("model:{},preTrainModelName:{}",modelDo.getId(),dataset.getPreTrainModelName());
//            req.setModel_name_or_path(dataset.getPreTrainModelName());
//        }

        String modelPath = amlInfo.getModelPath();
        if (!StringUtils.isEmpty(modelPath)){
            req.setModel_name_or_path(amlInfo.getModelPath());
        }

        String configPath = writeReqPath(modelDo, req);

//        String reqResult = lakeService.reqGet(trainUrl,configPath);
        log.warn("lake:trainUrl,config-path:{}",configPath);
        String reqResult = lakeService.startTrain(trainUrl,configPath);
//        String reqResult = "{\"status\":0}";
        log.info("model:{},lake-req,result:{}",modelDo.getId(),reqResult);
        if (StringUtils.isEmpty(reqResult)){
            updateTrainFail(modelDo,"模型服务引擎异常");
        }else {
            JSONObject jsonObject = JSON.parseObject(reqResult);
            Integer status = jsonObject.getInteger("status");
            if (status!=null&&status.equals(0)){
                updateOnTrain(modelDo,req,testDemoLine);
            }else {
                String msg = jsonObject.getString("msg");
                if (msg!=null&&msg.length()>200){
                    msg = msg.substring(0,200);
                }
                updateTrainFail(modelDo,msg);
            }

        }

    }


    private String getTestDemoLine(String testPath ) throws IOException {
        if (StringUtils.isEmpty(testPath)){
            return null;
        }
        File file = new File(testPath);
        Preconditions.checkArgument(file.exists(),"没有测试文件");
        String testLine = null;
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(testPath)))){
            String line = null;
            while ((line = reader.readLine())!=null){
                ModelDataItem dataItem = JSONObject.parseObject(line,ModelDataItem.class);
                if (dataItem !=null){
                    String sentence1 = dataItem.getSentence1();
                    if (org.apache.commons.lang3.StringUtils.isNotBlank(sentence1)){
                        testLine = sentence1;
                        break;
                    }
                }
            }
        }
        return testLine;
    }

    public void updateOnTrain(AmlModelDo modelDo,SingleLabelClassificationLakeReq req,String demo){

        AmlInfoDo amlInfoDo = new AmlInfoDo();
        amlInfoDo.setId(modelDo.getAmlId());
        amlInfoDo.setStatus(AmlStatusEnum.ON_PROCESS_TRAIN.getStatus());

        amlInfoDao.updateByPrimaryKey(amlInfoDo);

        AmlModelDo update = new AmlModelDo();

        update.setId(modelDo.getId());

        update.setId(modelDo.getId());
        update.setStatus(AmlStatusEnum.ON_PROCESS_TRAIN.getStatus());
        update.setTotalEpoch(req.getNum_train_epochs());
        update.setEpoch(0);
        update.setWorkEnv(JSON.toJSONString(req));
        update.setModelReqTime(new Date());
        update.setDemo(demo);
        amlModelDao.update(update);
    }

    public void updateTrainFail(AmlModelDo modelDo,String err){

        AmlInfoDo amlInfoDo = new AmlInfoDo();
        amlInfoDo.setId(modelDo.getAmlId());
        amlInfoDo.setStatus(AmlStatusEnum.TRAIN_FAIL.getStatus());

        amlInfoDao.updateByPrimaryKey(amlInfoDo);

        AmlModelDo update = new AmlModelDo();

        update.setId(modelDo.getId());
        update.setStatus(AmlStatusEnum.TRAIN_FAIL.getStatus());
        update.setErrMsg(err);
        amlModelDao.update(update);
    }

    private String getWorkPath(String userId,Integer modelId){
        return OUT_ROOT_PATH+"u"+userId + "/train/"+modelId+"/";
    }

    private String writeReqPath(AmlModelDo modelDo, SingleLabelClassificationLakeReq req)throws Exception{

        String reqConfigPath = getWorkPath(modelDo.getCreateBy(),modelDo.getId())+"config.json";

        File file = new File(reqConfigPath);
        if (!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }

        try(BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(reqConfigPath)))){
            bufferedWriter.write(JSON.toJSONString(req));
            bufferedWriter.flush();
        }
        return reqConfigPath;
    }


}
