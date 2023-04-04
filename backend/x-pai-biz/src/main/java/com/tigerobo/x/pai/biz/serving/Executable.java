package com.tigerobo.x.pai.biz.serving;

import com.tigerobo.x.pai.api.biz.entity.API;
import com.tigerobo.x.pai.api.dto.ApiDto;
import com.tigerobo.x.pai.api.serving.vo.ApiReqVo;

import java.util.Map;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description:
 * @modified By:
 * @version: $
 */
public interface Executable {

    String getName();
    String getApiKey();

    String getApiUri();

    String getApiStyle();

//    API profile();

    API profileClean();

    ApiDto getApiDto();

    Object execute(Map<String, Object> params);

    default Object execute(Map<String, Object> params,Long reqId, ApiReqVo reqVo){
        return execute(params);
    };

    default Object batchExecute(Map<String, Object> params){
        return execute(params);
    }

    default boolean supportCacheResult(){
        return true;
    }


     default Boolean getShowApi(){
        return true;
    }
//    boolean ready();
}
