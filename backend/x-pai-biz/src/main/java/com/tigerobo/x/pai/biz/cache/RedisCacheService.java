package com.tigerobo.x.pai.biz.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.PostConstruct;

/**
 * @author:Wsen
 * @time: 2019/12/6
 **/
@Slf4j
@Service
public class RedisCacheService extends JedisCache {

    @Value("${xpai.redis.address}")
    String address;
    @Value("${xpai.redis.port}")
    Integer port;
    @Value("${xpai.redis.password}")
    String password;
    @Value("${xpai.redis.database}")
    Integer database;

    @PostConstruct
    public void init(){
        JedisPoolConfig config = getConfig();

        JedisPool jedisPool = new JedisPool(config
                , address
                , port
                , 3000
                , password
                , database);

        setJedisPool(jedisPool);
    }

}
