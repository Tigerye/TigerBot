package com.tigerbot.chat.service.cache;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
public class RedisCacheService extends JedisCache{


    @Value("${pai.redis.address:}")
    String address;
    @Value("${pai.redis.port:}")
    Integer port;
    @Value("${pai.redis.password:}")
    String password;
    @Value("${pai.redis.database:}")
    Integer database;

    @PostConstruct
    public void init(){
        if (StringUtils.isBlank(address)){
            return;
        }
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
