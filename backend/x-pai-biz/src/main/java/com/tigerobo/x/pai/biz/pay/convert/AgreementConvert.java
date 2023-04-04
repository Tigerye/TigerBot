package com.tigerobo.x.pai.biz.pay.convert;

import com.tigerobo.x.pai.api.vo.api.AgreementVo;
import com.tigerobo.x.pai.biz.product.convert.SkuConvert;
import com.tigerobo.x.pai.biz.utils.TimeUtil;
import com.tigerobo.x.pai.dal.biz.entity.ApiDo;
import com.tigerobo.x.pai.dal.pay.entity.ApiAgreementPo;
import com.tigerobo.x.pai.dal.pay.entity.ProductSkuPo;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class AgreementConvert {
    public static List<AgreementVo> convert2vo(List<ApiAgreementPo> pos){

        if (CollectionUtils.isEmpty(pos)){
            return null;
        }
        return pos.stream().map(AgreementConvert::convert2vo).collect(Collectors.toList());

    }
    public static AgreementVo convert2vo(ApiAgreementPo po){

        if (po == null){
            return null;
        }
        AgreementVo vo = new AgreementVo();

        vo.setId(po.getId());
        vo.setModelId(po.getModelId());
        vo.setPrice(po.getPrice());
        vo.setUnit(po.getUnit());
        vo.setPriceType(po.getPriceType());
        vo.setSkuId(po.getSkuId());
        vo.setName(po.getName());

        final LinkedHashMap<String, Object> dtoProperties = SkuConvert.convertDtoProperties(po.getProperties());
        vo.setProperties(dtoProperties);
        vo.setCreateTime(po.getCreateTime());

        return vo;
    }


    public static ApiAgreementPo build(Integer userId, ApiDo apiDo, ProductSkuPo skuPo){
        ApiAgreementPo po = new ApiAgreementPo();
        po.setUserId(userId);
        po.setStartDay(TimeUtil.getDayValue(new Date()));
        po.setName(apiDo.getName());
        po.setModelName(apiDo.getName());
        po.setModelId(apiDo.getUuid());
        po.setSkuId(skuPo.getId());
        po.setPrice(skuPo.getPrice());
        po.setProperties(skuPo.getProperties());
        po.setPriceType(skuPo.getPriceType());
        po.setUnit(skuPo.getItemUnit());
        return po;
    }

}
