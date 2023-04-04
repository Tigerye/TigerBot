package com.tigerobo.x.pai.biz.utils;

import com.tigerobo.x.pai.api.entity.BaseId;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListConvertUtil {


    public static <T extends BaseId> Map<Integer, T> list2map(List<T> list) {
        if (CollectionUtils.isEmpty(list)) {
            return new HashMap<>();
        }
        Map<Integer, T> map = new HashMap<>();
        for (T t : list) {
            map.put(t.getId(), t);
        }
        return map;
    }
}
