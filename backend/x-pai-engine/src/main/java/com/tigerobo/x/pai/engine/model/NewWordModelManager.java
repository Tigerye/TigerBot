package com.tigerobo.x.pai.engine.model;

import com.alibaba.fastjson.JSON;
import com.tigerobo.x.pai.dal.biz.dao.ModelNewWordDao;
import com.tigerobo.x.pai.dal.biz.entity.ModelNewWordPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.util.*;

/**
 * 效果不好
 */
@Deprecated
@Slf4j
@Component
public class NewWordModelManager {

    String url = "http://gbox3.aigauss.com:9581/new_words_discovery";
    private List<String> types = Arrays.asList("政治","财经");
    @Autowired
    private ModelNewWordDao modelNewWordDao;


    private RestTemplate restTemplate = new RestTemplate();
    public void addDay(int day) throws ParseException {

        long start = System.currentTimeMillis();
        log.info("start:day:{}",day);
        Date startDay = DateUtils.parseDate(String.valueOf(day), "yyyyMMdd");
        Date nextDay = DateUtils.addDays(startDay, 1);
        String startStr = DateFormatUtils.format(startDay, "yyyy-MM-dd");
        String endStr = DateFormatUtils.format(nextDay, "yyyy-MM-dd");
        for (String type : types) {
            Map<String,Object> map = new HashMap<>();
            map.put("news_type",type);
            map.put("start_date",startStr);
            map.put("end_date",endStr);
            map.put("word_nums",200);
            String s = post(url,JSON.toJSONString(map));
            if (!StringUtils.isEmpty(s)){
                ModelNewWordPo modelNewWordPo = modelNewWordDao.get(day, type);

                if (modelNewWordPo!=null){
                    modelNewWordPo.setWords(s);
                    modelNewWordDao.update(modelNewWordPo);
                }else {
                    modelNewWordPo = new ModelNewWordPo();
                    modelNewWordPo.setDay(day);
                    modelNewWordPo.setType(type);
                    modelNewWordPo.setWords(s);
                    modelNewWordDao.add(modelNewWordPo);
                }
            }
        }

        log.info("day:{},delta:{}",day,System.currentTimeMillis()-start);
    }

    private String post(String url,String data){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("application/json;UTF-8"));
        HttpEntity<String> strEntity = new HttpEntity<String>(data,headers);
        return restTemplate.postForObject(url,strEntity,String.class);
    }
}
