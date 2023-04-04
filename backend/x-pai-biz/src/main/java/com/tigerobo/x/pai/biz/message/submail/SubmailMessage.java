package com.tigerobo.x.pai.biz.message.submail;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author:Wsen
 * @time: 2020/4/20
 **/
@Slf4j
public class SubmailMessage {

    private String appId ;
    private String appKey;
    private String signType;

    private static final String TO = "to";
    private static final String CONTENT = "content";


    private static String API_TIMESTAMP = "http://api.submail.cn/service/timestamp.json";
    private static final String API_SEND = "http://api.submail.cn/message/send.json";

    public static final String APPID = "appid";
    public static final String TIMESTAMP = "timestamp";
    public static final String SIGN_TYPE = "sign_type";
    public static final String SIGNATURE = "signature";
    public static final String APPKEY = "appkey";

    public SubmailMessage() {
        appId = "34745";
        appKey = "6bd24d262d87dd6a8cbca326eb1a162d";
        signType = "normal";

    }

    public String send(String mobile,String content){

        Map<String,Object> map = new TreeMap<>();
        map.put(TO,mobile);
        map.put(CONTENT,content);
        return send(map);
    }
    private String send(Map<String, Object> data){
        return request(API_SEND,data);
    }

    private String request(String url, Map<String, Object> data) {

        try (CloseableHttpClient closeableHttpClient = HttpClientBuilder.create().build()){
            HttpPost httppost = new HttpPost(url);
            httppost.addHeader("charset", "UTF-8");
            httppost.setEntity(build(closeableHttpClient,data));
            HttpResponse response = closeableHttpClient.execute(httppost);
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null) {
                String jsonStr = EntityUtils.toString(httpEntity, "UTF-8");
                return jsonStr;
            }
        } catch (Exception e) {
            log.error("data:"+ JSON.toJSONString(data),e);
        }
        return null;
    }


    /**
     * 将请求数据转换为HttpEntity
     *
     * @param data
     * @return HttpEntity
     */
    private HttpEntity build(CloseableHttpClient closeableHttpClient,Map<String, Object> data) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addTextBody(APPID,appId);
        // builder.setCharset(Charset.);
        builder.addTextBody(TIMESTAMP, this.getTimestamp(closeableHttpClient));
        builder.addTextBody(SIGN_TYPE, signType);
        // set the properties below for signature
        data.put(APPID, appId);
        data.put(TIMESTAMP, this.getTimestamp(closeableHttpClient));
        data.put(SIGN_TYPE, signType);
        ContentType contentType = ContentType.create(HTTP.PLAIN_TEXT_TYPE, HTTP.UTF_8);
        builder.addTextBody(SIGNATURE, createSignature(RequestEncoder.formatRequest(data)), contentType);
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String) {
                builder.addTextBody(key, String.valueOf(value), contentType);
            } else if (value instanceof File) {
                builder.addBinaryBody(key, (File) value);
            }
        }
        return builder.build();
    }


    /**
     * 请求时间戳
     *
     * @return timestamp
     */
    private String getTimestamp(CloseableHttpClient closeableHttpClient) {
        HttpGet httpget = new HttpGet(API_TIMESTAMP);
        HttpResponse response;
        try {
            response = closeableHttpClient.execute(httpget);
            HttpEntity httpEntity = response.getEntity();
            String jsonStr = EntityUtils.toString(httpEntity, "UTF-8");
            if (jsonStr != null) {
                JSONObject jsonObject = JSON.parseObject(jsonStr);
                return jsonObject.getString("timestamp");
            }
            closeableHttpClient.close();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String createSignature(String data) {
        if ("normal".equals(signType)) {
            return appKey;
        } else {
            return buildSignature(data);
        }
    }

    /**
     * 当 setSignType不正常时,创建 一个签名类型
     *
     * @param data
     *            请求数据
     * @return signature
     */
    private String buildSignature(String data) {
        // order is confirmed
        String jointData = appId + appKey + data + appId + appKey;
        if ("md5".equals(signType)) {
            return RequestEncoder.encode(RequestEncoder.MD5, jointData);
        } else if ("sha1".equals(signType)) {
            return RequestEncoder.encode(RequestEncoder.SHA1, jointData);
        }
        return null;
    }

    public static void main(String[] args) {
        SubmailMessage message = new SubmailMessage();
        message.send("13002150756","【test-why】你好，你的验证码是3373");
    }

}
