package com.tigerobo.x.pai.biz.utils.http;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.MapUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by lituo on 2019-10-15
 */
public class HttpReqFileUtil {

    private static Logger logger = LoggerFactory.getLogger(HttpReqFileUtil.class);

    public static String reqPostWithFile(String url, String fileNameKey, String fileName,
                                         InputStream inputStream, Map<String,Object> params,
                                         Map<String, String> headerMap,
                                         Integer timeout) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;

        if (MapUtils.isNotEmpty(headerMap)) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                httpPost.setHeader(entry.getKey(), entry.getValue());
            }
        }
        if (timeout == null){
            timeout = 30;
        }
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeout)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000).build();
//                .setSocketTimeout(1000).setConnectTimeout(1000).build();
        try {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addBinaryBody(fileNameKey, inputStream, ContentType.MULTIPART_FORM_DATA, fileName);// 文件流
            builder.addTextBody("filename", fileName);

            if (MapUtils.isNotEmpty(params)){
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    builder.addTextBody(entry.getKey(),String.valueOf(entry.getValue()));
                }
            }

            HttpEntity entity = builder.build();
            httpPost.setEntity(entity);
            httpPost.setConfig(requestConfig);


            response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity responseEntity = response.getEntity();
                return EntityUtils.toString(responseEntity);
            }
        } catch (SocketTimeoutException ex){
            logger.error("url:{},params:{},",url, JSON.toJSONString(params),ex);
            throw new IllegalArgumentException("请求处理超时");
        }catch (Exception e) {
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

    public static void main(String[] args) {
        String url = "http://gbox9.aigauss.com:9526/infer";
        String fileNameKey = "video";

        String filePath = "C:\\Users\\why19\\Downloads\\925a580adb1917de02dc85211e01fca0.mp4";
        Map<String,Object> params = new LinkedHashMap<>();
        params.put("output_path","/mnt/xpai/application/test/space_ac_det/01");
        final File file = new File(filePath);

        try(InputStream inputStream = new FileInputStream(file)){
            final String s = reqPostWithFile(url, fileNameKey, file.getName(), inputStream, params,
                    null,50);
            System.out.println(s);
        }catch (Exception ex){

        }


    }
}
