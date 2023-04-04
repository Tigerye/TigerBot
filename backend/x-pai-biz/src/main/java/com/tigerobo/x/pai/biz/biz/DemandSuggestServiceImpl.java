package com.tigerobo.x.pai.biz.biz;

import com.google.common.base.Preconditions;
import com.tigerobo.x.pai.api.dto.DemandSuggest;
import com.tigerobo.x.pai.api.enums.UserRoleTypeEnum;
import com.tigerobo.x.pai.dal.auth.entity.UserDo;
import com.tigerobo.x.pai.dal.biz.dao.DemandSuggestDao;
import com.tigerobo.x.pai.dal.biz.entity.DemandSuggestDo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DemandSuggestServiceImpl {

    @Autowired
    private DemandSuggestDao demandSuggestDao;

    public void addSuggest(DemandSuggest suggest, UserDo userDo){
        Preconditions.checkArgument(suggest!=null&& StringUtils.isNotBlank(suggest.getDemandUuid()),"参数为空");

        Preconditions.checkArgument(StringUtils.isNotBlank(suggest.getDocUrl()),"没有文档");
        Preconditions.checkArgument(userDo!=null,"用户没权限");

        Integer roleType = userDo.getRoleType();
        Preconditions.checkArgument( UserRoleTypeEnum.hasRole(roleType,UserRoleTypeEnum.SUPER_MAN),"没有添加建议的权限");

        String name = userDo.getName();
        DemandSuggestDo convert = convert(suggest, String.valueOf(userDo.getId()), name);
        demandSuggestDao.add(convert);
    }

    public List<DemandSuggest> getDemandSuggestList(String demandUuid){
        List<DemandSuggestDo> doList = demandSuggestDao.getDemandList(demandUuid);

        List<DemandSuggest> dtoList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(doList)){
            dtoList = doList.stream().map(this::convert2Dto).collect(Collectors.toList());
        }
        return dtoList;
    }


    private DemandSuggestDo convert(DemandSuggest suggest,String create,String name){
        DemandSuggestDo po = new DemandSuggestDo();
        po.setTitle(suggest.getTitle());
        po.setDocUrl(suggest.getDocUrl());
        po.setOperator(name);
        po.setSuggestDesc(suggest.getSuggestDesc());
        po.setCreateBy(create);
        po.setUpdateBy(create);
        po.setDemandUuid(suggest.getDemandUuid());
        return po;
    }

    private DemandSuggest convert2Dto(DemandSuggestDo po){
        DemandSuggest demandSuggest = new DemandSuggest();
        demandSuggest.setCreateTime(po.getCreateTime());
        demandSuggest.setDemandUuid(po.getDemandUuid());
        demandSuggest.setDocUrl(po.getDocUrl());
        demandSuggest.setOperator(po.getOperator());

        demandSuggest.setSuggestDesc(po.getSuggestDesc());
        demandSuggest.setTitle(po.getTitle());
        return demandSuggest;
    }
}
