package com.tigerobo.x.pai.biz.data.es;

import com.alibaba.fastjson.JSON;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.tigerobo.x.pai.api.constants.EsConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.bulk.*;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class EsPaiClient {
    private String host = EsConstant.ES_HOST;
//    private String host = "10.0.19.158";
    Integer port = 9200;
//Integer port = 8082;
    protected RestHighLevelClient client;
    protected BulkProcessor bulkProcessor;


    protected RestHighLevelClient preClient;
    protected BulkProcessor preBulkProcessor;
    private ScheduledExecutorService scheduledExecutorService;
    private static final int ES_BULK_FLUSH = 1000;
    private static final int ES_BULK_SIZE = 5;
    private static final int ES_BULK_CONCURRENT = 10;

    @PostConstruct
    public void initClient() {

        final ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat(getNameFormat())
                .setDaemon(false)
                .build();
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(threadFactory);

        refreshClient();
        scheduledExecutorService.scheduleWithFixedDelay(this::refreshClient, 1, 1, TimeUnit.HOURS);
    }

    protected String getNameFormat(){
        return "EsClientBase-%d";
    }

    protected synchronized void refreshClient(){
        log.info(getClass()+",refreshClient:start");
        try {
            String hostIps = getHosts();
            HttpHost[] hosts = Arrays.stream(hostIps.replaceAll(" ", "").split("[,，]"))
                    .parallel().map(hostIp -> {
                        return new HttpHost(hostIp, port, "http");
                    }).toArray(HttpHost[]::new);
            if (hosts == null) {
                return;
            }
            RestHighLevelClient tpmClient = new RestHighLevelClient(RestClient.builder(hosts));
            BulkProcessor tmpBulkProcessor = BulkProcessor.builder((request, bulkListener) -> {
                        try {
                            tpmClient.bulkAsync(request,RequestOptions.DEFAULT,bulkListener);
//                            tpmClient.bulkAsync(request, RequestOptions.DEFAULT, bulkListener);
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                            log.error("Error happened during we async bulk handle info");
                        }
                    },
                    getBPListener())
                    .setBulkActions(getBulkFlush())
                    .setBulkSize(new ByteSizeValue(ES_BULK_SIZE, ByteSizeUnit.MB))
                    .setFlushInterval(TimeValue.timeValueSeconds(getFlushInterval()))
                    .setConcurrentRequests(getConcurrentSize())
                    .setBackoffPolicy(BackoffPolicy.constantBackoff(TimeValue.timeValueSeconds(2L), getRetryTime()))
                    .build();


            RestHighLevelClient currentClient = client;

            BulkProcessor currentBulkProcessor = bulkProcessor;
            client = tpmClient;
            bulkProcessor = tmpBulkProcessor;
            closeClient(preClient,preBulkProcessor);
            preClient = currentClient;
            preBulkProcessor = currentBulkProcessor;
        } catch (Exception ex) {
            log.error("EsClientBase:init", ex);
        }
    }

    protected int getBulkFlush(){
        return ES_BULK_FLUSH;
    }

    protected long getFlushInterval(){
        return 5L;
    }

    protected int getConcurrentSize(){
        return ES_BULK_CONCURRENT;
    }

    protected int getRetryTime(){
        return 0;
    }

    protected String getHosts(){
        return host;
    }

    public RestHighLevelClient getClient() {
        if (client == null) {
            synchronized (this) {
                if (client == null) {
                    initClient();
                }
            }
        }
        return client;
    }

    public BulkProcessor getBulkProcesor() {
        return bulkProcessor;
    }

    /* 销毁client */
    @PreDestroy
    public void close() {
        closeClient(client, bulkProcessor);
        closeClient(preClient, preBulkProcessor);
    }

    private void closeClient(RestHighLevelClient currentClient, BulkProcessor tmpBulkProcessor) {
        try {
            if (tmpBulkProcessor != null) {
                tmpBulkProcessor.awaitClose(30L, TimeUnit.SECONDS);
            }
            if (currentClient != null) {
                currentClient.close();
            }
        } catch (Exception e) {
            log.error("RestHighLevelClient close error .", e);
        }
    }


    private BulkProcessor.Listener getBPListener() {
        return new BulkProcessor.Listener() {
            @Override
            public void beforeBulk(long executionId, BulkRequest request) {
//                log.info("Start to handle bulk commit executionId:[{}] for {} requests", executionId, request.numberOfActions());
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
//                log.info("Finished handling bulk commit executionId:[{}] for {} requests", executionId, request.numberOfActions());

                if (response.hasFailures()) {
                    AtomicInteger count = new AtomicInteger();
                    response.spliterator().forEachRemaining(x -> {
                        if (x.isFailed()) {
                            int itemId = x.getItemId();
                            DocWriteRequest<?> itemReq = request.requests().get(itemId);
                            Map<String, Object> source = null;
                            boolean isIndex = false;
                            if (itemReq instanceof IndexRequest) {
                                isIndex = true;
                                IndexRequest indexRequest = (IndexRequest) itemReq;
                                source = indexRequest.sourceAsMap();
                            } else if (itemReq instanceof UpdateRequest) {
                                UpdateRequest dateReq = (UpdateRequest) itemReq;
                                IndexRequest doc = dateReq.doc();
                                if (doc != null) {
                                    source = doc.sourceAsMap();
                                }
                            }
                            BulkItemResponse.Failure failure = x.getFailure();
                            String failureMessage = failure.getCause().getMessage();
                            if (failureMessage!=null&&failureMessage.contains(" document already exists")){
                                log.warn("{},document already exists",x.getId());
                            }else {
                                String msg = String.format(
                                        "isIndex:[%s],Index:[%s], type:[%s], id:[%s], itemId:[%s], opt:[%s], version:[%s],sourceData:[%s],errMsg:%s"
                                        ,isIndex, x.getIndex()
                                        , x.getType()
                                        , x.getId()
                                        , x.getItemId()
                                        , x.getOpType().getLowercase()
                                        , x.getVersion()
                                        , JSON.toJSONString(source)
                                        , failureMessage
                                );
                                log.error("Bulk executionId:[{}] has error messages:\t{}", executionId, msg);
                            }

                            count.incrementAndGet();
                        }
                    });
//                    log.info("Finished handling bulk commit executionId:[{}] for {} requests with {} errors", executionId, request.numberOfActions(), count.intValue());
                }
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                failure.printStackTrace();
                Class clazz = failure.getClass();
                log.error("Bulk [{}] finished with [{}] requests of error:{}, {}, {}:-[{}]", executionId
                        , request.numberOfActions()
                        , clazz.getName()
                        , clazz.getSimpleName()
                        , clazz.getTypeName()
                        , clazz.getCanonicalName()
                        , failure);

                for (DocWriteRequest<?> x : request.requests()) {
                    Map source = null;
                    if (x instanceof IndexRequest) {
                        source = ((IndexRequest) x).sourceAsMap();
                    } else if (x instanceof UpdateRequest) {
                        IndexRequest updateRequest = ((UpdateRequest) x).doc();
                        if (updateRequest != null) {
                            source = updateRequest.sourceAsMap();
                        }
                    }
                    log.error("Failure to handle index:[{}], type:[{}], op:[{}],data:[{}]", x.index(), x.type(), x.getClass().getName(), JSON.toJSONString(source));
                    break;
                }
            }
        };
    }

}
