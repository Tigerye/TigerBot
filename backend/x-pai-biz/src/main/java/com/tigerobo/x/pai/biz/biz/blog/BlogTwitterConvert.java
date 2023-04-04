package com.tigerobo.x.pai.biz.biz.blog;

import com.tigerobo.x.pai.api.crawler.CrawlerTwitterBlog;
import com.tigerobo.x.pai.api.enums.BlogSourceFromEnum;
import com.tigerobo.x.pai.dal.biz.entity.blog.BlogInfoPo;
import com.tigerobo.x.pai.dal.biz.entity.blog.BlogTwitterCrawlerPo;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

public class BlogTwitterConvert {


    public static BlogInfoPo po2blog(BlogTwitterCrawlerPo crawlerPo) {

        BlogInfoPo blog = new BlogInfoPo();
        blog.setThirdId(crawlerPo.getThirdId());
        blog.setAuthorName(crawlerPo.getAuthor());
        blog.setSourceFrom(BlogSourceFromEnum.BIG_SHOT.getType());
        blog.setPublishTime(crawlerPo.getPublishTime());
        blog.setSummary(crawlerPo.getContent());
        blog.setContent(crawlerPo.getContent());
        blog.setSourceUrl(crawlerPo.getUrl());
//        blog.setSourceName(crawlerPo.getAuthor());
        return blog;
    }

    public static List<BlogTwitterCrawlerPo> crawler2po(CrawlerTwitterBlog crawler){
        if (crawler == null){
            return null;
        }
        List<CrawlerTwitterBlog.CrawlerTwitterBlogItem> list = crawler.getList();
        if (CollectionUtils.isEmpty(list)){
            return null;
        }
        return list.stream().map(c->crawler2po(c)).collect(Collectors.toList());
    }

    public static BlogTwitterCrawlerPo crawler2po(CrawlerTwitterBlog.CrawlerTwitterBlogItem item){

        BlogTwitterCrawlerPo po = new BlogTwitterCrawlerPo();
        po.setThirdId(item.getId());
        po.setSrcId(item.getSrc_id());
        po.setKeyword(item.getKeyword());
        po.setAuthor(item.getAuthor());
        po.setAuthorLink(item.getAuthor_link());
        po.setContent(item.getContent());
        po.setPublishTime(item.getPublish_time());
        po.setUrl(item.getUrl());
        po.setReplyChain(item.getReply_chain());
        po.setSpecId(item.getSpecId());
        po.setChatId(item.getChat_id());
        return po;
    }
}
