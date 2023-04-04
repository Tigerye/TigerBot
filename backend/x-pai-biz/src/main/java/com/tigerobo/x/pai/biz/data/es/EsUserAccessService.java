package com.tigerobo.x.pai.biz.data.es;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tigerobo.x.pai.api.constants.EsConstant;
import com.tigerobo.x.pai.api.enums.ModelCallTypeEnum;
import com.tigerobo.x.pai.api.es.EsModelCall;
import com.tigerobo.x.pai.api.es.EsUserAccess;
import com.tigerobo.x.pai.biz.base.EnvService;
import com.tigerobo.x.pai.biz.cache.biz.MachineUtil;
import com.tigerobo.x.pai.biz.utils.IdGenerator;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.biz.utils.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class EsUserAccessService {

    @Autowired
    private EsPaiClient esPaiClient;


    @Value("${pai.es.index.user.access}")
    private String userAccessIndex;

    ObjectMapper objectMapper = new ObjectMapper();
    public void add(Integer userId, String ip){
        try {
            innerAdd(userId,ip);
        }catch (Exception ex){
            log.error("userId:{},ip:{}",userId,ip,ex);
        }
    }
    private void innerAdd(Integer userId, String ip){//catch

        if ((userId == null||userId==0)&&StringUtils.isEmpty(ip)){
            log.warn("userId{},ip:{},empty",userId,ip);
            return;
        }
        Date date = new Date();
        EsUserAccess call = new EsUserAccess();
        if (userId == null){
            userId = 0;
        }
        call.setUserId(userId);
        if (ip == null){
            ip = "";
        }
        int dayValue = TimeUtil.getDayValue(date);
        String id = IdGenerator.getUserUniqueId(userId, ip, dayValue);
        call.setId(id);
        call.setIp(ip);
        call.setDay(dayValue);
        call.setCreateTime(date);
        doAdd(call);

    }

    private void doAdd(EsUserAccess call){
        int month = TimeUtil.getMonthValue(call.getDay());
        String index = getIndex(month);

        IndexRequest indexRequest = new IndexRequest(index,EsConstant.INDEX_TYPE,String.valueOf(call.getId()));
        try {
            indexRequest.source(objectMapper.writeValueAsString(call), XContentType.JSON);
        } catch (JsonProcessingException e) {
            log.error("add-user-access:{}", JSON.toJSONString(call),e);
            throw new RuntimeException("生成json异常");
        }

        esPaiClient.getBulkProcesor().add(indexRequest);
    }


    public Integer countDay(int day){
        int month = TimeUtil.getMonthValue(day);
        try {
            String index = getIndex(month);
            CountRequest request = new CountRequest(index);

            request.query(QueryBuilders.termQuery("day", day));

            CountResponse count = esPaiClient.getClient().count(request, RequestOptions.DEFAULT);

            return (int)count.getCount();
        }catch (Exception ex){
            log.error("count-user,day:{}",day,ex);
        }
        return null;

    }
    public String getIndex(int month){

        return userAccessIndex+"_"+month;
    }
}
