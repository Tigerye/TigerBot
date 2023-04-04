package com.tigerobo.x.pai.engine.auto.ml.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tigerobo.x.pai.api.aml.enums.ModelServiceStatusEnum;
import com.tigerobo.x.pai.dal.aml.dao.AmlModelDao;
import com.tigerobo.x.pai.dal.aml.entity.AmlModelDo;
import com.tigerobo.x.pai.engine.lake.LakeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AmlTrainOnlineService {

    @Value("${pai.env.aml.prefix}")
    private String prefix;

    @Value("${pai.env.lake.infer.base.url:http://gbox8.aigauss.com:9595}")
    private String inferUrlBase;
    @Value("${pai.env.lake.infer.hupu.url:http://gbox8.aigauss.com:9393}")
    private String hupuInferUrlBase;

    @Autowired
    LakeService lakeService;
    @Autowired
    private AmlModelDao amlModelDao;

    private static final String NOT_EXIST = "模型不存在!";

    private static final String LOAD= "模型已经部署！";

    private static final String UNLOAD="模型没有上线！";

    public void dealOnline(AmlModelDo modelDo) {

        Integer id = modelDo.getId();

        String modelName = prefix+id;

        String modelUrl = getRemoteUrl(modelDo);
        String reqUrl = StringUtils.isBlank(modelUrl)?inferUrlBase:modelUrl;
        String result = lakeService.online(reqUrl, modelName);
        Integer serviceStatus = null;
        if (StringUtils.isNotBlank(result)){
            JSONObject jsonObject = JSON.parseObject(result);

            Integer status = jsonObject.getInteger("status");

            if (status==0){
                serviceStatus = ModelServiceStatusEnum.ONLINE.getStatus();
            }else {
                String msg = jsonObject.getString("msg");
                if (NOT_EXIST.equalsIgnoreCase(msg)){
                    serviceStatus = ModelServiceStatusEnum.NOT_EXIST.getStatus();
                }else if (LOAD.equals(msg)){
                    serviceStatus = ModelServiceStatusEnum.ONLINE.getStatus();
                }else {
                    log.error("model-online-err:modelId:{},result{}",id,result);
                }
            }
        }
        if (serviceStatus !=null){
            AmlModelDo update = new AmlModelDo();
            update.setServiceStatus(serviceStatus);
            update.setId(id);
            amlModelDao.update(update);
        }
    }

    public void dealOffline(AmlModelDo modelDo) {

        Integer id = modelDo.getId();

        String modelName = prefix+id;
        String modelUrl = getRemoteUrl(modelDo);
        String reqUrl = StringUtils.isBlank(modelUrl)?inferUrlBase:modelUrl;

        String result = lakeService.offline(reqUrl, modelName);
        Integer serviceStatus = null;
        if (StringUtils.isNotBlank(result)){
            JSONObject jsonObject = JSON.parseObject(result);

            Integer status = jsonObject.getInteger("status");

            if (status==0){
                serviceStatus = ModelServiceStatusEnum.OFFLINE.getStatus();
            }else {
                String msg = jsonObject.getString("msg");
                if (NOT_EXIST.equalsIgnoreCase(msg)){
                    serviceStatus = ModelServiceStatusEnum.NOT_EXIST.getStatus();
                }else if (UNLOAD.equals(msg)){
                    serviceStatus = ModelServiceStatusEnum.OFFLINE.getStatus();
                }else {
                    log.error("model-offline-err:modelId:{},result{}",id,result);
                }
            }
        }
        if (serviceStatus !=null){
            AmlModelDo update = new AmlModelDo();
            update.setServiceStatus(serviceStatus);
            update.setId(id);
            amlModelDao.update(update);
        }
    }

    private String getRemoteUrl(AmlModelDo modelDo){
        final String createBy = modelDo.getCreateBy();
        if ("15".equals(createBy)){
            return hupuInferUrlBase;
        }
        return modelDo.getModelUrl();
    }
}
