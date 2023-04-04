package com.tigerobo.pai.biz.test.common.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tigerobo.x.pai.api.constants.EsConstant;
import com.tigerobo.x.pai.biz.utils.RestUtil;
import com.tigerobo.x.pai.biz.utils.TimeUtil;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

public class CommonEsTest {

    @Test
    public void countTest(){
        String index = "pai_model_call_202111";
        String host = "http://ms-es-logs-1.aigauss.com:9200/";

        String url = host+index+"/_search";

        String agg= "{\"size\":0,\"aggs\":{\"age_count\":{\"value_count\":{\"field\":\"modelId\"}}}}";

        String post = RestUtil.post(url, JSON.parseObject(agg));

        JSONObject jsonObject = JSON.parseObject(post);

        System.out.println();

    }


    @Test
    public void deleteTest(){

        String prefix = "pai-t-";

        int year = 35497;

        int end = 35504;
        final RestTemplate restTemplate = RestUtil.getRestTemplate();
        String url = "http://ms-es-news-1.aigauss.com:9200/";
        for (int i = year; i <= end; i++) {

            StringBuilder builder = new StringBuilder();
            String index = prefix+i;

            builder.append(url).append(index);


            final String closeUrl = builder.toString();
            try {

                restTemplate.delete(closeUrl);
            }catch (Exception ex){
                final String message = ex.getMessage();
                if (!message.contains("no such index")){
                    System.out.println(builder.toString());
                    throw ex;
                }
            }
        }

    }

    @Test
    public void closeTest(){


        String prefix = "fin_news_";

        int year = 1999;

        int end = 2017;
        final RestTemplate restTemplate = RestUtil.getRestTemplate();
        String url = "http://ms-es-news-1.aigauss.com:9200/";
        for (int i = year; i <= end; i++) {

            StringBuilder builder = new StringBuilder();
            String index = prefix+i;

            builder.append(url).append(index).append("/_close");


            final String closeUrl = builder.toString();
            try {

                final String s = restTemplate.postForObject(closeUrl, null, String.class);
            }catch (Exception ex){
                final String message = ex.getMessage();
                if (!message.contains("no such index")){
                    System.out.println(builder.toString());
                    throw ex;
                }
            }
        }

    }

    @Test
    public void deleteAlaseTest(){
        String prefix = "fin_news_filtered_";
        String alias = "fin_news_current_reader_filtered";
        int start = 2002;
        int end = 2017;
        String url = "http://ms-es-news-1.aigauss.com:9200/";
        final RestTemplate restTemplate = RestUtil.getRestTemplate();
        for (int i = start; i <= end; i++) {

            String index = prefix + i;

            System.out.println(index);

            StringBuilder builder = new StringBuilder();
            builder.append(url);
            builder.append(index);
            builder.append("/");
            builder.append("_alias/");

            builder.append(alias);
            final String deleteUrl = builder.toString();
            try {
                restTemplate.delete(deleteUrl);
            }catch (Exception ex){
                System.out.println("del:" + deleteUrl);
                ex.printStackTrace();
            }

        }
    }
}
