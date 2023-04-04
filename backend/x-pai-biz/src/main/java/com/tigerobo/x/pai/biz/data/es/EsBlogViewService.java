package com.tigerobo.x.pai.biz.data.es;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tigerobo.x.pai.api.constants.EsConstant;
import com.tigerobo.x.pai.api.es.EsBlogView;
import com.tigerobo.x.pai.api.es.EsUserAccess;
import com.tigerobo.x.pai.biz.utils.IdGenerator;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.biz.utils.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class EsBlogViewService {

    @Autowired
    private EsPaiClient esPaiClient;


    @Value("${pai.es.index.blog.view}")
    private String blogViewIndex;

    ObjectMapper objectMapper = new ObjectMapper();
    public void add(Integer blogId){

        try {
            Integer userId = ThreadLocalHolder.getUserId();
            String ip = ThreadLocalHolder.getIp();
            innerAdd(blogId,userId,ip);
        }catch (Exception ex){
            log.error("add-es,blogId:{}",blogId,ex);
        }
    }
    private void innerAdd(Integer blogId,Integer userId, String ip){//catch

        if (blogId == null||blogId == 0){
            return;
        }
        if ((userId == null||userId==0)&&StringUtils.isEmpty(ip)){
            log.warn("userId{},ip:{},empty",userId,ip);
            return;
        }
        Date date = new Date();
        EsBlogView call = new EsBlogView();
        if (userId == null){
            userId = 0;
        }
        call.setUserId(userId);
        if (ip == null){
            ip = "";
        }
        int dayValue = TimeUtil.getDayValue(date);
        String id = IdGenerator.getUserBlogViewId(blogId,userId, ip, dayValue);
        call.setId(id);
        call.setIp(ip);
        call.setBlogId(blogId);
        call.setDay(dayValue);
        call.setCreateTime(date);
        doAdd(call);

    }

    private void doAdd(EsBlogView call){
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


    public String getIndex(int month){

        return blogViewIndex +"_"+month;
    }
}
