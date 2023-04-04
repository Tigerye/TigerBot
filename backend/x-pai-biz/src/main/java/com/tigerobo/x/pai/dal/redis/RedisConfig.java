//package com.tigerobo.x.pai.dal.redis;
//
//import com.fasterxml.jackson.annotation.JsonAutoDetect;
//import com.fasterxml.jackson.annotation.PropertyAccessor;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
//import org.springframework.data.redis.serializer.StringRedisSerializer;
//
///**
// * @author: yicun.chen@tigerobo.com
// * @date: Created in 2021/1/4 9:14 PM
// * @description:
// * @modified By:
// * @version: $
// */
////@Configuration
//public class RedisConfig {
//    @Bean(name = "redisTemplate")
//    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
//        //为了平时开发方便,使用<String,Object>
//        RedisTemplate<String, Object> template = new RedisTemplate<>();
//        //连接Redis工厂
//        template.setConnectionFactory(redisConnectionFactory);
//
//        //序列化操作 Json序列化
//        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
//        //转译
//        ObjectMapper objectMapper = new ObjectMapper();
//        //方便的方法允许更改底层VisibilityCheckers的配置，以更改自动检测哪些属性的详细信息
//        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
//        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
//
//        // String的序列化
//        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
//
//        //key采用String的序列化方式
//        template.setKeySerializer(stringRedisSerializer);
//        //hash的key也采用String的序列化方式
//        template.setHashKeySerializer(stringRedisSerializer);
//        //value序列化方式采用jackson
//        template.setValueSerializer(jackson2JsonRedisSerializer);
//        //hash的value序列化方式采用jackson
//        template.setHashValueSerializer(jackson2JsonRedisSerializer);
//        //把所有的properties Set进去
//        template.afterPropertiesSet();
//
//        return template;
//    }
//
//}
