package com.tigerobo.x.pai.biz.biz.process;

import com.aliyun.oss.model.OSSObjectSummary;
import com.google.common.collect.Lists;
import com.tigerobo.x.pai.biz.oss.OSSHome;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Random;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description:
 * @modified By:
 * @version: $
 */
@Slf4j
@Component
//@Lazy
public class ImageFactory {
    @Getter
    private volatile List<String> imagePool;
    private volatile int poolSize = 1;
    private final static Random random = new Random(1);

    @Autowired
    private OSSHome ossHome;
    @Value("${task.support.win:false}")
    boolean supportWin;

//    @PostConstruct
//    @Scheduled(fixedDelay = 3600000L)
    public void init() {
        if (!supportWin){
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("windows")){
//                return;
            }
        }
        this.imagePool = Lists.newArrayList();
        List<OSSObjectSummary> ossObjectSummaryList = this.ossHome.getList("biz/demand/image/default/Frame");
        for (OSSObjectSummary ossObjectSummary : ossObjectSummaryList) {
            String url = this.ossHome.getBaseUrl(ossObjectSummary.getKey());
            this.imagePool.add(url);
        }
        this.poolSize = this.imagePool.size();
        log.info("load image: {}", this.imagePool.size());
    }

    public String getImage() {
        if (this.poolSize == 0 || CollectionUtils.isEmpty(this.imagePool)){
            init();

        }
        if (this.poolSize == 0 || CollectionUtils.isEmpty(this.imagePool)){
            return null;
        }
        long time = System.currentTimeMillis()%1_000_000;
        int index = (int)(time % this.poolSize);
        return this.imagePool.get(index);
    }
}