package com.tigerobo.pai.biz.test.es;

import com.tigerobo.x.pai.biz.data.es.EsPaiClient;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.PutIndexTemplateRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

public class EsUserAccessCreateTest {


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
                    "pai_user_access_template"
            );
            request.alias(new Alias("pai_user_access"));
            request.patterns(Arrays.asList("pai_user_access_2*"));
            request.settings(settings);
            request.mapping(getxContentBuilder());

            client.indices().putTemplate(request,RequestOptions.DEFAULT);
        }else {
            PutIndexTemplateRequest request = new PutIndexTemplateRequest(
                    "test_pai_user_access_template"
            );
            request.alias(new Alias("test_pai_user_access"));
            request.patterns(Arrays.asList("test_pai_user_access_2*"));
            request.settings(settings);
            request.mapping(getxContentBuilder());

            client.indices().putTemplate(request,RequestOptions.DEFAULT);
        }

    }

    private XContentBuilder getxContentBuilder() throws IOException {
        XContentBuilder mapping = XContentFactory.jsonBuilder()
                .startObject().startObject("type")
                .startObject("properties")
                .startObject("id").field("type", "keyword").endObject()
                .startObject("day").field("type","integer").endObject()
                .startObject("userId").field("type","integer").endObject()
                .startObject("ip").field("type", "keyword").endObject()
                .startObject("createTime").field("type","date").field("format","yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis").endObject()
                .endObject()
                .endObject()
                .endObject();
        return mapping;
    }



}
