package com.tigerobo.x.pai.biz.aml.service;

import com.tigerobo.x.pai.api.aml.dto.AmlBaseModelDto;
import com.tigerobo.x.pai.biz.aml.convert.AmlBaseModelConverter;
import com.tigerobo.x.pai.dal.aml.dao.AmlBaseModelDao;
import com.tigerobo.x.pai.dal.aml.entity.AmlBaseModelDo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AmlBaseModelServiceImpl {

    @Autowired
    private AmlBaseModelDao amlBaseModelDao;
    public AmlBaseModelDto getBaseModel(Integer baseModelId){

        AmlBaseModelDo fromCache = amlBaseModelDao.getFromCache(baseModelId);

        return AmlBaseModelConverter.do2dto(fromCache);
    }

    public List<AmlBaseModelDto> getBaseModelList(){
        List<AmlBaseModelDo> all = amlBaseModelDao.getAll();
        return AmlBaseModelConverter.do2dtoList(all);
    }

}
