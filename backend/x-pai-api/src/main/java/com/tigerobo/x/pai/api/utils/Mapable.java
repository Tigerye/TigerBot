package com.tigerobo.x.pai.api.utils;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
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
public interface Mapable<K, V> {
    default Map<K, V> get() {
        return Maps.newHashMap();
    }

    default void set(Map<K, V> map) {
        get().putAll(map);
    }

    default void addAll(Map<K, V> map) {
        if (Objects.isNull(this.get()))
            this.set(Maps.newHashMap());
        this.get().putAll(map);
    }

    default int size() {
        return this.get().size();
    }

    @JSONField(serialize = false, deserialize = false)
    @JsonIgnore
    default boolean isEmpty() {
        return CollectionUtils.isEmpty(this.get());
    }

    @JSONField(serialize = false, deserialize = false)
    @JsonIgnore
    default boolean isNull() {
        return Objects.isNull(this.get());
    }

    @JSONField(serialize = false, deserialize = false)
    @JsonIgnore
    default boolean hasValue(K field) {
        if (this.isEmpty())
            return false;
        return this.exist(field)
                && this.getOrDefault(field, null) != null
                && !StringUtils.isEmpty(this.getString(field, ""))
                && !this.getString(field, "").equalsIgnoreCase("[]")
                && !this.getString(field, "").equalsIgnoreCase("{}");
    }

    default V getOrDefault(K field, V defaultVal) {
        return MapUtils.<K, V>get(this.get(), field, defaultVal);
    }

    default String getString(K field) {
        return MapUtils.<K, V>getString(this.get(), field, null);
    }

    default String getString(K field, String defaultVal) {
        return MapUtils.<K, V>getString(this.get(), field, defaultVal);
    }

    default <T> T getOrDefault(K field, Class<T> clazz, T defaultVal) {
        return MapUtils.<K, V, T>get(this.get(), field, clazz, defaultVal);
    }

    default <T> List<T> get(K field, Class<T> clazz) {
        return MapUtils.<K, V, T>getList(this.get(), field, clazz);
    }

    default Map<K, V> sub(List<K> fieldList) {
        return MapUtils.<K, V>sub(this.get(), fieldList);
    }

    default Map<K, V> sub(K... fields) {
        return MapUtils.<K, V>sub(this.get(), Lists.newArrayList(fields));
    }

    default void set(K field, V val) {
        if (this.isNull())
            this.set(Maps.newHashMap());
        this.get().put(field, val);
    }

    default V coalesce(V... val) {
        if (val == null || val.length == 0)
            return null;
        for (V v : val) {
            if (v != null)
                return v;
        }
        return null;
    }

    default void setNonNull(K field, V... val) {
        if (val == null || val.length == 0)
            return;
        for (V v : val) {
            if (v != null) {
                this.set(field, v);
                return;
            }
        }
    }

    default void resetNonNull(K field, V... val) {
        if (val == null || val.length == 0)
            return;
        if (this.hasValue(field))
            return;
        this.setNonNull(field, val);
    }

    default void resetNonNull(Map<K, V> valueMap) {
        if (CollectionUtils.isEmpty(valueMap))
            return;
        for (K k: valueMap.keySet())
            this.resetNonNull(k, valueMap.get(k));
    }

    default void setNotEmpty(K field, V... val) {
        if (val == null || val.length == 0)
            return;
        for (V v : val) {
            if (!StringUtils.isEmpty(v)) {
                this.set(field, v);
                return;
            }
        }
    }

    default void resetNotEmpty(K field, V... val) {
        if (val == null || val.length == 0)
            return;
        if (this.hasValue(field))
            return;
        this.setNotEmpty(field, val);
    }

    default void resetNotEmpty(Map<K, V> valueMap) {
        if (CollectionUtils.isEmpty(valueMap))
            return;
        for (K k: valueMap.keySet())
            this.resetNotEmpty(k, valueMap.get(k));
    }

    default <T> void append(K field, T val) {
        V object = this.getOrDefault(field, null);
        List<T> list = Lists.newArrayList();
        if (object instanceof List)
            list = (List<T>) object;
        list.add(val);
        this.set(field, (V) list);
    }

    default <T> void append(K field, T... val) {
        if (val == null || val.length == 0)
            return;
        V object = this.getOrDefault(field, null);
        List<T> list = Lists.newArrayList();
        if (object instanceof List)
            list = (List<T>) object;
        list.addAll(Arrays.asList(val));
        this.set(field, (V) list);
    }

    default <T> void appendNonNull(K field, T... val) {
        if (val == null || val.length == 0)
            return;
        V object = this.getOrDefault(field, null);
        List<T> list = Lists.newArrayList();
        if (object instanceof List)
            list = (List<T>) object;
        for (T v : val) {
            if (v != null)
                list.add(v);
        }
        if (!CollectionUtils.isEmpty(list))
            this.set(field, (V) list);
    }

    default <T> void appendNotEmpty(K field, T... val) {
        if (val == null || val.length == 0)
            return;
        V object = this.getOrDefault(field, null);
        List<T> list = Lists.newArrayList();
        if (object instanceof List)
            list = (List<T>) object;
        for (T v : val) {
            if (!StringUtils.isEmpty(v))
                list.add(v);
        }
        if (!CollectionUtils.isEmpty(list))
            this.set(field, (V) list);
    }

    default <T> T indexAt(K field, Integer idx, T defaultVal) {
        V object = this.getOrDefault(field, null);
        if (object != null && object instanceof List) {
            List<T> list = (List<T>) object;
            if (idx == null || idx < 0)
                idx = 0;
            if (list.size() > idx)
                return list.get(idx);
        }
        return defaultVal;
    }

    default boolean exist(K field) {
        return (!this.isEmpty()) && this.get().containsKey(field);
    }

    default <T> T[] array(K field, Class<T> clazz) {
        return MapUtils.<K, V, T>getArray(this.get(), field, clazz);
    }

    default String[] string(K field) {
        List<String> list = this.get(field, String.class);
        String[] arr = new String[list.size()];
        return list.toArray(arr);
    }

    default Integer[] integer(K field) {
        List<Integer> list = this.get(field, Integer.class);
        Integer[] arr = new Integer[list.size()];
        return list.toArray(arr);
    }
}
