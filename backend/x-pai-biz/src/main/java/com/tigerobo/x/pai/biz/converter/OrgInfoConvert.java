package com.tigerobo.x.pai.biz.converter;

import com.tigerobo.x.pai.api.auth.entity.OrgInfoDto;
import com.tigerobo.x.pai.api.enums.OrgVerifyStatusEnum;
import com.tigerobo.x.pai.dal.auth.entity.OrgInfoPo;

public class OrgInfoConvert {


    public static OrgInfoDto convert(OrgInfoPo po){
        if (po == null){
            return null;
        }
        OrgInfoDto infoDto = new OrgInfoDto();

        infoDto.setId(po.getId());
        infoDto.setUserId(po.getUserId());
        infoDto.setFullName(po.getFullName());
        infoDto.setShortName(po.getShortName());
        infoDto.setContactName(po.getContactName());
        infoDto.setContactMobile(po.getContactMobile());
        infoDto.setVerifyStatus(po.getVerifyStatus());
        infoDto.setVerifyStatusName(OrgVerifyStatusEnum.getStatusName(po.getVerifyStatus()));
        infoDto.setVerifyTime(po.getVerifyTime());
        infoDto.setReason(po.getReason());
        return infoDto;
    }


    public static OrgInfoPo convert(OrgInfoDto dto){
        if (dto == null){
            return null;
        }

        OrgInfoPo po = new OrgInfoPo();
        po.setId(dto.getId());
        po.setUserId(dto.getUserId());
        po.setFullName(dto.getFullName());
        po.setShortName(dto.getShortName());
        po.setContactMobile(dto.getContactMobile());
        po.setContactName(dto.getContactName());
        po.setVerifyStatus(dto.getVerifyStatus());
        po.setVerifyTime(dto.getVerifyTime());
        po.setReason(dto.getReason());
        return po;
    }

}
