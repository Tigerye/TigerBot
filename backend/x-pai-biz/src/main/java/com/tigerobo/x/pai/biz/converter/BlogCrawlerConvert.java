package com.tigerobo.x.pai.biz.converter;

import com.tigerobo.x.pai.api.crawler.CrawlerBlog;
import com.tigerobo.x.pai.api.enums.BlogSourceFromEnum;
import com.tigerobo.x.pai.dal.biz.entity.blog.BlogCrawlerPo;
import com.tigerobo.x.pai.dal.biz.entity.blog.BlogInfoPo;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

public class BlogCrawlerConvert {



    public static List<BlogInfoPo> convert(CrawlerBlog crawlerBlog) {

        if (crawlerBlog == null) {
            return null;
        }

        List<CrawlerBlog.CrawlerBlogItem> list = crawlerBlog.getList();
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.stream().map(b->convert(b)).collect(Collectors.toList());
    }

    public static BlogInfoPo convert(CrawlerBlog.CrawlerBlogItem item) {

        if (item == null) {
            return null;
        }
        BlogInfoPo po = new BlogInfoPo();

        po.setThirdId(item.getId());
        po.setHeadImg(item.getCover_image());
        po.setPublishTime(item.getPdate());
//        po.setOssUrl(item.getOss_url());
        po.setSourceFrom(BlogSourceFromEnum.SITE.getType());
//        po.setSite(item.getSource());
        po.setSourceUrl(item.getUrl());
        po.setTitle(item.getTitle());
        po.setTranslateTitle(item.getTitle_cn());
        po.setSourceName(item.getSrc_name());
        po.setProcessStatus(1);
        po.setAuthorName(item.getAuthor());
        po.setProcessStatus(1);
        po.setSiteId(item.getSiteId());
        return po;
    }


    public static List<BlogCrawlerPo> convert2db(CrawlerBlog crawlerBlog) {

        if (crawlerBlog == null) {
            return null;
        }

        List<CrawlerBlog.CrawlerBlogItem> list = crawlerBlog.getList();
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.stream().map(b->convert2db(b)).collect(Collectors.toList());
    }

    private static BlogCrawlerPo convert2db(CrawlerBlog.CrawlerBlogItem item) {

        if (item == null) {
            return null;
        }
        BlogCrawlerPo po = new BlogCrawlerPo();

        po.setThirdId(item.getId());

        po.setTitle(item.getTitle());
        po.setTitleCn(item.getTitle_cn());

        po.setPdate(item.getPdate());
        po.setUrl(item.getUrl());
        po.setOssUrl(item.getOss_url());

        po.setCoverImage(item.getCover_image());


        po.setSource(item.getSource());
        po.setThirdCreateTime(item.getCreateTime());
        po.setSrcId(item.getSrc_id());
        po.setSrcName(item.getSrc_name());

        String author = item.getAuthor();
        if (author!=null&&author.length()>150){
            author = author.substring(0,150);
        }
        po.setAuthor(author);
        po.setProcessStatus(1);
        return po;
    }

}
