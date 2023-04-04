package com.tigerobo.x.pai.api.serving.service;

import com.tigerobo.x.pai.api.serving.vo.ApiReqVo;
import com.tigerobo.x.pai.api.serving.vo.ApiResultVo;
import com.tigerobo.x.pai.api.serving.vo.ApiVo;

import java.util.List;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description: 服务模块-API服务接口定义
 * @modified By:
 * @version: $
 */
public interface ApiService {

    ApiResultVo execute(ApiReqVo request) ;

    ApiResultVo executeServing(ApiReqVo request);

    ApiResultVo getApiDoc(ApiReqVo apiReqVo);
}