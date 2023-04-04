package com.tigerobo.x.pai.biz.biz.model;

import com.tigerobo.x.pai.api.dto.model.ModelCategoryDto;
import com.tigerobo.x.pai.biz.converter.ModelCategoryConvert;
import com.tigerobo.x.pai.dal.biz.dao.model.ModelCategoryDao;
import com.tigerobo.x.pai.dal.biz.entity.model.ModelCategoryPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ModelCategoryService {

    @Autowired
    private ModelCategoryDao modelCategoryDao;

    public ModelCategoryDto getCategoryByUid(String uid){
        if (StringUtils.isBlank(uid)){
            return null;
        }

        ModelCategoryPo po = modelCategoryDao.getByUid(uid);
        return ModelCategoryConvert.po2dto(po);
    }

    public List<ModelCategoryDto> getByUids(List<String> uids){
        if (CollectionUtils.isEmpty(uids)){
            return new ArrayList<>();
        }
        return uids.stream().distinct().map(this::getCategoryByUid).filter(Objects::nonNull).collect(Collectors.toList());
    }
}
