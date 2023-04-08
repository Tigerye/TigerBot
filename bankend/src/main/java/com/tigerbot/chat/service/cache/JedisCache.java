package com.tigerbot.chat.service.cache;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.*;

import javax.annotation.PreDestroy;
import java.util.*;

/**
 * @author:Wsen
 * @time: 2019/12/6
 **/
@Slf4j
public class JedisCache {

    private JedisPool jedisPool;

    public JedisCache() {
    }


    protected void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public Long expire(String key,int seconds){
        if (checkNotInit()){
            return null;
        }
        try (Jedis client = jedisPool.getResource()) {
            return client.expire(key,seconds);
        } catch (Exception ex) {
            log.error("exist:key-" + key, ex);
        }
        return null;
    }


    public Long lpush(String key,String member){
        if (checkNotInit()){
            return null;
        }
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.lpush(key, member);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error happened when we do lpush on key:[{}] value:[{}]", key, member);
        }
        return null;
    }


    public List<String> lrange(String key,long start,long stop){
        if (checkNotInit()){
            return null;
        }
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.lrange(key, start, stop);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error happened when we do lrange on key:[{}] value:[{}]", key, stop);
        }
        return null;
    }
    public String ltrim(String key,long start,long end){
        if (checkNotInit()){
            return null;
        }
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.ltrim(key, start,end);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error happened when we do ltrim on key:[{}] start:[{},{}]", key, start,end);
        }
        return null;
    }


    private boolean checkNotInit(){
        if (jedisPool == null){
            log.error("redis 没有初始化");
        }
        return jedisPool==null;
    }

    protected JedisPoolConfig getConfig() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(1000);
        config.setMinIdle(20);
        config.setMaxIdle(150);
        config.setMaxWaitMillis(20000);
//        config.setBlockWhenExhausted(false);
        config.setTestOnBorrow(true);
        config.setTestOnReturn(true);
        return config;
    }

    @PreDestroy
    public void close() {
        if (jedisPool != null) {
            try {
                jedisPool.close();
            } catch (Exception ex) {
                log.error("close ", ex);
            }
        }
    }
}
