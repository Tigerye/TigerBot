package com.tigerobo.x.pai.engine.lake;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tigerobo.x.pai.biz.utils.RestUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.*;

@Slf4j
@Service
public class LakeService {
    private  RestTemplate restTemplate = new RestTemplate();



    public String startTrain(String trainUrl , String configPath){
        log.warn("reqGet-lake:trainUrl,config-path:{}",configPath);
        String url = trainUrl+"?config_path={1}";
        String forObject = restTemplate.getForObject(url, String.class,configPath);
        log.info("reqGet:{}",forObject);
        return forObject;
    }

    public String online(String url,String modelName){
        String onlineUrl = url +"/deploy/load";
        log.info("online:modelName:{}",modelName);
        Map<String,Object> map = new HashMap<>();
        map.put("model_name",modelName);
        String result = RestUtil.post(onlineUrl,map);
        return result;
    }

    public String offline(String uri,String modelName){
        String useUrl = uri +"/deploy/unload";

        Map<String,Object> map = new HashMap<>();
        map.put("model_name",modelName);
        log.info("offline:modelName:{}",modelName);
        String result = RestUtil.post(useUrl,map);


        return result;
    }
    public String reqClient(String url,String path) {
        // 创建Httpclient对象
        CloseableHttpClient httpclient = HttpClients.createDefault();

        //response 对象
        CloseableHttpResponse response = null;
        try {
            // 定义请求的参数
            URI uri = new URIBuilder(url).setParameter("config_path", path).build();
            // 创建http GET请求
            HttpGet httpGet = new HttpGet(uri);
            // 执行http get请求
            response = httpclient.execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                String content = EntityUtils.toString(response.getEntity(), "UTF-8");
                //内容写入文件
                return content;
            }
        }catch (Exception ex){
            log.error("url:{},path:{},",url,path,ex);
        }finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpclient.close();
            }catch (Exception ex){

            }
        }
        return null;

    }


}
