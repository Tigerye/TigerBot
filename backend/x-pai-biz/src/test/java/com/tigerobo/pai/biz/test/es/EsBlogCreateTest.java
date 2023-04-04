package com.tigerobo.pai.biz.test.es;

import com.alibaba.fastjson.JSON;
import com.tigerobo.x.pai.biz.data.es.EsPaiClient;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CloseIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.client.indices.PutIndexTemplateRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EsBlogCreateTest {


//    @Autowired
    private EsPaiClient esPaiClient = new EsPaiClient();
    RestHighLevelClient client;
    @Before
    public void init(){
        client = esPaiClient.getClient();
    }



    @Test
    public void listIndexListTest()throws Exception{
        GetIndexRequest getIndexRequest = new GetIndexRequest("*test*");

        final GetIndexResponse response = client.indices().get(getIndexRequest, RequestOptions.DEFAULT);

        final String[] indices = response.getIndices();
        System.out.println(JSON.toJSONString(indices));


        final String[] strings = Arrays.stream(indices).filter(index -> {

            if (index.contains("shj") || index.contains("rgzn") || index.contains("pdf-image") || index.contains("zrc")) {
                return false;
            }
            return true;
        }).toArray(String[]::new);

        System.out.println("index name start:=========");
        for (String string : strings) {
            System.out.println(string);

        }


        final List<String> completeList1 = getCompleteList1();

        List<String> list = new ArrayList<>();
        for (String string : strings) {
            boolean contain = false;
            for (String s : completeList1) {
                if (string.contains(s)){
                    contain = true;
                    break;
                }
            }
            if (!contain){
                list.add(string);
            }
        }


        final String[] objects = list.toArray(new String[0]);

        Arrays.stream(strings).parallel().forEach(
                delIndex->{
                    DeleteIndexRequest request = new DeleteIndexRequest();
            request.indices(delIndex);

                    try {
                        client.indices().delete(request,RequestOptions.DEFAULT);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );
//
//        for (String delIndex : strings) {
//
//        }




        System.out.println("size="+objects.length);
        System.out.println("size="+strings.length);
//
//        CloseIndexRequest closeIndexRequest = new CloseIndexRequest(strings);
//        client.indices().close(closeIndexRequest,RequestOptions.DEFAULT);


//        client.indices().close()
    }


    private List<String> getCompleteList1(){
        return Arrays.asList("tisp-complete-index-13",
                "tisp-complete-index-24",
                "tisp-complete-index-25",
                "tisp-complete-index-26",
                "tisp-complete-index-27",
                "tisp-complete-index-29",
                "tisp-complete-index-30",
                "tisp-complete-index-31",
                "tisp-complete-index-32",
                "tisp-complete-index-34",
                "tisp-complete-index-36",
                "tisp-complete-index-40",
                "tisp-complete-index-45",
                "tisp-complete-index-48",
                "tisp-complete-index-50",
                "tisp-complete-index-61",
                "tisp-complete-index-62",
                "tisp-complete-index-63",
                "tisp-complete-index-70",
                "tisp-complete-index-71",
                "tisp-complete-index-74",
                "tisp-complete-index-87",
                "tisp-complete-index-89",
                "tisp-complete-index-90",
                "tisp-complete-index-91",
                "tisp-complete-index-92",
                "tisp-complete-index-93",
                "tisp-complete-index-94",
                "tisp-complete-index-95",
                "tisp-complete-index-96",
                "tisp-complete-index-48",
                "tisp-complete-index-76",
                "tisp-complete-index-77");
    }


    @Test
    public void delTest()throws Exception{

        DeleteIndexRequest request = new DeleteIndexRequest();
        request.indices("yinhao_v20220820");

        client.indices().delete(request,RequestOptions.DEFAULT);

    }



    @Test
    public void closeTest()throws Exception{

        String text = "xinyou12_v20220831";
        CloseIndexRequest closeIndexRequest = new CloseIndexRequest(text);
        client.indices().close(closeIndexRequest,RequestOptions.DEFAULT);

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


        boolean prod = false;
        if (prod){
            PutIndexTemplateRequest request = new PutIndexTemplateRequest(
                    "pai_blog_view_template"
            );
            request.alias(new Alias("pai_blog_view"));
            request.patterns(Arrays.asList("pai_blog_view_2*"));
            request.settings(settings);
            request.mapping(getxContentBuilder());

            client.indices().putTemplate(request,RequestOptions.DEFAULT);
        }else {
            PutIndexTemplateRequest request = new PutIndexTemplateRequest(
                    "test_pai_blog_view_template"
            );
            request.alias(new Alias("test_pai_blog_view"));
            request.patterns(Arrays.asList("test_pai_blog_view_2*"));
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
                .startObject("blogId").field("type","integer").endObject()
                .startObject("ip").field("type", "keyword").endObject()
                .startObject("createTime").field("type","date").field("format","yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis").endObject()
                .endObject()
                .endObject()
                .endObject();
        return mapping;
    }



}
