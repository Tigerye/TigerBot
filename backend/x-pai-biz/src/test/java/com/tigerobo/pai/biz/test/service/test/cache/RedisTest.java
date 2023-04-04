package com.tigerobo.pai.biz.test.service.test.cache;

import com.alibaba.fastjson.JSON;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.entity.IndexItem;
import com.tigerobo.x.pai.biz.biz.process.ApiProcessor;
import com.tigerobo.x.pai.biz.cache.RedisCacheService;
import com.tigerobo.x.pai.biz.cache.RedisStatisticService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class RedisTest extends BaseTest {


    @Autowired
    private RedisCacheService redisCacheService;

    @Autowired
    private ApiProcessor apiProcessor;
    @Autowired
    private RedisStatisticService redisStatisticService;

    @Test
    public void apiTest(){


        String apiKey = "c2e6cc0bdce407ca57b13f2c1845a839";
        final List<IndexItem> indices = apiProcessor.indices(apiKey);


        System.out.println(JSON.toJSONString(indices));

        final Integer integer = apiProcessor.doGetTotal(apiKey, "");
        System.out.println(integer);


    }

    @Test
    public void lpushTest(){


        String key = "test:20230222";
        redisCacheService.del(key);
        for (int i = 0; i < 10; i++) {
//            System.out.print(i+"\t");

            final Long lpush = redisCacheService.lpush(key, String.valueOf(i));
            System.out.println(i+"\t"+lpush);

        }
        System.out.println();
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        final List<String> lrange = redisCacheService.lrange(key, 0, 11);

        Collections.reverse(lrange);
        System.out.println(JSON.toJSONString(lrange));

        final String ltrim = redisCacheService.ltrim(key, 0, 2);
        System.out.println(ltrim);
        final List<String> lrange2 = redisCacheService.lrange(key, 0, 10);
        System.out.println(JSON.toJSONString(lrange2));
    }

    @Test
    public void statisticTest(){


        String key = "tmp:1104";

        for (int i = 0; i <5; i++) {
            final Long lpush = redisCacheService.lpush(key, String.valueOf(i));
            System.out.println(lpush);
        }

    }

    @Test
    public void ltrimTest(){
        String key = "tmp:1104";

        print(key);

        redisCacheService.ltrim(key,0,2);
        print(key);
    }

    private void print(String key) {
        final List<String> lrange = redisCacheService.lrange(key, 0, -1);
        for (String s : lrange) {
            System.out.println(s);
        }
    }

    @Test
    public void redisTest(){

        String key = "test:tep:1";
        Long incr = redisCacheService.incr(key);
        System.out.println(incr);
        incr = redisCacheService.incr(key);
        redisCacheService.expire(key,1);
        System.out.println(incr);
    }


    private String currYearmo() {
        return String.format("%d%02d", Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH) + 1);
    }

    @Test
    public void test(){
        String key = "test:blog:sort:list";
        Map<String,Double> map = new HashMap<>();
//
//        for (int i = 0; i < 200; i++) {
//            Long zadd = redisCacheService.zadd(key, i * 2D+1, String.valueOf(i));
//            System.out.println(zadd);
//        }

        Map<String, Object> data = redisCacheService.zgetPipe(Arrays.asList(key), 0, 100);


        System.out.println(JSON.toJSONString(data.get(key)));
    }
}
