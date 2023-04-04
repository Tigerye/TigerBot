package com.tigerobo.x.pai.biz.utils.http;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpReqUtil {

    private static Logger logger = LoggerFactory.getLogger(HttpReqUtil.class);

    public static String reqPost(String url, String reqJson, int timeOut) {
        return reqPost(url, reqJson, timeOut, "");
    }

    public static String reqPost(String url, String reqJson, int timeOut, String info, int tryCount) {
        while (tryCount > 0) {
            tryCount = tryCount - 1;
            String response = reqPost(url, reqJson, timeOut, info);
            if (StringUtils.isNotBlank(response)) {
                return response;
            }
            logger.warn("post接口失败，还可以重试次数：" + tryCount);
        }
        return "";
    }

    public static String reqPost(String url, String reqJson, int timeOut, String info) {
        logger.info("Request url {} with request {}", url, reqJson);

        if (StringUtils.isEmpty(url)) {
            logger.warn("the input url is empty !");
            return "";
        }

        HttpURLConnection conn = null;
        long beginTime = System.currentTimeMillis();
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();

            // add request header
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestProperty("Connection", "close");
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setConnectTimeout(2000);
            conn.setReadTimeout(timeOut);
            conn.connect();

            // send request
            DataOutputStream out = new DataOutputStream(conn.getOutputStream());
            out.write(reqJson.getBytes(StandardCharsets.UTF_8));
            out.flush();
            out.close();

            //read response
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() < 300) {
                return readFromResponse(conn.getInputStream());
            } else {
                String errorResponse = readFromResponse(conn.getErrorStream());
                logger.error("http post请求失败，返回状态码：" + conn.getResponseCode() + ", 返回内容：" + errorResponse + ", 访问地址：" + url
                        + "\n请求参数" + reqJson);
                return "";
            }

        } catch (Exception e) {
            logger.error("connect error to: " + url, e);
            return "";
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            long endTime = System.currentTimeMillis();
            long costTime = endTime - beginTime;
            if (costTime > 300) {
                logger.info("请求耗时：" + costTime + "ms, info =" + info + ", url = " + url);
            }
        }
    }

    public static String reqPostWithBearerToken(String url, String token, String reqJson, int timeOut) {

        if (StringUtils.isEmpty(url)) {
            logger.warn("the input url is empty !");
            return "";
        }

        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();

            // add request header
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestProperty("Connection", "close");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setConnectTimeout(2000);
            conn.setReadTimeout(timeOut);
            conn.connect();

            // send request
            DataOutputStream out = new DataOutputStream(conn.getOutputStream());
            out.write(reqJson.getBytes(StandardCharsets.UTF_8));
            out.flush();
            out.close();

            //read response
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() < 300) {
                return readFromResponse(conn.getInputStream());
            } else {
                String errorResponse = readFromResponse(conn.getErrorStream());
                logger.error("http post请求失败，返回状态码：" + conn.getResponseCode() + ", 返回内容：" + errorResponse + ", 访问地址：" + url);
                return "";
            }

        } catch (Exception e) {
            logger.error("connect error to: " + url, e);
            return "";
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public static String reqPost(String url, String params, Map<String, String> headerMap) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;

        if (params != null) {
            StringEntity requestEntity = new StringEntity(params, Charset.forName("UTF-8"));
            requestEntity.setContentEncoding("UTF-8");
            requestEntity.setContentType("application/json");
            httpPost.setEntity(requestEntity);
        }

        if (headerMap!=null&&headerMap.size()>0) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                httpPost.setHeader(entry.getKey(), entry.getValue());
            }
        }

        try {
            response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity responseEntity = response.getEntity();
                return EntityUtils.toString(responseEntity);
            }
        } catch (Exception e) {
            logger.error("connect error to: " + url, e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpClient.close();
            } catch (IOException e) {
                logger.error("response close error: " + e.getMessage());
            }
        }

        return null;
    }


    private static String readFromResponse(InputStream inputStream) {
        try {
            StringBuffer sbf = new StringBuffer();
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader rd = new BufferedReader(isr);

            int BUFFER_SIZE=1024;
            char[] buffer = new char[BUFFER_SIZE]; // or some other size,
            int charsRead = 0;
            while ( (charsRead  = rd.read(buffer, 0, BUFFER_SIZE)) != -1) {
                sbf.append(buffer, 0, charsRead);
            }
//            String line;
//            while ((line = in.readLine()) != null) {
//                sbf.append(line);
//            }
            return sbf.toString();
        } catch (Exception e) {
            logger.error("解析返回数据失败",e);
            return "";
        }
    }

    public static String reqGet(String url, int timeOut) {

        if (StringUtils.isEmpty(url)) {
            logger.warn("the input url is empty !");
            return "";
        }

        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            // add request header
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Connection", "close");
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setConnectTimeout(2000);
            conn.setReadTimeout(timeOut);
            conn.connect();

            //read response
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() < 300) {
                return readFromResponse(conn.getInputStream());
            } else {
                String errorResponse = readFromResponse(conn.getInputStream());
                logger.error("http get请求失败，返回状态码：" + conn.getResponseCode() + ", 返回内容：" + errorResponse + ", 访问地址：" + url);
                return "";
            }
        } catch (Exception e) {
            logger.error("connect error to: " + url, e);
            //e.printStackTrace();
            return "";
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public static void main(String[] args) {
        String url = "http://admin-api.aigauss.com/api/ximei/video/videoUrlEncode";

        Map<String,Object> map = new HashMap<>();

        final String json = JSON.toJSONString(map);
        String response = HttpReqUtil.reqPost(url, json, 5*1000, "空");
        System.out.println(response);
    }
}
