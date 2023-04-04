package com.tigerobo.x.pai.biz.biz.process;

import com.google.common.collect.Lists;
import com.tigerobo.x.pai.api.entity.*;
import com.tigerobo.x.pai.api.utils.MapUtils;
import com.tigerobo.x.pai.biz.cache.RedisCacheService;
import com.tigerobo.x.pai.biz.serving.ApiCountService;
import com.tigerobo.x.pai.dal.biz.dao.ApiDao;
import com.tigerobo.x.pai.dal.redis.RedisTmpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description: 业务模块-处理器-API处理器
 * @modified By:
 * @version: $
 */
@Slf4j
@Component
//@Lazy
public class ApiProcessor{


    @Autowired
    protected RedisTmpUtils redisTmpUtils;

    @Autowired
    private ApiCountService apiCountService;

    @Autowired
    private ApiDao apiDao;

    public List<IndexItem> indices(String uuid) {

        if (StringUtils.isEmpty(uuid)){
            return null;
        }
        Entity.Type type = Entity.Type.API;
        List<IndexItem> indexItemList = Lists.newArrayList();
        String apiKey = uuid;

        List<String> relAmlIds = apiDao.getRelAmlId(apiKey);

        int amlCount = getAmlCount(relAmlIds);

        final String key = type.getModule().getName() + ":" + type.toString().toLowerCase() + ":stat:" + uuid;
        System.out.println(key);
        Map<Object, Object> statMap = redisTmpUtils.hmget(key);
        IndexItem indexItem3 = IndexItem.builder().uid("api-call-total").name("接口调用总量").type(IndexType.API_CALL_TOTAL).build();
        IndexItem indexItem4 = IndexItem.builder().uid("api-call-by-month").name("接口调用量按月").type(IndexType.API_CALL_BY_MONTH).build();
        Integer call = MapUtils.get(statMap, "call", Integer.class,0);


        if (amlCount>0){
            if (call == null){
                call = 0;
            }
            call += amlCount;
        }
        indexItem3.set("count", call);
        int currYear = Calendar.getInstance().get(Calendar.YEAR);
        int currMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        List<Pair<String, Integer>> list = Lists.newArrayList();
        for (int idx = 11; idx >= 0; idx--) {
            int month = (currMonth - idx - 1 + 1200000) % 12 + 1;
            int year = currMonth > idx ? currYear : currYear - (idx - month) / 12 - 1;

            String format = String.format("%d-%02d", year, month);
            String format2 = String.format("%d年%02d月", year, month);
            IndexItem item = IndexItem.builder().uid(format).name(format2).type(IndexType.API_CALL_BY_MONTH).build();
            String format3 = String.format("call:%d%02d", year, month);
            Object val = MapUtils.get(statMap, format3, 0);
            item.set("count", val);

            Integer count = item.getOrDefault("count", Integer.class, 0);

//
//            if (amlCount>0&&year==2021&&month==11){
//                if (count == null){
//                    count = 0;
//                }
//                count += amlCount;
//            }
            list.add(Pair.create(format, count));

            indexItem4.addIndexItem(item);
        }
        indexItem4.set("count", list);
        indexItemList.add(indexItem3);
        indexItemList.add(indexItem4);
        return indexItemList;
    }

    private int getAmlCount(List<String> relAmlIds) {
        int amlCount =0;
        if (!CollectionUtils.isEmpty(relAmlIds)){
            for (String relAmlId : relAmlIds) {
                amlCount += apiCountService.getAmlCount(String.valueOf(relAmlId));
            }
        }
        return amlCount;
    }

    public List<IndexItem> baseIndices(String uuid,Integer relAmlId) {

        if (StringUtils.isEmpty(uuid)){
            return null;
        }

//        Integer call = doGetTotal(uuid, relAmlId);
//        return buildIndexItems(call);
        return null;
    }

    public List<IndexItem> buildIndexItems(Integer call) {
        IndexItem indexItem3 = IndexItem.builder().uid("api-call-total").name("接口调用总量").type(IndexType.API_CALL_TOTAL).build();

        indexItem3.set("count", call);

        List<IndexItem> indexItemList = Lists.newArrayList();
        indexItemList.add(indexItem3);

        return indexItemList;
    }

    public Integer getTotal(String uuid){

        if (StringUtils.isEmpty(uuid)){
            return 0;
        }
        String apiKey = uuid;

        List<String> relAmlIds = apiDao.getRelAmlId(apiKey);


        return doGetTotal(apiKey, relAmlIds);
    }

    public Integer doGetTotal(String apiKey, String relAmlIdStr) {
        List<String> relAmlIds = org.apache.commons.lang3.StringUtils.isNotBlank(relAmlIdStr)?
                Arrays.asList(relAmlIdStr.split(",")):null;
        return doGetTotal(apiKey,relAmlIds);
    }
    public int doGetTotal(String apiKey, List<String> relAmlIds) {
        int amlCount =getAmlCount(relAmlIds);

        Entity.Type type = Entity.Type.API;
        Module module = type.getModule();
        final String key = module.getName() + ":" + type.toString().toLowerCase() + ":stat:" + apiKey;
        Map<Object, Object> statMap = redisTmpUtils.hmget(key);
        Integer call = MapUtils.get(statMap, "call", Integer.class,0);
        if (amlCount>0){
            if (call == null){
                call = 0;
            }
            call += amlCount;
        }
        if (call == null){
            call =0;
        }
        return call;
    }

}