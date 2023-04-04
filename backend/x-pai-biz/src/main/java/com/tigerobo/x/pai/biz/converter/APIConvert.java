package com.tigerobo.x.pai.biz.converter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tigerobo.x.pai.api.biz.entity.API;
import com.tigerobo.x.pai.api.dto.ApiDto;
import com.tigerobo.x.pai.dal.biz.entity.ApiDo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Map;

public class APIConvert {

    public static ApiDto po2dto(ApiDo apiDo) {
        if (apiDo == null){
            return null;
        }

        JSONObject demo = StringUtils.isBlank(apiDo.getDemo())?new JSONObject():JSON.parseObject(apiDo.getDemo());
        JSONObject apiDemo = null;
        if (!StringUtils.isBlank(apiDo.getPageDemo())){
            apiDemo = JSON.parseObject(apiDo.getPageDemo());
        }
        ApiDto dto = new ApiDto();

        dto.setId(apiDo.getId());
        dto.setUuid(apiDo.getUuid());
        dto.setName(apiDo.getName());
        dto.setIntro(apiDo.getIntro());
        dto.setDesc(apiDo.getDesc());
        dto.setImage(apiDo.getImage());
        dto.setUri(apiDo.getUri());
        dto.setStyle(apiDo.getStyle());

        final Boolean showApi = apiDo.getShowApi();
        dto.setShowApi(showApi);
        dto.setDemo(demo);
        dto.setPageDemo(apiDemo);
        dto.setStatus(apiDo.getStatus());
        dto.setModelId(apiDo.getModelId());
        dto.setModelUuid(apiDo.getModelUuid());
        dto.setBaseModelUid(apiDo.getBaseModelUid());

        dto.setOriginCallCount(apiDo.getOriginCallCount());

        return dto;
    }
    final static int wan = 10000;
    public static API dto2vo(ApiDto api){
        Map<String, Object> pageDemo = api.getPageDemo();
        Map<String, Object> apiDemo = CollectionUtils.isEmpty(pageDemo) ? api.getDemo() : pageDemo;
        API vo = new API();
        vo.setApiKey(api.getUuid());
        vo.setApiStyle(api.getStyle());
        vo.setBaseModelUid(api.getBaseModelUid());
        vo.setApiDemo(apiDemo);

        final Integer originCallCount = api.getOriginCallCount();
        String originCallCountStr = getCountString(originCallCount);
        vo.setOriginCallCountValue(originCallCountStr);

        return vo;
    }

    public static String getCountString(Integer originCallCount) {
        if (originCallCount==null){
            return null;
        }

        if (originCallCount ==0){
            return "0";
        }
        String originCallCountStr = null;
        final int wanOffset = originCallCount / wan;
        final int remainOffset = originCallCount % wan;
        StringBuilder builder = new StringBuilder();
        if (wanOffset>0){
            builder.append(wanOffset).append("w");
        }
        if (remainOffset>0){
            builder.append(remainOffset);
        }
        originCallCountStr = builder.toString();
        return originCallCountStr;
    }

}
