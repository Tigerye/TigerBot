package com.tigerobo.x.pai.biz.biz.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.tigerobo.x.pai.api.biz.entity.API;
import com.tigerobo.x.pai.api.biz.entity.Model;
import com.tigerobo.x.pai.api.dto.ApiDto;
import com.tigerobo.x.pai.biz.converter.APIConvert;
import com.tigerobo.x.pai.dal.biz.dao.ApiDao;
import com.tigerobo.x.pai.dal.biz.entity.ApiDo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class WebApiService {


    @Autowired
    private ApiDao apiDao;

    public void createOrUpdate(Model model,String demoText){

        ApiDo apiDb = apiDao.getByModelUuid(model.getUuid());

        if (apiDb == null){

            ApiDo api = new ApiDo();
            api.setName(model.getName());
            api.setIntro(model.getIntro());
            api.setDesc(model.getDesc());
            api.setImage(model.getImage());
            api.setUri(model.getApiUri());
            api.setModelId(model.getId());
            api.setModelUuid(model.getUuid());
            api.setStyle(model.getStyle());
            api.setUuid(model.getUuid());

            api.setStatus(30);

            api.setCreateBy(model.getCreateBy());
            if (StringUtils.isNotBlank(demoText)){
                api.setDemo(demoText);
            }

            apiDao.insert(api);
        }else {

            ApiDo update = new ApiDo();
            update.setId(apiDb.getId());
            update.setCreateBy(model.getCreateBy());
            update.setUri(model.getApiUri());
            update.setDemo(demoText);

            update.setStatus(30);
            apiDao.update(update);
        }
    }


    private String getDemo(String text){
        Map<String,String> map = new HashMap<>();
        map.put("text",text);
        return JSON.toJSONString(map);
    }

    public Map<String,Object> getDemoMap(String text){
        Map<String,Object> map = new HashMap<>();
        map.put("text",text);
        return map;
    }

}
