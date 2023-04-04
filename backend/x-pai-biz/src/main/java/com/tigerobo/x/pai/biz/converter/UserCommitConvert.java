package com.tigerobo.x.pai.biz.converter;

import com.tigerobo.x.pai.api.dto.UserCommitSiteDto;
import com.tigerobo.x.pai.api.enums.CommitSiteStatusEnum;
import com.tigerobo.x.pai.api.req.UserCommitSiteReq;
import com.tigerobo.x.pai.dal.biz.entity.user.UserCommitSitePo;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

public class UserCommitConvert {

    public static UserCommitSitePo addReq2po(UserCommitSiteReq dto){

        UserCommitSitePo po = new UserCommitSitePo();
        BeanUtils.copyProperties(dto,po);
        return po;
    }

    public static List<UserCommitSiteDto> po2dto(List<UserCommitSitePo> pos){

        if (CollectionUtils.isEmpty(pos)){
            return null;
        }
        return pos.stream().map(UserCommitConvert::po2dto).collect(Collectors.toList());
    }

    public static UserCommitSiteDto po2dto(UserCommitSitePo po){
        UserCommitSiteDto dto = new UserCommitSiteDto();
        BeanUtils.copyProperties(po,dto);

        Integer status = po.getStatus();
        CommitSiteStatusEnum statusEnum = CommitSiteStatusEnum.getByStatus(status);
        String statusName = statusEnum==null?"":statusEnum.getName();
        dto.setStatusName(statusName);
        return dto;
    }
}

