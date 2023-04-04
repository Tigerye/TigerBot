package com.tigerobo.x.pai.api.utils;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description:
 * @modified By:
 * @version: $
 */
public class MapUtils<K, V> {
    /**
     * 设置字段值
     *
     * @param map   对象
     * @param field 字段名
     * @param val   字段值
     */
    public static <K, V> void set(Map<K, V> map, K field, V val) {
        if (Objects.isNull(map))
            map = Maps.newHashMap();
        map.put(field, val);
    }

    /**
     * 字段是否存在
     *
     * @param map   对象
     * @param field 字段名
     * @return true or false
     */
    public static <K, V> boolean exist(Map<K, V> map, K field) {
        if (Objects.isNull(map))
            return false;
        return map.containsKey(field);
    }

    /**
     * 获取字段值，不存在则返回默认值
     *
     * @param map        对象
     * @param field      字段名
     * @param defaultVal 默认值
     * @return 字段值
     */
    public static <K, V> String getString(Map<K, V> map, K field, String defaultVal) {
        if (Objects.isNull(map))
            return defaultVal;
        Object val = map.get(field);
        return (val == null || StringUtils.isEmpty(String.valueOf(val))) ? defaultVal : String.valueOf(val);
    }

    public static <K, V> V get(Map<K, V> map, K field, V defaultVal) {
        if (Objects.isNull(map))
            return defaultVal;
        return map.getOrDefault(field, defaultVal);
    }

    public static <K, V, T> T get(Map<K, V> map, K field, Class<T> clazz, T defaultVal) {
        return convert(get(map, field, null), clazz, defaultVal);
    }

    public static <K, V, T> List<T> getList(Map<K, V> map, K field, Class<T> clazz) {
        if (Objects.isNull(map))
            return null;
        if (map.containsKey(field) && map.get(field) != null) {
            List<T> valueList = new ArrayList<>();
            Object value = map.get(field);
            if (value instanceof List) {
                for (Object obj : (List) value) {
                    T val = convert(obj, clazz, null);
                    if (val != null)
                        valueList.add(val);
                }
            } else {
                T val = convert(value, clazz, null);
                if (val != null)
                    valueList.add(val);
            }
            return valueList;
        }
        return null;
    }

    public static <K, V, T> T[] getArray(Map<K, V> map, K field, Class<T> clazz) {
        if (Objects.isNull(map))
            return null;
        List<T> valueList = new ArrayList<>();
        if (map.containsKey(field) && map.get(field) != null) {
            Object value = map.get(field);
            if (value.getClass().isArray()) {
                for (Object obj : (Object[]) value) {
                    T val = convert(obj, clazz, null);
                    if (val != null)
                        valueList.add(val);
                }
            } else if (value instanceof List) {
                for (Object obj : (List) map.get(field)) {
                    T val = convert(obj, clazz, null);
                    if (val != null)
                        valueList.add(val);
                }
            } else {
                T val = convert(value, clazz, null);
                if (val != null)
                    valueList.add(val);
            }
        }
        T[] array = (T[]) new Object[valueList.size()];
        valueList.toArray(array);
        return array;
    }


    /**
     * Object转成指定的类型
     *
     * @param obj        目标对象
     * @param clazz      目标类型
     * @param defaultVal 默认值
     * @param <T>        泛型
     * @return 目标值对象
     */
    public static <K, V, T> T convert(Object obj, Class<T> clazz, T defaultVal) {
        try {
            if (obj != null) {
                if (obj.getClass() == clazz || clazz.isInstance(obj))
                    return clazz.cast(obj);
                else
                    return JSON.parseObject(JSON.toJSONString(obj), clazz);
            } else
                return defaultVal;
        } catch (Exception e) {
            return defaultVal;
        }
    }

    /**
     * 获取MAP子集
     *
     * @param map       对象
     * @param fieldList 字段列表
     * @return
     */
    public static <K, V> Map<K, V> sub(Map<K, V> map, List<K> fieldList) {
        Map<K, V> subMap = Maps.newHashMap();
        if (Objects.isNull(map) || CollectionUtils.isEmpty(fieldList))
            return subMap;
        for (K k : fieldList) {
            if (map.containsKey(k))
                subMap.put(k, map.get(k));
        }
        return subMap;
    }
}
