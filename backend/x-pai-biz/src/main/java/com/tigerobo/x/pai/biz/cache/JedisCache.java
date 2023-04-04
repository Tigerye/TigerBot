package com.tigerobo.x.pai.biz.cache;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.jedis.params.SetParams;

import javax.annotation.PreDestroy;
import java.util.*;
import java.util.stream.Collectors;

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

    public JedisPool getJedisPool(){
        return jedisPool;
    }

    public int getNum(String key){
        if (StringUtils.isEmpty(key)){
            return 0;
        }
        String s = get(key);
        if (org.apache.commons.lang3.StringUtils.isBlank(s)){
            return 0;
        }
        if (!s.matches("\\d+")){
            return 0;
        }
        return Integer.parseInt(s);
    }
    public Integer getInteger(String key){

        final Long v = getLong(key);
        return v == null?null:v.intValue();
    }

    public Long getLong(String key){
        if (StringUtils.isEmpty(key)){
            return null;
        }
        String s = get(key);
        if (org.apache.commons.lang3.StringUtils.isBlank(s)){
            return null;
        }
        if (!s.matches("\\d+")){
            return null;
        }
        return Long.parseLong(s);
    }


    public String get(String key) {
        try (Jedis client = jedisPool.getResource()) {
            return client.get(key);
        } catch (Exception ex) {
            log.error("get:key-" + key, ex);
        }
        return null;
    }

    public boolean exist(String key){
        try (Jedis client = jedisPool.getResource()) {
            return client.exists(key);
        } catch (Exception ex) {
            log.error("exist:key-" + key, ex);
        }
        return false;
    }


    public Long incr(String key){
        try (Jedis client = jedisPool.getResource()) {
            return client.incr(key);
        } catch (Exception ex) {
            log.error("incr:key-" + key, ex);
        }
        return null;
    }

    public Long incr(String key,Integer count){
        try (Jedis client = jedisPool.getResource()) {
            return client.incrBy(key,count);
        } catch (Exception ex) {
            log.error("incr:key-" + key, ex);
        }
        return null;
    }

    public Long decr(String key){
        try (Jedis client = jedisPool.getResource()) {
            return client.decr(key);
        } catch (Exception ex) {
            log.error("incr:key-" + key, ex);
        }
        return null;
    }
    public Object eval(String script,List<String> keys, List<String> values){
        try (Jedis client = jedisPool.getResource()) {
            return client.eval(script,keys,values);
        } catch (Exception ex) {
            log.error("eval:keys-" + keys, ex);
        }
        return null;
    }

    public Long expire(String key,int seconds){
        try (Jedis client = jedisPool.getResource()) {
            return client.expire(key,seconds);
        } catch (Exception ex) {
            log.error("exist:key-" + key, ex);
        }
        return null;
    }


    public List<String> get(List<String> keys) {
        try (Jedis client = jedisPool.getResource()) {
            Pipeline pipeline = client.pipelined();
            keys.forEach(pipeline::get);
            List<Object> objects = pipeline.syncAndReturnAll();
            List<String> result = new ArrayList<>();
            if (objects != null && objects.size() > 0) {
                Object[] arr = objects.stream().filter(object -> object instanceof JedisDataException).limit(1).toArray();
                if (arr.length > 0) {
                    log.error("redis set 异常", (Exception) arr[0]);
                } else {
                    result = objects.stream().map(s -> (String) s).collect(Collectors.toList());
                }
            }
            return result;
        } catch (Exception ex) {
            log.error("redis set", ex);
            return null;
        }
    }

    public String setNx(String key,String value,long expMills){
        try (Jedis client = jedisPool.getResource()) {
            SetParams params = new SetParams();
            params.nx();
            params.px(expMills);
            return client.set(key, value,params);
        } catch (Exception ex) {
            log.error("set: key-" + key + "value-" + value, ex);
        }
        return null;
    }

    public String set(String key, String value,int expSeconds) {
        try (Jedis client = jedisPool.getResource()) {
            SetParams params = new SetParams();
            params.ex(expSeconds);
            return client.set(key, value,params);
        } catch (Exception ex) {
            log.error("set: key-" + key + "value-" + value, ex);
        }
        return null;
    }

    public String set(String key, String value) {
        try (Jedis client = jedisPool.getResource()) {
            SetParams params = new SetParams();
            return client.set(key, value,params);
        } catch (Exception ex) {
            log.error("set: key-" + key + "value-" + value, ex);
        }
        return null;
    }


    public void set(Map<String, String> input) {
        set(input, null);
    }

    public void set(Map<String, String> input, Integer secondsToExpire) {
        SetParams setParams = null;
        if (secondsToExpire != null) {
            setParams = new SetParams();
            setParams.ex(secondsToExpire);
        }
        try (Jedis client = jedisPool.getResource()) {
            Pipeline pipeline = client.pipelined();
            if (setParams == null) {
                input.forEach(pipeline::set);
            } else {
                SetParams inputParam = setParams;
                input.forEach((key, value) -> pipeline.set(key, value, inputParam));
            }
            pipeline.sync();
            List<Object> objects = pipeline.syncAndReturnAll();
            if (objects != null && objects.size() > 0) {
                Object[] arr = objects.stream().filter(object -> object instanceof JedisDataException).limit(1).toArray();
                if (arr.length > 0) {
                    log.error("redis set 异常", (Exception) arr[0]);
                }
            }
        } catch (Exception ex) {
            log.error("redis set", ex);
        }
    }

    public String hget(String key, String field) {
        try (Jedis client = jedisPool.getResource()) {
            return client.hget(key, field);
        } catch (Exception ex) {
            log.error("hget:key-" + key + "field-" + field, ex);
        }

        return null;
    }

    public Map<String, String> hgetAll(String key) {
        try (Jedis client = jedisPool.getResource()) {
            return client.hgetAll(key);
        } catch (Exception ex) {
            log.error("hget:key-" + key, ex);
        }
        return new HashMap<>();
    }


    public Double hincrByFloat(String key,String field,double value) {
        try (Jedis client = jedisPool.getResource()) {

            return client.hincrByFloat(key,field,value);
        } catch (Exception ex) {
            log.error("hincrBy:key-{},field:{},value:{}" , key,field,value, ex);
        }

        return null;
    }


    public void hset(String key, String field, String value) {

        try (Jedis jedis = jedisPool.getResource()) {
            jedis.hset(key, field, value);
        } catch (Exception e) {
            // TODO: handle exception
            log.error(e.getMessage());
        }
    }

    public String hmset(String key, Map<String, String> info) {
        String result = "";

        try (Jedis jedis = jedisPool.getResource();) {
            result = jedis.hmset(key, info);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return result;
    }

    public String hmset(String key, Map<String, String> info, Integer seconds) {
        String result = "";

        try (Jedis jedis = jedisPool.getResource();) {
            result = jedis.hmset(key, info);
            jedis.expire(key, seconds);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return result;
    }

    public Long zadd(String key, Double score, String member) {
        Long result = 0L;

        score = Optional.ofNullable(score).orElse(0D);
        try (Jedis jedis = jedisPool.getResource()) {
            result = jedis.zadd(key, score, member);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error happened when we do zadd on key:[{}] value:[{}]", key, member);
        }
        return result;
    }

    public Long lpush(String key,String member){
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.lpush(key, member);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error happened when we do lpush on key:[{}] value:[{}]", key, member);
        }
        return null;
    }

    public Long rpush(String key,String member){
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.rpush(key, member);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error happened when we do rpush on key:[{}] value:[{}]", key, member);
        }
        return null;
    }
    public List<String> lrange(String key,long start,long stop){
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.lrange(key, start, stop);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error happened when we do lrange on key:[{}] value:[{}]", key, stop);
        }
        return null;
    }
    public String ltrim(String key,long start,long end){
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.ltrim(key, start,end);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error happened when we do ltrim on key:[{}] start:[{},{}]", key, start,end);
        }
        return null;
    }
    public Map<String,Map<String,String>> pipeHgetAll(List<String> keys){
        if (keys==null||keys.isEmpty()){
            return null;
        }
        Map<String,Map<String,String>> ret = new HashMap<>();
        try (Jedis resource = jedisPool.getResource(); Pipeline pipelined = resource.pipelined()) {
            for (String key : keys) {
                pipelined.hgetAll(key);
            }
            List<Object> objects = pipelined.syncAndReturnAll();
            if (objects == null || objects.isEmpty()) {
                return null;
            }
            for (int i = 0; i < objects.size(); i++) {
                Object object = objects.get(i);
                String key = keys.get(i);
                if (object instanceof JedisDataException) {
                    log.error("pipeGet error", (JedisDataException) object);
                }else if (object instanceof Map){
                    Map<String,String> map = (Map<String,String>)object;
                    ret.put(key,map);
                }
            }
            return ret;
        } catch (Exception ex) {
            log.error("pipeSet", ex);
        }
        return null;
    }



    public Set<Tuple> zrevrangeWithScores(String key, int start, int stop) {
        try (Jedis client = jedisPool.getResource()) {
            return client.zrevrangeWithScores(key, start, stop);
        } catch (Exception ex) {
            log.error("hget:key-" + key, ex);
        }
        return null;
    }

    public ScanResult<String> scan(String cursor, String pattern) {
        ScanParams params = new ScanParams().count(20000).match(pattern);

        try (Jedis client = jedisPool.getResource()) {
            return client.scan(cursor, params);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public Long del(String key) {
        try (Jedis client = jedisPool.getResource()) {
            return client.del(key);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public Set<String> zrevrange(String key, int start, int stop) {
        try (Jedis client = jedisPool.getResource()) {
            return client.zrevrange(key, start, stop);
        } catch (Exception ex) {
            log.error("hget:key-" + key, ex);
        }
        return null;
    }


    public Long sadd(String key, String... member) {
        try (Jedis client = jedisPool.getResource()) {
            return client.sadd(key, member);
        } catch (Exception ex) {
            log.error("sadd:key-{},member:{}", key,member, ex);
        }
        return null;
    }

    public Long srem(String key, String... member) {
        try (Jedis client = jedisPool.getResource()) {
            return client.srem(key, member);
        } catch (Exception ex) {
            log.error("srem:key-{},member:{}", key,member, ex);
        }
        return null;
    }

    public Set<String> smembers(String key) {
        try (Jedis client = jedisPool.getResource()) {
            return client.smembers(key);
        } catch (Exception ex) {
            log.error("smembers:key-{}", key, ex);
        }
        return null;
    }

    public boolean sIsMember(String key, String member) {
        try (Jedis client = jedisPool.getResource()) {
            return client.sismember(key, member);
        } catch (Exception ex) {
            log.error("sIsMember:key-{},member:{}", key,member, ex);
        }
        return false;
    }


    public Map<String, Object> zgetPipe(List<String> keys, int start, int size) {
        try (Jedis resource = jedisPool.getResource(); Pipeline pipelined = resource.pipelined()) {
            for (String key : keys) {
                pipelined.zrevrange(key, start, size);
            }
            List<Object> objects = pipelined.syncAndReturnAll();
            Map<String, Object> keyMap = new HashMap<>();
            if (objects != null && objects.size() > 0) {
                for (int i = 0; i < objects.size(); i++) {
                    Object object = objects.get(i);
                    if (object instanceof JedisDataException) {
                        log.error("异常", (Exception) object);
                    } else {
                        keyMap.put(keys.get(i), object);
                    }
                }
            }
            return keyMap;
        } catch (Exception ex) {
            String key = (keys == null || keys.isEmpty()) ? "" : keys.get(0);
            log.error("zaddPipe:key-" + key, ex);
        }
        return null;
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
