package com.tigerobo.x.pai.biz.biz.process;

import com.google.common.collect.Lists;
import com.tigerobo.x.pai.api.entity.IndexItem;
import com.tigerobo.x.pai.api.entity.IndexType;
import com.tigerobo.x.pai.api.utils.MapUtils;
import com.tigerobo.x.pai.dal.redis.RedisTmpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description: 业务模块-处理器-任务处理器
 * @modified By:
 * @version: $
 */
@Slf4j
@Component
//@Lazy
public class TaskProcessor  {
    @Autowired
    protected RedisTmpUtils redisTmpUtils;

    public List<IndexItem> indices( String uuid) {
        List<IndexItem> indexItemList = Lists.newArrayList();
        if (!StringUtils.isEmpty(uuid)) {
//            Entity.Type type = Entity.Type.TASK;
//            Entity.Type type = value.getType();
//            String uuid = value.getUuid();
//            Map<Object, Object> statMap = redisUtils.hmget(type.getModule().getName() + ":" + type.toString().toLowerCase() + ":stat:" + uuid);

            int viewNum = getViewNum(uuid);
            IndexItem indexItem1 = IndexItem.builder().uid("task-view-total").name("任务浏览总量").type(IndexType.TASK_VIEW_TOTAL).build();
            indexItem1.set("count", viewNum);
            indexItemList.add(indexItem1);
        }
        return indexItemList;
    }

    public List<IndexItem> buildIndex(int viewNum){
        List<IndexItem> indexItemList = Lists.newArrayList();

        IndexItem indexItem1 = IndexItem.builder().uid("task-view-total").name("任务浏览总量").type(IndexType.TASK_VIEW_TOTAL).build();
        indexItem1.set("count", viewNum);
        indexItemList.add(indexItem1);
        return indexItemList;
    }


    public int getViewNum(String uuid){
        String key = "biz:task:stat:"+ uuid;
        Map<Object, Object> hmget = redisTmpUtils.hmget(key);
        return MapUtils.get(hmget, "view", Integer.class, 0);
    }
}
