package com.tigerobo.x.pai.biz.serving.execute;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tigerobo.x.pai.api.biz.entity.API;
import com.tigerobo.x.pai.api.biz.entity.Model;
import com.tigerobo.x.pai.api.dto.ApiDto;
import com.tigerobo.x.pai.api.enums.Style;
import com.tigerobo.x.pai.api.exception.APIException;
import com.tigerobo.x.pai.api.exception.ResultCode;
import com.tigerobo.x.pai.biz.converter.APIConvert;
import com.tigerobo.x.pai.biz.serving.Executable;
import lombok.Data;
import lombok.NoArgsConstructor;
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
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.Map;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description:
 * @modified By:
 * @version: $
 */
@Data
@Slf4j
@NoArgsConstructor
public class UriApiExecutor implements Executable {
    String name;
    private String apiKey;
    private String apiUri;
    private String apiStyle;
    private Style style;
    Boolean showApi = true;

    //    private Model model;    // 关联模型
    ApiDto api;

    public UriApiExecutor(ApiDto api) {
        this.api = api;
        this.name = api.getName();
        this.apiKey = api.getUuid();
        this.apiUri = api.getUri();
        this.apiStyle = api.getStyle();
        this.showApi = api.getShowApi();

        style = Style.getByValue(apiStyle);
//        this(api,null);
    }


//
//    @Override
//    public API profile() {
//        return API.builder()
//                .uuid(getApiKey())
//                .uri(getApiUri())
//                .style(getApiStyle())
////                .demo(getApiDemo())
////                .apiKey(getApiKey())
////                .apiUri(getApiUri())
////                .apiStyle(getApiStyle())
//                .apiDemo(this.apiDemo)
//                .demo(this.demo)
//                .modelId(this.api.getId())
//
//                .modelUuid(this.api.getUuid()).build();
//    }

    @Override
    public API profileClean() {


        final API vo = APIConvert.dto2vo(this.api);

        boolean support = style != null && style.isSupportBatch();
        vo.setSupportBatch(support);

        vo.setShowApi(showApi);
        return vo;
    }
    @Override
    public ApiDto getApiDto() {
        return api;
    }


    @Override
    public Object execute(Map<String, Object> params) {
        if (StringUtils.isEmpty(this.getApiUri()))
            throw new NullPointerException("AbstractUriApiExecutor.uri");
//        log.info("url:{},params:{}",getApiUri(),JSON.toJSONString(params));
        // 构建请求
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String uri = this.apiUri;
        if (StringUtils.isBlank(uri)){
            throw new IllegalArgumentException("模型服务未部署");
        }
//        uri = uri.replace("https://pai.tigerobo.com/x-pai-serving", "http://localhost:8080");
        HttpPost httpPost = new HttpPost(uri);
        httpPost.addHeader("Content-Type", "application/json");
        JSONObject body = new JSONObject();
        body.putAll(params);
        StringEntity stringEntity = new StringEntity(body.toJSONString(), ContentType.APPLICATION_JSON);
        stringEntity.setContentEncoding("utf-8");
        httpPost.setEntity(stringEntity);

        // 执行请求操作，并拿到结果
        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String result = EntityUtils.toString(response.getEntity(), "utf-8");
                //http://localhost:8080
                return JSON.parseObject(result);
            } else {
                log.error("uri:{},param:{},{}", uri, JSON.toJSONString(params), response);
                throw new APIException(ResultCode.FAILED, "接口调用失败", null);
            }
        } catch (IOException e) {
            log.error("uri:{},param:{},failed to execute: {}", uri, JSON.toJSONString(params), e.getMessage());
            throw new APIException(ResultCode.FAILED, "模型服务调用失败", null);
        }
    }
}
