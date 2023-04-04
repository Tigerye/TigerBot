package com.tigerobo.x.pai.biz.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tigerobo.x.pai.api.exception.APIException;
import com.tigerobo.x.pai.api.exception.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.InvalidParameterException;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description:
 * @modified By:
 * @version: $
 */
@Slf4j
@Component
public class WechatHandler {
    private final String HUBOUC_URI = "https://hubouc.tigerobo.com/api/mp/login/qr/getWechatInfoByCode";
    @Deprecated
    private final String TIGEROBO_APPID = "wx1932de2133a4a2ba";
//    private final String TIGEROBO_APPID = "wx7dce890fbc536f39";

    public JSONObject getWechatInfoByCode(String appId,String code) {
        if (StringUtils.isEmpty(code))
            throw new InvalidParameterException("code");
        // 构建请求
        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost(HUBOUC_URI);
        httpPost.addHeader("Content-Type", "application/json");
//        httpPost.addHeader("");
        JSONObject body = new JSONObject();
        if (StringUtils.isBlank(appId)){
            appId = TIGEROBO_APPID;
        }
        body.put("appId", appId);
        body.put("code", code);
        StringEntity stringEntity = new StringEntity(body.toJSONString(), ContentType.APPLICATION_JSON);
        stringEntity.setContentEncoding("utf-8");
        httpPost.setEntity(stringEntity);

        // 执行请求操作，并拿到结果
        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String result = EntityUtils.toString(response.getEntity(), "utf-8");
                JSONObject jsonObject = JSON.parseObject(result);
                return jsonObject.getJSONObject("data");
            } else {
                log.error("wechat-call-error,req:{},resp:{}",body.toJSONString(), response);
                throw new APIException(ResultCode.FAILED, "微信信息接口调用失败", null);
            }
        } catch (IOException e) {
            log.error("failed to execute", e);
            throw new APIException(ResultCode.FAILED, "获取用户微信信息失败", null);
        }
    }
}
