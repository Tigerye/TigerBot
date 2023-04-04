package com.tigerobo.x.pai.api.utils;

import com.tigerobo.x.pai.api.entity.BaseId;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SortUtil {

   public static  <T extends BaseId> List<T> sortByIdIndex(List<Integer> ids, List<T> list){
       if (CollectionUtils.isEmpty(ids)||CollectionUtils.isEmpty(list)){
           return null;
       }
       Map<Integer,T> map = new HashMap<>();
       for (T t : list) {
           map.put(t.getId(),t);
       }
       return ids.stream().map(map::get).collect(Collectors.toList());
   }
}
