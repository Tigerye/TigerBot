package com.tigerobo.x.pai.biz.serving.execute;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tigerobo.x.pai.api.biz.entity.API;
import com.tigerobo.x.pai.api.biz.entity.Model;
import com.tigerobo.x.pai.api.dto.ApiDto;
import com.tigerobo.x.pai.api.exception.APIException;
import com.tigerobo.x.pai.api.exception.ResultCode;
import com.tigerobo.x.pai.dal.biz.dao.ModelNewWordDao;
import com.tigerobo.x.pai.dal.biz.entity.ModelNewWordPo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description: 开域问答 执行器
 * @modified By:
 * @version: $
 */
@Slf4j
@Data
@AllArgsConstructor
public class NewWordApiExecutor extends UriApiExecutor {
    private ModelNewWordDao modelNewWordDao;

    public NewWordApiExecutor(ApiDto api, ModelNewWordDao modelNewWordDao) {
        super(api);
        this.modelNewWordDao = modelNewWordDao;
    }

    private volatile boolean ready = true;

    @Override
    public Object execute(Map<String, Object> params) {

        String startDate = (String)params.get("startDate");
        String endDate = (String)params.get("endDate");
        String newsType = (String)params.get("newsType");
        JSONObject jsonObject = new JSONObject();


        List<String> list;
        try {
            list = getList(startDate, endDate, newsType);
        } catch (Exception ex) {
            log.error("get new words err",ex);
            list = new ArrayList<>();
        }
        jsonObject.put("result",list);

        return jsonObject;
    }

    private List<String> getList(String startDate,String endDate,String newsType)throws Exception{

        if (StringUtils.isEmpty(newsType)){
            newsType = "政治";
        }
        Date today = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
        Date startDayValue =StringUtils.isEmpty(startDate)?today:DateUtils.parseDate(startDate, "yyyy-MM-dd");
        Date endDayValue = StringUtils.isEmpty(endDate)?today:DateUtils.parseDate(endDate, "yyyy-MM-dd");
        Date min = DateUtils.parseDate("20210101","yyyyMMdd");
        if (startDayValue.before(min)){
            startDayValue = min;
        }else if (startDayValue.after(today)){
            startDayValue = today;
        }
        if (endDayValue.before(min)){
            return new ArrayList<>();
        }
        if (endDayValue.after(today)){
            endDayValue = today;
        }
        List<Integer> days = new ArrayList<>();

        Date useDay = endDayValue;
        while (!useDay.before(startDayValue)){
            Integer day = Integer.parseInt(DateFormatUtils.format(useDay,"yyyyMMdd"));
            days.add(day);
            useDay = DateUtils.addDays(useDay,-1);
        }

        if (days.isEmpty()){
            return new ArrayList<>();
        }

        if (modelNewWordDao == null){

        }
        List<ModelNewWordPo> search = modelNewWordDao.search(days, newsType);

        if (CollectionUtils.isEmpty(search)){
            return new ArrayList<>();
        }

        List<String> words = new ArrayList<>();
        for (ModelNewWordPo modelNewWordPo : search) {

            String wordsInDb = modelNewWordPo.getWords();
            if (!StringUtils.isEmpty(wordsInDb)){
                try {
                    List<String> parseWords = JSON.parseArray(wordsInDb, String.class);
                    if (!CollectionUtils.isEmpty(parseWords)) {
                        words.addAll(parseWords);
                    }
                }catch (Exception ex){
                    log.error("parse-error:{}",modelNewWordPo.getId(),ex);
                }
            }
            if (words.size()>=200){
                break;
            }
        }
        return words;
    }

}
