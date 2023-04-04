package com.tigerobo.pai.biz.test.es;

import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.enums.ModelCallTypeEnum;
import com.tigerobo.x.pai.api.es.EsModelCall;
import com.tigerobo.x.pai.api.es.EsUserAccess;
import com.tigerobo.x.pai.biz.data.es.EsPaiClient;
import com.tigerobo.x.pai.biz.data.es.EsService;
import com.tigerobo.x.pai.biz.data.es.EsUserAccessService;
import com.tigerobo.x.pai.biz.utils.TimeUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class EsServiceTest extends BaseTest {

    @Autowired
    private EsService esService;

    @Autowired
    private EsPaiClient esPaiClient;

    @Autowired
    private EsUserAccessService esUserAccessService;

    @Test
    public void countTest()throws Exception{
        Integer count = esUserAccessService.countDay(20211211);
        System.out.println(count);

    }
    @Test
    public void queryTest()throws Exception{

        RestHighLevelClient client = esPaiClient.getClient();

        Date time = new Date();
        String index = esService.getIndex(TimeUtil.getMonthValue(time));

        SearchRequest searchRequest = new SearchRequest(index);

        SearchSourceBuilder builder = new SearchSourceBuilder();

        int day = TimeUtil.getDayValue(time);


        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        boolQuery.filter(QueryBuilders.termQuery("day",day));
        boolQuery.filter(QueryBuilders.termQuery("type",2));

        builder.query(boolQuery);
        builder.size(1);
//        AggregationBuilders.
////        builder.aggregation()
//
//
//        searchRequest.source(builder);


        SearchResponse search = client.search(searchRequest, RequestOptions.DEFAULT);

        System.out.println();
    }

    @Test
    public void addTest(){

        EsModelCall call = new EsModelCall();
        call.setDealTime(2L);
        call.setContent("content");
        call.setModelId("modelId");
        call.setUserId(3);
        call.setDemo(false);
        call.setSource(1);
        call.setType(ModelCallTypeEnum.APP.getType());
        call.setResult("result");
        esService.add(call);
    }


    @Test
    public void sumTest(){

    }
}
