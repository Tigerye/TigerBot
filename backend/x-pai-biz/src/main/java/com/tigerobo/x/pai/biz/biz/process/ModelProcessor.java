package com.tigerobo.x.pai.biz.biz.process;

import com.google.common.collect.Lists;
import com.tigerobo.x.pai.api.entity.Entity;
import com.tigerobo.x.pai.api.entity.IndexItem;
import com.tigerobo.x.pai.api.entity.IndexType;
import com.tigerobo.x.pai.api.utils.MapUtils;
import com.tigerobo.x.pai.dal.redis.RedisTmpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ModelProcessor {
    @Autowired
    protected RedisTmpUtils redisTmpUtils;

    public List<IndexItem> getIndices(String modelUuid){


        if (StringUtils.isEmpty(modelUuid)){
            return new ArrayList<>();
        }
        final Integer view = getModelCount(modelUuid);


        return build(view);
    }

    public int getModelCount(String modelUuid) {
        Entity.Type modelType = Entity.Type.MODEL;
        Map<Object, Object> statMap = redisTmpUtils.hmget(modelType.getModule().getName() + ":" + modelType.toString().toLowerCase() + ":stat:" + modelUuid);

        final String view = MapUtils.getString(statMap, "view", null);

        if (view == null){
            return 0;
        }
        return Integer.parseInt(view);
    }

    public List<IndexItem> build( Object view) {
        List<IndexItem> indexItemList = Lists.newArrayList();
        IndexItem indexItem1 = IndexItem.builder().uid("model-view-total").name("模型浏览总量").type(IndexType.MODEL_VIEW_TOTAL).build();
        indexItem1.set("count", view);
//        IndexItem indexItem2 = IndexItem.builder().uid("model-download-total").name("模型下载总量").type(IndexType.MODEL_DOWNLOAD_TOTAL).build();
//        indexItem2.set("count", MapUtils.get(statMap, "download", 0));
        indexItemList.add(indexItem1);
//        indexItemList.add(indexItem2);
        return indexItemList;
    }

}
