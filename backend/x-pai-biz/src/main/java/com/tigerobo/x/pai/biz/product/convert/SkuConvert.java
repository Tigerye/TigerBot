package com.tigerobo.x.pai.biz.product.convert;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tigerobo.x.pai.api.product.dto.ApiSkuDto;
import com.tigerobo.x.pai.api.product.dto.ProductSkuDto;
import com.tigerobo.x.pai.biz.constant.SkuConstant;
import com.tigerobo.x.pai.dal.pay.entity.ProductSkuPo;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class SkuConvert {
    public static List<ProductSkuDto> convert(List<ProductSkuPo> pos){
        if (CollectionUtils.isEmpty(pos)){
            return null;
        }

        return pos.stream().map(SkuConvert::convert).collect(Collectors.toList());
    }

    public static ProductSkuDto convert(ProductSkuPo po){

        if (po == null){
            return null;
        }

        ProductSkuDto dto = new ProductSkuDto();
        BeanUtils.copyProperties(po,dto);
        final LinkedHashMap<String, Object> skuProperties = convertDtoProperties(po.getProperties());
        dto.setSkuProperties(skuProperties);
        return dto;
    }


    public static ApiSkuDto convertApiSku(ProductSkuPo po){

        if (po == null){
            return null;
        }

        ApiSkuDto dto = new ApiSkuDto();

        dto.setId(po.getId());
        dto.setShowName(po.getShowName());
        dto.setDesc(po.getDesc());

        dto.setPrice(po.getPrice());
        dto.setUnit(po.getItemUnit());
        dto.setPriceType(po.getPriceType());

        LinkedHashMap<String,Object> dtoProperties = convertDtoProperties(po.getProperties());
        dto.setProperties(dtoProperties);


        return dto;
    }

    public static LinkedHashMap<String,Object> convertDtoProperties(String json){
        if (StringUtils.isEmpty(json)){
            return new LinkedHashMap<>();
        }
        if (!json.trim().startsWith("{")){
            return new LinkedHashMap<>();
        }
        final JSONObject jsonObject = JSON.parseObject(json);
        if (jsonObject == null) {
            return null;
        }

        LinkedHashMap<String,Object> targetMap = new LinkedHashMap<>();

        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            final String key = entry.getKey();
            if (SkuConstant.REGION_PRICE.equalsIgnoreCase(key)){
                final JSONObject regionPriceProperty = jsonObject.getJSONObject(key);
                final LinkedHashMap<Long, BigDecimal> regionPriceMap = formatRegionPrice(regionPriceProperty);
                final LinkedHashMap<String, Object> regionDtoMap = convert2PriceRegionDtoMap(regionPriceMap);

                targetMap.putAll(regionDtoMap);
//                continue;
            }else {
                targetMap.put(key,entry.getValue());
            }
        }
        return targetMap;

    }

    private static LinkedHashMap<String,Object> convert2PriceRegionDtoMap(LinkedHashMap<Long, BigDecimal> regionMap) {
        if (regionMap == null){
            return null;
        }
        LinkedHashMap<String,String> regionDetailMap = new LinkedHashMap<>();
        if (!CollectionUtils.isEmpty(regionMap)){

            final ArrayList<Long> periods = new ArrayList<>(regionMap.keySet());

            for (int i = 0; i < periods.size(); i++) {

                final Long period = periods.get(i);

                String key = String.valueOf(period);
                if (i<periods.size()-1){
                    key += "-"+periods.get(i+1);
                }else {
                    key = "大于"+periods.get(i);
                }
                String value = regionMap.get(period).toString();
                regionDetailMap.put(key,value);
            }
        }
        LinkedHashMap<String,Object> dtoProperties = new LinkedHashMap();

        dtoProperties.put(SkuConstant.REGION_PRICE,regionDetailMap);
        return dtoProperties;
    }


    public static LinkedHashMap<Long, BigDecimal> convertPriceRegionMap(String properties) {
        final JSONObject jsonObject = JSON.parseObject(properties);
        if (jsonObject == null) {
            return null;
        }
        final JSONObject region_price = jsonObject.getJSONObject(SkuConstant.REGION_PRICE);

        if (region_price == null) {
            return null;
        }
        return formatRegionPrice(region_price);
    }

    private static LinkedHashMap<Long, BigDecimal> formatRegionPrice(JSONObject region_price) {
        LinkedHashMap<Long, BigDecimal> map = new LinkedHashMap<>();

        for (Map.Entry<String, Object> entry : region_price.entrySet()) {

            final String key = entry.getKey();
            final long regionStart = Long.parseLong(key);
            final BigDecimal price = region_price.getBigDecimal(key);
            map.put(regionStart, price);
        }
        return map;
    }

}
