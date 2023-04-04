package com.tigerobo.x.pai.biz.utils;

//import org.springframework.http.client.SimpleClientHttpRequestFactory;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import java.util.Map;
@Slf4j
public class RestUtil {
    private static RestTemplate restTemplate;
    public static RestTemplate getRestTemplate(){
        if (restTemplate == null){
            synchronized (RestUtil.class){
//                SimpleClientHttpRequestFactory factory = getSimpleClientHttpRequestFactory();
                if(restTemplate == null){
                    HttpClient httpClient = httpClient();
                    HttpComponentsClientHttpRequestFactory apacheFactory = new HttpComponentsClientHttpRequestFactory(httpClient);


                    restTemplate = new RestTemplate(apacheFactory);
                    final List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
                    messageConverters.set(1,new StringHttpMessageConverter(StandardCharsets.UTF_8));

                }

//                if (restTemplate == null){
//                    restTemplate = new RestTemplate();
//                }
            }
        }
        return restTemplate;
    }

    private static SimpleClientHttpRequestFactory getSimpleClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory  factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(50000);
//                factory.setConnectionRequestTimeout(30000);
        factory.setConnectTimeout(3000);
        return factory;
    }

    public static HttpClient httpClient() {
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", SSLConnectionSocketFactory.getSocketFactory())
                .build();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);
        //设置整个连接池最大连接数 根据自己的场景决定
        connectionManager.setMaxTotal(200);
        //路由是对maxTotal的细分
        connectionManager.setDefaultMaxPerRoute(100);
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(30000) //服务器返回数据(response)的时间，超过该时间抛出read timeout
                .setConnectTimeout(5000)//连接上服务器(握手成功)的时间，超出该时间抛出connect timeout
                .setConnectionRequestTimeout(15000)//从连接池中获取连接的超时时间，超过该时间未拿到可用连接，会抛出org.apache.http.conn.ConnectionPoolTimeoutException: Timeout waiting for connection from pool
                .build();
        return HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(connectionManager)
                .build();
    }

    public static String postStr(String url,String reqData,MediaType mediaType){
        return postStr(getRestTemplate(),url,reqData,mediaType);
    }
    public static String postStr(RestTemplate restTemplate,String url,String reqData,MediaType mediaType){
        HttpHeaders headers = new HttpHeaders();
        if (mediaType!=null){
            headers.setContentType(mediaType);
        }else {
            headers.setContentType(MediaType.APPLICATION_JSON);
        }

        HttpEntity<String> request = new HttpEntity<>(reqData, headers);

        try {
            return restTemplate.postForObject(url, request, String.class);
        }catch (Exception ex){
            log.error("url:{},data:{}",url, JSON.toJSONString(reqData),ex);
        }
        return null;
    }

    public static String postWithFile(String url,MultiValueMap<String,Object> dataMap) {

        HttpHeaders headers = new HttpHeaders();
//        MediaType type = MediaType.parseMediaType("multipart/form-data");
        MediaType type = MediaType.MULTIPART_FORM_DATA;
        // 设置请求的格式类型
        headers.setContentType(type);

//        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();

        HttpEntity<MultiValueMap<String, Object>> files = new HttpEntity<>(dataMap, headers);
        RestTemplate restTemplate = RestUtil.getRestTemplate();
        try {
            return restTemplate.postForObject(url, files, String.class);
        }catch (Exception ex){
            log.error("url:{},param:{}",url,JSON.toJSONString(dataMap));
            throw new IllegalArgumentException("模型调用失败");
        }
    }

    public static String post(String url,Map<String,Object> reqData){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(reqData, headers);

        try {
            return getRestTemplate().postForObject(url, request, String.class);
        }catch (Exception ex){
            log.error("url:{},data:{}",url, JSON.toJSONString(reqData),ex);
        }
        return null;
    }

    public static String get(String uri,Map<String,Object> map){

        StringBuilder urlBuilder = new StringBuilder(uri);
        if (map!=null&&map.size()>0){
            int i=0;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (entry.getValue() ==null){
                    continue;
                }
                if(i==0){
                    urlBuilder.append("?");
                }else {
                    urlBuilder.append("&");
                }
                i++;
                urlBuilder.append(entry.getKey()).append("=").append(entry.getValue());
            }
        }
        return getRestTemplate().getForObject(urlBuilder.toString(),String.class);
    }

}
