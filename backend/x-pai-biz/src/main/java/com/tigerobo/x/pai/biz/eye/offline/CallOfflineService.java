package com.tigerobo.x.pai.biz.eye.offline;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tigerobo.x.pai.api.constants.EsConstant;
import com.tigerobo.x.pai.api.enums.ModelCallSourceEnum;
import com.tigerobo.x.pai.api.enums.ModelCallTypeEnum;
import com.tigerobo.x.pai.biz.data.es.EsService;
import com.tigerobo.x.pai.biz.utils.RestUtil;
import com.tigerobo.x.pai.biz.utils.TimeUtil;
import com.tigerobo.x.pai.dal.biz.dao.ApiDao;
import com.tigerobo.x.pai.dal.biz.dao.call.ModelCallDao;
import com.tigerobo.x.pai.dal.biz.dao.call.ModelSourceCallDao;
import com.tigerobo.x.pai.dal.biz.entity.ApiDo;
import com.tigerobo.x.pai.dal.biz.entity.call.ModelCallPo;
import com.tigerobo.x.pai.dal.biz.entity.call.ModelSourceCallPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CallOfflineService {

    @Autowired
    private ModelCallDao modelCallDao;

    @Autowired
    private EsService esService;


    @Autowired
    private ModelSourceCallDao modelSourceCallDao;
    @Autowired
    private ApiDao apiDao;


    public void refreshNum() {
        int dayValue = TimeUtil.getDayValue(new Date());
        refreshNum(dayValue);
    }

    public void refreshSourceNum() {

        int dayValue = TimeUtil.getDayValue(new Date());
        try {
            refreshSourceNum(dayValue);
        }catch (Exception ex){
            log.error("refreshSourceNum,day:{}",dayValue,ex);
        }
    }

    public void refreshSourceNum(int day) {

        for (ModelCallTypeEnum callType : ModelCallTypeEnum.values()) {
            for (ModelCallSourceEnum source : ModelCallSourceEnum.values()) {
//                System.out.println(day + "\t" + callType + "\t" + source);


                List<ModelSourceCallPo> waitDealList = new ArrayList<>();

                if (ModelCallSourceEnum.API_BATCH_EVALUATE == source) {
                    waitDealList = doBatchRefreshSourceNum(day, callType.getType(), source.getType());
                } else {
                    waitDealList = doApiRefreshSourceNum(day, callType.getType(), source.getType());
                }
                List<ModelSourceCallPo> existList = modelSourceCallDao.getByDay(day, source.getType(), callType.getType());
                addOrUpdate(existList,waitDealList);
            }
        }
    }

    private void addOrUpdate(List<ModelSourceCallPo> existList,List<ModelSourceCallPo> waitDealList){

        if (CollectionUtils.isEmpty(waitDealList)){
            return;
        }
        Map<String, ModelSourceCallPo> existMap = buildMap(existList);

        List<ModelSourceCallPo> addList = new ArrayList<>();
        List<ModelSourceCallPo> updateList = new ArrayList<>();

        for (ModelSourceCallPo po : waitDealList) {
            String key = getKey(po);

            ModelSourceCallPo existPo = existMap.get(key);
            if (existPo==null){
                addList.add(po);
            }else {
                if (po.getNum().compareTo(existPo.getNum())>0){
                    po.setId(existPo.getId());
                    updateList.add(po);
                }else {
//                    log.warn("ignore:{}"+JSON.toJSONString(po));
                }
            }
        }

        if (addList.size()>0){
            for (ModelSourceCallPo modelSourceCallPo : addList) {
                modelSourceCallDao.add(modelSourceCallPo);
            }
        }
        if (updateList.size()>0){
            for (ModelSourceCallPo po : updateList) {
                modelSourceCallDao.update(po);
            }
        }

    }

    private Map<String, ModelSourceCallPo> buildMap(List<ModelSourceCallPo> existList) {
        Map<String, ModelSourceCallPo> map = new HashMap<>();

        if (!CollectionUtils.isEmpty(existList)) {
            return existList.stream().collect(Collectors.toMap(po -> getKey(po), v -> v));
        }
        return map;
    }

    private String getKey(ModelSourceCallPo po) {
        return po.getDay() + "_" + po.getModelId() + "_" + po.getUserId() + "_" + po.getSource() + "_" + po.getType();
    }


    public List<ModelSourceCallPo> doApiRefreshSourceNum(int day, int type, int source) {

        Map<String, Object> map = new LinkedHashMap<>();

        String query = "{\"bool\":{\"filter\":[{\"term\":{\"day\":\"" + day + "\"}},{\"term\":{\"type\":" + type + "}},{\"term\":{\"source\":" + source + "}}]}}";
        map.put("query", JSON.parseObject(query));
        map.put("size", 0);

        Map<String, Object> agg = buildAgg();

        map.put("aggregations", agg);

        String index = esService.getIndex(TimeUtil.getMonthValue(day));

        String url = EsConstant.ES_HOST_URL + index + "/_search";
        String post = RestUtil.post(url, map);

        JSONObject jsonObject = JSON.parseObject(post);

        JSONArray jsonArray = jsonObject.getJSONObject("aggregations").getJSONObject("modelAgg").getJSONArray("buckets");

        List<ModelSourceCallPo> targetList = new ArrayList<>();

        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject subJson = jsonArray.getJSONObject(i);

            String modelId = subJson.getString("key");

            JSONArray userArray = subJson.getJSONObject("userAgg").getJSONArray("buckets");

            for (int j = 0; j < userArray.size(); j++) {

                JSONObject userJson = userArray.getJSONObject(j);
                Integer count = userJson.getInteger("doc_count");
                if (count == null || count == 0) {
                    continue;
                }
                Integer userId = userJson.getInteger("key");

                ModelSourceCallPo po = new ModelSourceCallPo();
                po.setDay(day);
                po.setModelId(modelId);
                po.setUserId(userId);
                po.setSource(source);
                po.setType(type);
                po.setNum(count);
                targetList.add(po);
            }
        }
        return targetList;
    }

    public List<ModelSourceCallPo> doBatchRefreshSourceNum(int day, int type, int source) {

        Map<String, Object> map = new LinkedHashMap<>();

        String query = "{\"bool\":{\"filter\":[{\"term\":{\"day\":\"" + day + "\"}},{\"term\":{\"type\":" + type + "}},{\"term\":{\"source\":" + source + "}}]}}";
        map.put("query", JSON.parseObject(query));
        map.put("size", 0);

        Map<String, Object> agg = buildSumAgg();

        map.put("aggregations", agg);

        String index = esService.getIndex(TimeUtil.getMonthValue(day));

        String url = EsConstant.ES_HOST_URL + index + "/_search";
        String post = RestUtil.post(url, map);

        JSONObject jsonObject = JSON.parseObject(post);

        List<ModelSourceCallPo> targetList = new ArrayList<>();
        JSONArray jsonArray = jsonObject.getJSONObject("aggregations").getJSONObject("modelAgg").getJSONArray("buckets");
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject subJson = jsonArray.getJSONObject(i);

            String modelId = subJson.getString("key");

            JSONArray userArray = subJson.getJSONObject("userAgg").getJSONArray("buckets");

            for (int j = 0; j < userArray.size(); j++) {

                JSONObject userJson = userArray.getJSONObject(j);
//                Integer count = userJson.getInteger("doc_count");
                Integer userId = userJson.getInteger("key");
                int batchCount = userJson.getJSONObject("batchSum").getBigDecimal("value").intValue();

                if (batchCount == 0) {
                    continue;
                }
                ModelSourceCallPo po = new ModelSourceCallPo();
                po.setModelId(modelId);
                po.setDay(day);
                po.setUserId(userId);
                po.setSource(source);
                po.setType(type);
                po.setNum(batchCount);
                targetList.add(po);
            }
        }
        return targetList;
    }

    private Map<String, Object> buildSumAgg() {

        String json = "{\"modelAgg\":{\"aggregations\":{\"userAgg\":{\"aggregations\":{\"batchSum\":{\"sum\":{\"field\":\"callNum\"}}},\"terms\":{\"field\":\"userId\",\"size\":5000}}},\"terms\":{\"field\":\"modelId\",\"size\":500}}}";


        return JSON.parseObject(json);
    }


    private Map<String, Object> buildAgg() {

        String json = "{\"modelAgg\":{\"aggregations\":{\"userAgg\":{\"terms\":{\"field\":\"userId\",\"size\":5000}}},\"terms\":{\"field\":\"modelId\",\"size\":5000}}}";


        return JSON.parseObject(json);
    }


    private Map<String, Object> buildTerm(String key, Object value) {
        Map<String, Object> map = new HashMap<>();

        Map<String, Object> subMap = new HashMap<>();
        subMap.put(key, value);
        map.put("term", subMap);
        return map;
    }

    public void refreshNum(int dayValue) {

        for (ApiDo apiDo : apiDao.getBindAmlList()) {

            String amlRelIds = apiDo.getAmlRelIds();
            if (StringUtils.isEmpty(amlRelIds)){
                continue;
            }
            Integer userId = apiDo.getRelUserId();
            if (userId == null||userId<=0){
                continue;
            }
            String[] split = amlRelIds.split(",");

            List<Integer> modelIds = new ArrayList<>();
            for (String s : split) {
                if (s.matches("\\d+")){
                    modelIds.add(Integer.parseInt(s));
                }
            }

            if (modelIds.isEmpty()){
                continue;
            }

            dealModelIds(dayValue, userId, modelIds);
        }

    }

    private void dealModelIds(int dayValue, Integer userId, List<Integer> modelIds) {
        for (Integer modelId : modelIds) {


            String index = esService.getIndex(TimeUtil.getMonthValue(dayValue));
            String json = "{\"query\":{\"bool\":{\"must\":[{\"term\":{\"day\":\"" + dayValue + "\"}},{\"match\":{\"modelId\":\"" + modelId + "\"}},{\"term\":{\"userId\":\"" + userId + "\"}}],\"must_not\":[],\"should\":[]}},\"from\":0,\"size\":0,\"sort\":[],\"aggs\":{}}";

            String url = "http://ms-es-logs-1.aigauss.com:9200/" + index + "/_search";
            JSONObject jsonObject = JSON.parseObject(json);
            Map<String, Object> innerMap = jsonObject.getInnerMap();
            String post = RestUtil.post(url, innerMap);

            JSONObject result = JSON.parseObject(post);


            ModelCallPo existInDb = modelCallDao.getByKey(dayValue, modelId, userId);
            Integer num = result.getJSONObject("hits").getInteger("total");
            if (num == null) {
                continue;
            }

            if (existInDb != null) {
                ModelCallPo update = new ModelCallPo();
                update.setId(existInDb.getId());
                update.setNum(num);
                if (num.equals(existInDb.getNum())){
                    continue;
                }
                modelCallDao.update(update);
            } else {
                ModelCallPo add = new ModelCallPo();
                add.setDay(dayValue);
                add.setUserId(userId);
                add.setModelId(modelId);
                add.setNum(num);
                modelCallDao.add(add);
            }
        }
    }

}
