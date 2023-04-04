package com.tigerobo.x.pai.biz.biz.customer;

import com.tigerobo.x.pai.biz.base.EnvService;
import com.tigerobo.x.pai.biz.notify.DingTalkNotify;
import com.tigerobo.x.pai.biz.utils.TimeUtil;
import com.tigerobo.x.pai.dal.biz.dao.ApiDao;
import com.tigerobo.x.pai.dal.biz.dao.call.ModelSourceCallDao;
import com.tigerobo.x.pai.dal.biz.entity.ApiDo;
import com.tigerobo.x.pai.dal.biz.entity.call.ModelSourceCallPo;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AmlApiCustomerService {

    @Autowired
    private ModelSourceCallDao modelSourceCallDao;

    @Autowired
    private EnvService envService;

    @Autowired
    private ApiDao apiDao;

    public void checkHupu(){

        if (!envService.isProd()){
            return;
        }

        String apiId = "e2dc52a544af544b6e6b515b01d3d5b8";
        final ApiDo apiDo = apiDao.getByModelUuid(apiId);
        if (apiDo == null){
            log.warn("model-apiId:{},not exist",apiId);
            return;
        }

        final Integer amlModelId = apiDo.getAmlModelId();
        final String amlRelIds = apiDo.getAmlRelIds();

        final List<String> existIds = new ArrayList<>(Arrays.asList(amlRelIds.split("[,，]")));

        final int dayValue = TimeUtil.getDayValue(new Date());
        int userId =15;Integer limit = 40000;
        final List<ModelSourceCallPo> callList = modelSourceCallDao.getUserDayListAmlOnlineCall(userId, dayValue, limit);

        if (CollectionUtils.isEmpty(callList)){
            return;
        }


        for (int i = 0; i < 2 && i < callList.size(); i++) {
            final ModelSourceCallPo po = callList.get(i);
            final String modelId = po.getModelId();
            if (modelId == null){
                continue;
            }
            if (!modelId.matches("\\d+")){
                continue;
            }
            final int modelIdInt = Integer.parseInt(modelId);
            if (amlModelId.equals(modelIdInt)){
                final Date updateTime = po.getUpdateTime();
                if (updateTime!=null){
                    if (updateTime.getTime()-System.currentTimeMillis()>3600_000){
                        continue;
                    }
                }
                return;
            }
            if (existIds.contains(modelId)){
                continue;
            }
            existIds.add(modelId);
            ApiDo updatePo = new ApiDo();

            updatePo.setId(apiDo.getId());
            updatePo.setAmlModelId(modelIdInt);

            updatePo.setAmlRelIds(String.join(",",existIds));
            apiDao.update(updatePo);
            String msg = String.format("虎扑模型id替换,:ori=%s,new=%s",amlModelId,modelId);
            DingTalkNotify.notice(msg);
            break;
        }


    }

}
