package com.tigerobo.pai.biz.test.es;

import com.tigerobo.x.pai.biz.data.es.EsPaiClient;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.PutIndexTemplateRequest;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Arrays;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public class EsModelCreateTest {


//    @Autowired
    private EsPaiClient esPaiClient = new EsPaiClient();
    RestHighLevelClient client;
    @Before
    public void init(){
        client = esPaiClient.getClient();
    }


    @Test
    public void templateTest()throws Exception{

        Settings settings = Settings.builder()
                .put("number_of_shards", 3)
                .put("refresh_interval","1m")
                .put("number_of_replicas",1)
                .put("max_result_window",2000000)
                .put("index.merge.scheduler.max_thread_count", 2)
                .build();


        boolean prod = true;
        if (prod){
            PutIndexTemplateRequest request = new PutIndexTemplateRequest(
                    "pai_model_call_template"
            );
            request.alias(new Alias("pai_model_call"));
            request.patterns(Arrays.asList("pai_model_call_2*"));
            request.settings(settings);
            request.mapping(getxContentBuilder());

            client.indices().putTemplate(request,RequestOptions.DEFAULT);
        }else {
            PutIndexTemplateRequest request = new PutIndexTemplateRequest(
                    "test_pai_model_call_template"
            );
            request.alias(new Alias("test_pai_model_call"));
            request.patterns(Arrays.asList("test_pai_model_call_2*"));
            request.settings(settings);
            request.mapping(getxContentBuilder());

            client.indices().putTemplate(request,RequestOptions.DEFAULT);
        }

    }

    private XContentBuilder getxContentBuilder() throws IOException {
        XContentBuilder mapping = XContentFactory.jsonBuilder()
                .startObject().startObject("type")
                .startObject("properties")
                .startObject("id").field("type", "long").endObject()
                .startObject("day").field("type","integer").endObject()
                .startObject("userId").field("type","integer").endObject()
                .startObject("modelId").field("type","keyword").endObject()
                .startObject("ip").field("type", "keyword").endObject()
                .startObject("appId").field("type","keyword").endObject()
                .startObject("source").field("type", "integer").endObject()
                .startObject("content").field("type","text").endObject()
                .startObject("result").field("type","text").field("index","false").endObject()
                .startObject("demo").field("type", "boolean").endObject()
                .startObject("dealTime").field("type", "long").endObject()
                .startObject("createTime").field("type","date").field("format","yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis").endObject()
                .startObject("bizId").field("type","keyword").endObject()
                .endObject()
                .endObject()
                .endObject();
        return mapping;
    }



}
