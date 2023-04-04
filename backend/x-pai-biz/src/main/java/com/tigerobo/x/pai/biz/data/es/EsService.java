package com.tigerobo.x.pai.biz.data.es;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tigerobo.x.pai.api.constants.EsConstant;
import com.tigerobo.x.pai.api.enums.ModelCallTypeEnum;
import com.tigerobo.x.pai.api.es.EsModelCall;
import com.tigerobo.x.pai.biz.base.EnvService;
import com.tigerobo.x.pai.biz.cache.biz.MachineUtil;
import com.tigerobo.x.pai.biz.utils.IdGenerator;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.biz.utils.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class EsService {

    @Autowired
    private EsPaiClient esPaiClient;


    @Autowired
    private MachineUtil machineUtil;

    @Autowired
    private EnvService envService;
    ObjectMapper objectMapper = new ObjectMapper();
//
//    public void add(Integer userId, String modelId, String content, Integer source, ModelCallTypeEnum type, String result,Long dealTime){//catch
//
//        if (StringUtils.isEmpty(modelId)){
//            return;
//        }
//        EsModelCall call = new EsModelCall();
//        call.setModelId(modelId);
//        if (userId == null){
//            userId = 0;
//        }
//        call.setUserId(userId);
//        call.setContent(content);
//        call.setSource(source);
//        call.setType(type.getType());
//        if (result == null){
//            result = "";
//        }
//        call.setResult(result);
//        String ip = ThreadLocalHolder.getIp();
//        if (ip == null){
//            ip = "";
//        }
//        call.setIp(ip);
//        call.setDealTime(dealTime);
//        add(call);
//    }

    public void add(EsModelCall call){

        try {
            if (call.getId() == null||call.getId()<=0){
                long id = IdGenerator.getId(machineUtil.getMachineId());
                call.setId(id);
            }


            Date curTime = new Date();
            int day = TimeUtil.getDayValue(curTime);
            call.setDay(day);
            call.setCreateTime(curTime);
            call.setAppId(envService.getAppId());
            Integer callNum = call.getCallNum();
            if (callNum == null) {
                call.setCallNum(1);
            }
            doAdd(call);
        }catch (Exception ex){
            log.error("add-call-es:{}",JSON.toJSONString(call),ex);
        }
    }

    private void doAdd(EsModelCall call){
        int month = TimeUtil.getMonthValue(call.getDay());
        String index = getIndex(month);

        IndexRequest indexRequest = new IndexRequest(index,EsConstant.INDEX_TYPE,String.valueOf(call.getId()));
        try {
            indexRequest.source(objectMapper.writeValueAsString(call), XContentType.JSON);
        } catch (JsonProcessingException e) {
            log.error("add-model:{}", JSON.toJSONString(call),e);
            throw new RuntimeException("生成json异常");
        }

        esPaiClient.getBulkProcesor().add(indexRequest);
    }


    public String getIndex(int month){

        if (envService.isProd()){
            return EsConstant.INDEX_MODEL_CALL_INDEX+month;
        }else {
            return EsConstant.TEST_INDEX_MODEL_CALL_INDEX+month;
        }
    }
}
