package com.tigerobo.x.pai.biz.biz.service;

import com.alibaba.fastjson.JSON;
import com.tigerobo.x.pai.api.enums.ModelCallSourceEnum;
import com.tigerobo.x.pai.api.enums.ModelCallTypeEnum;
import com.tigerobo.x.pai.biz.data.es.EsService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.biz.utils.ThreadUtil;
import com.tigerobo.x.pai.dal.biz.dao.ModelCallLogDao;
import com.tigerobo.x.pai.dal.biz.entity.ModelCallLogPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class ModelCallLogService {

    @Autowired
    private ModelCallLogDao modelCallLogDao;

    @Resource
    Environment environment;

//    @Value("${pai.call.log.api.open:true}")
//    boolean apiLogSwitchOpen;

    public void add(Integer userId,String modelId,String content,Integer source,String result){//catch
        if (StringUtils.isEmpty(modelId)){
            return;
        }
        if (!ModelCallSourceEnum.PAGE_EXECUTE.getType().equals(source)){
            return;
        }

        String dayFormat = DateFormatUtils.format(new Date(), "yyyyMMdd");
        int day = Integer.parseInt(dayFormat);

        ModelCallLogPo po = new ModelCallLogPo();
        po.setModelId(modelId);
        po.setUserId(userId);
        po.setDay(day);
        po.setContent(content);
        po.setSource(source);
        po.setAppId(getAppId());
        po.setResult(result);
        String ip = ThreadLocalHolder.getIp();
        po.setIp(ip);

        try {
            ThreadUtil.executorService.submit(() -> doAdd(po));
        }catch (Exception ex){
            log.error("write call2db error",ex);
        }
    }

    private void doAdd(ModelCallLogPo po) {
        try {
            modelCallLogDao.add(po);
        }catch (Exception ex){
            log.error("add-Model-log:{}", JSON.toJSON(po),ex);
        }
    }

    private String getAppId(){
        return environment.getProperty("app.id");
    }
}
