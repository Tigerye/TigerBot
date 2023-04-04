package com.tigerobo.x.pai.biz.serving.execute;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tigerobo.x.pai.api.biz.entity.API;
import com.tigerobo.x.pai.api.biz.entity.Model;
import com.tigerobo.x.pai.api.dto.ApiDto;
import com.tigerobo.x.pai.api.exception.APIException;
import com.tigerobo.x.pai.api.exception.ResultCode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import java.lang.IllegalArgumentException;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description: 开域问答 执行器
 * @modified By:
 * @version: $
 */
@Slf4j
@Data
//@AllArgsConstructor
public class OpenDomainQAApiExecutor extends UriApiExecutor {
    public OpenDomainQAApiExecutor(ApiDto api) {
        super(api);
    }

    private volatile boolean ready = true;

    @Override
    public JSONObject execute(Map<String, Object> params) {
        if (StringUtils.isEmpty(this.getApiUri()))
            throw new NullPointerException("AbstractUriApiExecutor.uri");
        // 构建请求
        RequestConfig defaultRequestConfig = RequestConfig.custom().setSocketTimeout(60000).setConnectTimeout(20000).setConnectionRequestTimeout(20000).build();
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig).build();
        HttpPost httpPost = new HttpPost(this.getApiUri());
        httpPost.addHeader("Content-Type", "application/json");
        JSONObject body = new JSONObject();
        if (params.containsKey("question"))
            body.put("question", params.get("question"));
        else if (params.containsKey("text"))
            body.put("question", params.get("text"));
        else if (params.containsKey("text_list"))
            body.put("question", ((List<String>) params.get("text_list")).get(0));
        else
            throw new IllegalArgumentException("AbstractUriApiExecutor.params[" + params.toString() + "]");
        StringEntity stringEntity = new StringEntity(body.toJSONString(), ContentType.APPLICATION_JSON);
        stringEntity.setContentEncoding("utf-8");
        httpPost.setEntity(stringEntity);

        // 执行请求操作，并拿到结果
        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String result = EntityUtils.toString(response.getEntity(), "utf-8");
                JSONArray jsonArray = JSON.parseArray(result);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("status", 0);
                jsonObject.put("result",jsonArray);

                return jsonObject;
            } else {
                log.error("{}", response);
                throw new APIException(ResultCode.FAILED, "接口调用失败", null);
            }
        } catch (ClientProtocolException e) {
            log.error("failed to test API: {}, {}, cause by {}", this.getApiKey(), this.getApiUri(), e.getMessage());
            log.error("", e);
            return null;
        } catch (IOException e) {
            log.error("failed to test API: {}, {}, , cause by {}", this.getApiKey(), this.getApiUri(), e.getMessage());
            log.error("", e);
            return null;
        }
    }
}
