package com.tigerobo.pai.common.util;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.util.CollectionUtils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import java.io.*;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author:Wsen
 * @time: 2018/12/12
 **/
@Slf4j
public class OkHttpUtil {
    private static OkHttpClient httpClient;
    private static OkHttpClient longHttpClient;

    private static Integer HTTP_WRITE_TIMEOUT = 10000;
    private static Integer HTTP_CONNECT_TIMEOUT = 10000;
    private static Integer HTTP_READ_TIMEOUT = 30 * 1000;
    private static Integer MAX_IDLE_CONNECTIONS = 150;
    private static Integer KEEP_ALIVE_DURATION = 10;

    public static OkHttpClient getOkHttp() {
        if (httpClient == null) {
            synchronized (OkHttpUtil.class) {
                if (httpClient == null) {
                    instanceClient();
                }
            }
        }
        return httpClient;
    }

    public static OkHttpClient getLongOkHttp() {
        if (longHttpClient == null) {
            synchronized (OkHttpUtil.class) {
                if (longHttpClient == null) {
                    OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
//        httpClientBuilder.cookieJar(new MyCookieJar());

                    ConnectionPool connectionPool = new ConnectionPool(MAX_IDLE_CONNECTIONS, KEEP_ALIVE_DURATION, TimeUnit.MINUTES);

                    httpClientBuilder.connectTimeout(HTTP_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                            .connectionPool(connectionPool)
                            .readTimeout(20, TimeUnit.MINUTES)
                            .writeTimeout(HTTP_WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
                            .retryOnConnectionFailure(true)
//                .sslSocketFactory(getTrustedSSLSocketFactory())
                            .hostnameVerifier(new HostnameVerifier() {
                                public boolean verify(String hostname, SSLSession session) {
                                    return true;
                                }
                            });
                    longHttpClient = httpClientBuilder.build();
                }
            }
        }
        return longHttpClient;
    }

    private static void instanceClient() {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
//        httpClientBuilder.cookieJar(new MyCookieJar());

        ConnectionPool connectionPool = new ConnectionPool(MAX_IDLE_CONNECTIONS, KEEP_ALIVE_DURATION, TimeUnit.MINUTES);

        httpClientBuilder.connectTimeout(HTTP_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                .connectionPool(connectionPool)
                .readTimeout(HTTP_READ_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(HTTP_WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true)
//                .sslSocketFactory(getTrustedSSLSocketFactory())
                .hostnameVerifier(new HostnameVerifier() {
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
        httpClient = httpClientBuilder.build();
    }


    public static String jsonPost(String url, Map<String, Object> params) {
        String data = JSON.toJSONString(params);
        return jsonPost(url, data);
    }


    public static String jsonPost(String url, String data) {
        OkHttpClient okHttp =  getOkHttp();
        return jsonPost(okHttp,url, data, null);
    }

    public static String jsonPost(OkHttpClient okHttp ,String url, String data, Map<String, String> headerMap) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, data);

        Request.Builder builder = new Request.Builder();
        if (headerMap != null && headerMap.size() > 0) {
            log.info("url:{},data:{},header:{}", url, data, headerMap);
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }

        Request request = builder.url(url).post(body).build();

        try (Response execute = okHttp.newCall(request).execute()) {
            int code = execute.code();
            if (execute.isSuccessful()) {
                return execute.body().string();
            } else {
                log.error("jsonPost,失败，code:{},url-{},data-{},error-{}", code, new Object[]{url, data, execute.message()});
            }
        } catch (Exception ex) {
            log.error("jsonPost,ioException,url:{}," + url, ex);
        }

        return null;
    }

    public static String get(String urlPath, Map<String, Object> params) {
        StringBuilder builder = new StringBuilder(urlPath);
        if (!CollectionUtils.isEmpty(params)) {
            builder.append("?");
            boolean isFirst = true;
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    builder.append("&");
                }
                builder.append(entry.getKey() + "=" + entry.getValue());
            }
        }
        String url = builder.toString();
//        System.out.println(url);
        Request request = new Request.Builder().url(url).get().build();
        OkHttpClient okHttp = getOkHttp();
        try (Response execute = okHttp.newCall(request).execute();) {
            if (execute.isSuccessful()) {
                return execute.body().string();
            } else {
                log.error("");
            }

        } catch (Exception e) {
            log.error("url-" + url + ",param-" + JSON.toJSONString(params), e);
        }
        return null;
    }
}
