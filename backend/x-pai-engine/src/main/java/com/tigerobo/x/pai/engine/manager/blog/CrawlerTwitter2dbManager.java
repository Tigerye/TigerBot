package com.tigerobo.x.pai.engine.manager.blog;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tigerobo.x.pai.api.crawler.CrawlerTwitterBlog;
import com.tigerobo.x.pai.biz.biz.blog.BlogTwitterConvert;
import com.tigerobo.x.pai.biz.biz.blog.CrawlerTwitter2dbService;
import com.tigerobo.x.pai.biz.utils.RestUtil;
import com.tigerobo.x.pai.dal.biz.entity.blog.BlogTwitterCrawlerPo;
import com.tigerobo.x.pai.dal.biz.mapper.blog.BlogQueryMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CrawlerTwitter2dbManager {

    @Value("${pai.blog.crawler.twitter.url}")
    private String blogTwitterUrl  = "http://10.0.19.103:8002/export/v1/roll";

//    @Value("${pai.blog.crawler.twitter.id.url}")
    private String blogIdTwitterUrl  = "http://10.0.19.103:8002/export/v1/qs";

    @Autowired
    private CrawlerTwitter2dbService crawlerTwitter2dbService;

    @Autowired
    private BlogQueryMapper blogQueryMapper;
    @Value("${pai.task.blog.open:true}")
    boolean blogSwitch;

    public void dealCrawlerTwitterData(){

        Integer maxThirdId = blogQueryMapper.getTwitterMaxThirdId();
        if (maxThirdId == null){
            maxThirdId = 0;
        }
        dealCrawlerTwitterData(maxThirdId,false);
    }

    public void dealCrawlerTwitterData(Integer maxThirdId,boolean test) {
        CrawlerTwitterBlog twitterData = getTwitterData(maxThirdId);

        int i = 0;
        while (!CollectionUtils.isEmpty(twitterData.getList())){
            if (!blogSwitch){
                log.warn("blogSwitch false");
                return;
            }
            if (i>20){
                break;
            }
            i++;
//            log.info("twitter,i={}",i++);
            doProcess(twitterData,test);
            Integer maxId = twitterData.getMaxId();
            if (maxId==null){
                break;
            }
            twitterData = getTwitterData(maxId);
        }
    }

    private void doProcess(CrawlerTwitterBlog twitterData,boolean test){
        List<BlogTwitterCrawlerPo> pos = BlogTwitterConvert.crawler2po(twitterData);
        if (CollectionUtils.isEmpty(pos)){
            return;
        }
        crawlerTwitter2dbService.crawler2db(pos,test);
    }

    public void processByThirdId(Integer thirdId,boolean test){
        List<CrawlerTwitterBlog.CrawlerTwitterBlogItem> items = getTwitterDataById(thirdId);

        if (CollectionUtils.isEmpty(items)){
            return;
        }
        List<BlogTwitterCrawlerPo> pos = items.stream().map(item -> BlogTwitterConvert.crawler2po(item)).collect(Collectors.toList());
        crawlerTwitter2dbService.crawler2db(pos,test);
    }

    private CrawlerTwitterBlog getTwitterData(Integer thirdId) {
        return getTwitterData(thirdId,100);
    }

    private CrawlerTwitterBlog getTwitterData(Integer thirdId,int size) {
        if (thirdId == null){
            return null;
        }
        Map<String, Object> params = new HashMap<>();

        params.put("type", "news.aitwitter.932");
        params.put("app", "c01");
        params.put("sign", "e1fe4fc470b694994eba0f691c56a033");
        params.put("offset", thirdId);
        params.put("size", size);
        String response = RestUtil.get(blogTwitterUrl, params);

        if (StringUtils.isEmpty(response)) {
            return null;
        }
        JSONObject jsonObject = JSON.parseObject(response);
        return jsonObject.getObject("data", CrawlerTwitterBlog.class);
    }


    private List<CrawlerTwitterBlog.CrawlerTwitterBlogItem> getTwitterDataById(Integer thirdId) {
        if (thirdId == null){
            return null;
        }
        Map<String, Object> params = new HashMap<>();

        params.put("type", "news.aitwitter.qs.940");
        params.put("app", "c01");
        params.put("sign", "e1fe4fc470b694994eba0f691c56a033");
        params.put("wd", thirdId);
        String response = RestUtil.get(blogIdTwitterUrl, params);

        if (StringUtils.isEmpty(response)) {
            return null;
        }
        JSONObject jsonObject = JSON.parseObject(response);


        JSONArray data = jsonObject.getJSONArray("data");
        if (data == null){
            return null;
        }
        return JSONArray.parseArray(JSON.toJSONString(data),CrawlerTwitterBlog.CrawlerTwitterBlogItem.class);

    }
}
