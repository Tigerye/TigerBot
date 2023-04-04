package com.tigerobo.x.pai.engine.manager.blog;

import com.tigerobo.x.pai.api.crawler.CrawlerBlog;
import com.tigerobo.x.pai.biz.biz.blog.BlogCrawlerService;
import com.tigerobo.x.pai.biz.converter.BlogCrawlerConvert;
import com.tigerobo.x.pai.biz.crawler.CrawlerService;
import com.tigerobo.x.pai.dal.biz.dao.blog.BlogCrawlerDao;
import com.tigerobo.x.pai.dal.biz.dao.blog.BlogSearchDao;
import com.tigerobo.x.pai.dal.biz.entity.blog.BlogCrawlerPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static com.tigerobo.x.pai.biz.utils.HtmlContentUtil.cleanNBSP;

@Slf4j
@Component
public class CrawlerBlogManager {

    @Autowired
    private CrawlerService crawlerService;

    @Autowired
    private BlogCrawlerService blogCrawlerService;
    @Autowired
    private BlogCrawlerDao blogCrawlerDao;
//    @Autowired
//    private BlogInfoDao blogInfoDao;
    @Autowired
    private BlogSearchDao blogSearchDao;
    @Value("${pai.task.blog.open:true}")
    boolean blogSwitch;

    public void saveCrawler2db(){

        Integer maxThirdId = blogSearchDao.getMaxThirdId();
        if(maxThirdId == null){
            maxThirdId =0;
        }
        saveCrawler2db(maxThirdId);
    }
    public void saveCrawler2db(int startOffSet) {

        CrawlerBlog crawlerData = crawlerService.getCrawlerData(startOffSet);
        while (crawlerData != null && !CollectionUtils.isEmpty(crawlerData.getList())) {

            if (!blogSwitch){
                log.warn("blogSwitch false");
                return;
            }
            log.info("crawler-blog-size:{}",crawlerData.getList().size());
            clean(crawlerData);
            processCrawlerDataDb(crawlerData);

            Integer maxId = crawlerData.getMaxId();
            if (maxId != null) {
                crawlerData = crawlerService.getCrawlerData(maxId);
            }
        }
    }

    private void clean(CrawlerBlog crawlerBlog){

        if (crawlerBlog == null){
            return;
        }
        List<CrawlerBlog.CrawlerBlogItem> list = crawlerBlog.getList();

        if (CollectionUtils.isEmpty(list)){
            return;
        }
        for (CrawlerBlog.CrawlerBlogItem item : list) {
            item.setAuthor(cleanNBSP(item.getAuthor()));
            item.setSrc_name(cleanNBSP(item.getSrc_name()));
            item.setTitle(cleanNBSP(item.getTitle()));
            item.setAuthor(cleanNBSP(item.getAuthor()));

            Integer src_id = item.getSrc_id();
//            Integer siteId = pubCrawlerSiteRelDao.getSiteIdBySrcId(src_id);
//            item.setSiteId(siteId);
        }

    }

    public void crawler2blog(boolean test) {
        List<BlogCrawlerPo> waitDealList;
        if (test){
            waitDealList = blogCrawlerDao.getTestWaitDealList();
        }else {
            waitDealList = blogCrawlerDao.getWaitDealList();
        }

        if (CollectionUtils.isEmpty(waitDealList)){
            return;
        }

        for (BlogCrawlerPo blogCrawlerPo : waitDealList) {
            try {
                blogCrawlerService.save2blog(blogCrawlerPo);
            }catch (IllegalArgumentException ex){
                log.error("thirdId:{}",blogCrawlerPo.getThirdId(),ex);
                try {
                    blogCrawlerService.updateFail(blogCrawlerPo.getId(), ex.getMessage());
                }catch (Exception subEx){
                    log.error("",subEx);
                }
            }catch (Exception ex){
                log.error("thirdId:{}",blogCrawlerPo.getThirdId(),ex);
                try {
                    blogCrawlerService.updateFail(blogCrawlerPo.getId(),"服务异常");
                }catch (Exception subEx){
                    log.error("",subEx);
                }
            }
        }

    }

    public void recognizeCategory(boolean test) {
        List<BlogCrawlerPo> waitDealList;
        if (test){
            waitDealList = blogCrawlerDao.getWaitDealCategoryList();
        }else {
            waitDealList = blogCrawlerDao.getWaitDealCategoryList();
        }

        if (CollectionUtils.isEmpty(waitDealList)){
            return;
        }

        for (BlogCrawlerPo blogCrawlerPo : waitDealList) {
            try {
                blogCrawlerService.doUpdateBlogCategory(blogCrawlerPo);
            }catch (IllegalArgumentException ex){
                log.error("category-thirdId:{}",blogCrawlerPo.getThirdId(),ex);
                try {
                    blogCrawlerService.updateCategoryFail(blogCrawlerPo.getId(), ex.getMessage());
                }catch (Exception subEx){
                    log.error("",subEx);
                }
            }catch (Exception ex){
                log.error("category - thirdId:{}",blogCrawlerPo.getThirdId(),ex);
                try {
                    blogCrawlerService.updateCategoryFail(blogCrawlerPo.getId(),"标签-服务异常");
                }catch (Exception subEx){
                    log.error("",subEx);
                }
            }
        }

    }

    private void processCrawlerDataDb(CrawlerBlog crawlerData) {
        if (crawlerData == null) {
            return;
        }
        List<BlogCrawlerPo> pos = BlogCrawlerConvert.convert2db(crawlerData);
        if (!CollectionUtils.isEmpty(pos)) {
            blogCrawlerService.add2CrawlerBlogs(pos);
        }
    }


}
