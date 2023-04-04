package com.tigerobo.x.pai.engine.manager.test;

import com.tigerobo.pai.common.util.TextFileUtil;
import com.tigerobo.x.pai.biz.biz.blog.Twitter2BlogService;
import com.tigerobo.x.pai.biz.crawler.CrawlerSubscribeService;
import com.tigerobo.x.pai.dal.biz.dao.blog.BlogSearchDao;
import com.tigerobo.x.pai.dal.biz.entity.blog.BlogTwitterCrawlerPo;
import com.tigerobo.x.pai.engine.EngineBaseTest;
import com.tigerobo.x.pai.engine.manager.blog.CrawlerBlogManager;
import com.tigerobo.x.pai.engine.manager.blog.CrawlerTwitter2dbManager;
import com.tigerobo.x.pai.engine.manager.blog.SubscribeManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
public class CrawlerBlogTest extends EngineBaseTest {

    @Autowired
    private CrawlerBlogManager crawlerBlogManager;
    @Autowired
    private BlogSearchDao blogSearchDao;

    @Autowired
    private Twitter2BlogService twitter2BlogService;


    @Autowired
    private CrawlerTwitter2dbManager crawlerTwitter2dbManager;

    @Autowired
    private CrawlerSubscribeService crawlerSubscribeService;

    @Autowired
    private SubscribeManager subscribeManager;



    @Test
    public void crawlerBaseTest(){

//        maxThirdId = 1018041;
        crawlerBlogManager.saveCrawler2db();
    }


    @Test
    public void twitterTest(){

        int maxThirdId = 1017867;
//        maxThirdId = 1018041;
        crawlerTwitter2dbManager.dealCrawlerTwitterData(maxThirdId,true);
    }

    @Test
    public void byThirdTest(){
        String path = "E:\\pai\\blog\\爬虫数据\\twitter-视频-回复中.txt";
        List<Integer> list = TextFileUtil.getIntList(path);

        list.parallelStream().forEach(id->{
            try {
                crawlerTwitter2dbManager.processByThirdId(id, true);
            }catch (Exception ex){
                log.error("id:{}",id,ex);
            }
        });
    }
    @Test
    public void saveTwitter2blogTest(){


        List<BlogTwitterCrawlerPo> waitDealList = twitter2BlogService.getWaitDealList(true);
        if (CollectionUtils.isEmpty(waitDealList)) {
            return;
        }
        for (BlogTwitterCrawlerPo blogTwitterCrawlerPo : waitDealList) {
            try {
                twitter2BlogService.save2blog(blogTwitterCrawlerPo);
            } catch (Exception ex) {
                log.error("thirdId:{}", blogTwitterCrawlerPo.getThirdId(), ex);
            }
        }
    }
    @Test
    public void mangerTest(){
        crawlerBlogManager.saveCrawler2db(1515751);
    }



    @Test
    public void transTest(){
        crawlerBlogManager.crawler2blog(false);
    }

    @Test
    public void categoryTest(){
        crawlerBlogManager.recognizeCategory(true);
    }

    @Test
    public void taskTest(){
        try {
            Integer maxThirdId = blogSearchDao.getMaxThirdId();
            if (maxThirdId == null){
                maxThirdId =0;
            }
            crawlerBlogManager.saveCrawler2db(maxThirdId);
        }catch (Exception ex){
            log.error("CrawlerBlogTask add",ex);
        }
        try {
//            crawlerBlogManager.dealTranslate();
        }catch (Exception ex){
            log.error("CrawlerBlogTask translate",ex);
        }
    }

    @Test
    public void subscribeTest(){


        String url = "https://twitter.com/MichaelEPorter";

        Integer srcId = crawlerSubscribeService.subscribe(url);

        System.out.println(srcId);

    }

    @Test
    public void subscribeManagerTest(){
        subscribeManager.run(true);
    }


}
