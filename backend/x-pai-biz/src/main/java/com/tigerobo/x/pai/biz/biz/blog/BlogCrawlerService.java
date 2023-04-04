package com.tigerobo.x.pai.biz.biz.blog;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.base.Preconditions;
import com.tigerobo.x.pai.api.enums.BlogSourceFromEnum;
import com.tigerobo.x.pai.biz.biz.OssService;
import com.tigerobo.x.pai.biz.crawler.CrawlerService;
import com.tigerobo.x.pai.biz.lake.LakeAbstractService;
import com.tigerobo.x.pai.biz.lake.LakeBlogCategoryService;
import com.tigerobo.x.pai.biz.lake.LakeTranslateService;
import com.tigerobo.x.pai.biz.utils.HtmlContentUtil;
import com.tigerobo.x.pai.biz.utils.Md5Util;
import com.tigerobo.x.pai.biz.utils.RestUtil;
import com.tigerobo.x.pai.dal.biz.dao.blog.BlogCrawlerDao;
import com.tigerobo.x.pai.dal.biz.dao.blog.BlogSearchDao;
import com.tigerobo.x.pai.dal.biz.dao.blog.BlogTranslateDao;
import com.tigerobo.x.pai.dal.biz.dao.pub.PubCrawlerSiteRelDao;
import com.tigerobo.x.pai.dal.biz.dao.pub.PubSiteDao;
import com.tigerobo.x.pai.dal.biz.entity.blog.BlogCrawlerPo;
import com.tigerobo.x.pai.dal.biz.entity.blog.BlogInfoPo;
import com.tigerobo.x.pai.dal.biz.entity.blog.BlogTranslatePo;
import com.tigerobo.x.pai.dal.biz.entity.pub.PubSitePo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BlogCrawlerService {
//    @Autowired
//    private BlogInfoDao blogInfoDao;
    @Autowired
    private BlogSearchDao blogSearchDao;
    @Autowired
    BlogCrawlerDao blogCrawlerDao;
    @Autowired
    private OssService ossService;
    @Autowired
    private LakeTranslateService lakeTranslateService;
    @Autowired
    private BlogTranslateDao blogTranslateDao;
    @Autowired
    private CrawlerService crawlerService;

    @Autowired
    private PubCrawlerSiteRelDao pubCrawlerSiteRelDao;

    @Autowired
    private LakeBlogCategoryService lakeBlogCategoryService;

    @Autowired
    private LakeAbstractService lakeAbstractService;

    @Autowired
    private BlogCategoryService blogCategoryService;

    @Autowired
    private BlogCategoryRelService blogCategoryRelService;

    @Autowired
    private PubSiteDao pubSiteDao;

    public void save2blog(BlogCrawlerPo crawlerPo) {
        save2blog(crawlerPo, new BlogCrawlerConfig());
    }

    public void save2blog(BlogCrawlerPo crawlerPo, BlogCrawlerConfig config) {

        if (crawlerPo == null){
            return;
        }
        BlogInfoPo blog = convert2blog(crawlerPo);

        Integer thirdId = crawlerPo.getThirdId();
        List<String> blogContent = crawlerService.getBlogContent(thirdId);

        doSave2blog(crawlerPo, blog, blogContent, config);
    }

    public boolean doUpdateBlogCategory(BlogCrawlerPo crawlerPo) {

        if (crawlerPo == null){
            return false;
        }

        Integer thirdId = crawlerPo.getThirdId();
        List<String> blogContent = crawlerService.getBlogContent(thirdId);

        BlogInfoPo dbBlog = blogSearchDao.getByThirdId(crawlerPo.getThirdId(), BlogSourceFromEnum.SITE.getType());
        if (dbBlog == null|| dbBlog.getId() == null){
            return false;
        }
        String title = crawlerPo.getTitle();
        List<String> categoryList = getCategory(thirdId,title, blogContent);

        blogCategoryRelService.addOrUpdateCategory(dbBlog.getId(),categoryList);

        BlogCrawlerPo update = new BlogCrawlerPo();
        update.setId(crawlerPo.getId());
        update.setCategoryStatus(1);
        blogCrawlerDao.update(update);
        return true;
    }

    public void updateCategoryFail(Integer id, String msg) {
        BlogCrawlerPo update = new BlogCrawlerPo();
        update.setId(id);
        update.setCategoryStatus(2);
        update.setErrMsg("分类"+msg);
        blogCrawlerDao.update(update);
    }
    public void updateFail(Integer id, String msg) {
        BlogCrawlerPo update = new BlogCrawlerPo();
        update.setId(id);
        update.setProcessStatus(2);
        update.setErrMsg(msg);
        blogCrawlerDao.update(update);
    }

    private BlogInfoPo convert2blog(BlogCrawlerPo item) {
        BlogInfoPo po = new BlogInfoPo();

        po.setThirdId(item.getThirdId());
        po.setHeadImg(item.getCoverImage());
        Date pdate = item.getPdate();
        Date thirdCreateTime = item.getThirdCreateTime();
        if (thirdCreateTime != null) {
            if (DateUtils.isSameDay(pdate, thirdCreateTime)) {
                po.setPublishTime(thirdCreateTime);
            }
        }
        if (po.getPublishTime() == null) {
            po.setPublishTime(pdate);
        }

//        po.setOssUrl(item.getOss_url());
        po.setSourceFrom(BlogSourceFromEnum.SITE.getType());
//        po.setSite(item.getSource());
        po.setSourceUrl(item.getUrl());
        po.setTitle(item.getTitle());
//        po.setTranslateTitle(item.getTitleCn());
//        po.setSourceName(item.getSrcName());
//        po.setProcessStatus(1);
        String author = item.getAuthor();
        if (!StringUtils.isEmpty(author)) {
            if (author.length() < 100 && !author.startsWith("http")) {
                po.setAuthorName(author);
            }
        }

//        po.setProcessStatus(1);
//        po.setSiteId(item.getSiteId());
        return po;
    }

    public void add2CrawlerBlogs(List<BlogCrawlerPo> pos) {
        if (CollectionUtils.isEmpty(pos)) {
            return;
        }

        List<Integer> thirdIds = pos.stream().map(po -> po.getThirdId()).collect(Collectors.toList());

        List<BlogCrawlerPo> existList = blogCrawlerDao.getByThirdIds(thirdIds);

        Map<Integer, BlogCrawlerPo> map = new HashMap<>();
        List<Integer> existThirdIds = new ArrayList<>();
        if (existList != null) {
            existThirdIds = existList.stream().map(e -> e.getThirdId()).collect(Collectors.toList());

            for (BlogCrawlerPo crawlerPo : existList) {
                map.put(crawlerPo.getThirdId(), crawlerPo);
            }

        }


        for (BlogCrawlerPo po : pos) {

            BlogCrawlerPo exist = map.get(po.getThirdId());
            if (exist != null) {
                if (StringUtils.isEmpty(po.getAuthor())) {
                    continue;
                }
                BlogCrawlerPo update = new BlogCrawlerPo();
                update.setId(exist.getId());
                update.setAuthor(po.getAuthor());
                blogCrawlerDao.update(update);
                continue;
            }
            try {
                blogCrawlerDao.add(po);
            }catch (Exception ex){
                log.error("add,{}",JSON.toJSONString(po),ex);
                throw new IllegalArgumentException("添加异常");
            }
        }
    }


    public void doSave2blog(BlogCrawlerPo crawlerPo, BlogInfoPo po, List<String> enList, BlogCrawlerConfig config) {
        if (CollectionUtils.isEmpty(enList)) {
            log.warn("thirdId:{},noTrans", po.getThirdId());
            throw new IllegalArgumentException("获取内容为空");
        }

        initSiteId(crawlerPo, po);

//        String category = getCategory(po.getTitle(), enList);
//        po.setCategory(category);
        if (!config.ignoreSummary){
            initSummary(po, enList);
        }else {
            po.setSummary(null);
            po.setTranslateSummary(null);
        }

        String translateTitle = lakeTranslateService.en2cn(crawlerPo.getTitle());
        Preconditions.checkArgument(!StringUtils.isEmpty(translateTitle), "翻译title失败");
        po.setTranslateTitle(translateTitle);

        if (!config.isIgnoreTranslate()) {
            List<String> zhList = getZnList(po.getThirdId(), enList,config);
            addTranslateLog(po, enList, zhList);
            uploadHtml(crawlerPo, po, enList, zhList);
        }
        po.setOnlineStatus(1);

        BlogInfoPo dbBlog = blogSearchDao.getByThirdId(crawlerPo.getThirdId(), BlogSourceFromEnum.SITE.getType());
        if (dbBlog == null) {
            blogSearchDao.add(po);
        } else {
            po.setId(dbBlog.getId());
            blogSearchDao.update(po);
        }

        updateCrawler2blogSuccess(crawlerPo.getId());
    }

    private void initSummary(BlogInfoPo po, List<String> enList) {
        String enSummary = null;
        if (!CollectionUtils.isEmpty(enList)) {
            StringBuilder enContentBuilder = new StringBuilder();
            int count = 0;
            for (int i = 0; i < enList.size(); i++) {

                String s = enList.get(i);
                if (StringUtils.isEmpty(s)||s.startsWith("<img")||s.startsWith("<code")){
                    continue;
                }
                if (count > 3) {
                    break;
                }
                count++;
                enContentBuilder.append(enList.get(i));
            }

            enSummary = lakeAbstractService.getAbstract(enContentBuilder.toString());
            if (StringUtils.isEmpty(enSummary)) {
                enSummary = HtmlContentUtil.getSummary(enList);
            }
            po.setSummary(enSummary);
        }
        String translateSummary = null;
        if (!StringUtils.isEmpty(enSummary)) {
            translateSummary = lakeTranslateService.doEn2cn(enSummary);
        }

        if (translateSummary == null) {
            translateSummary = "";
        }
        po.setTranslateSummary(translateSummary);
    }

    private void initSiteId(BlogCrawlerPo crawlerPo, BlogInfoPo po) {
        Integer srcId = crawlerPo.getSrcId();
        Integer siteId = pubCrawlerSiteRelDao.getSiteIdBySrcId(srcId);

        if (siteId == null) {
            throw new IllegalArgumentException("siteId为空");
        }

        PubSitePo pubSitePo = pubSiteDao.get(siteId);

        Preconditions.checkArgument(pubSitePo!=null,"没有匹配site");

        po.setVip(pubSitePo.getVip());
        po.setSiteId(siteId);
    }

    private List<String> getCategory(Integer thirdId, String title, List<String> enList) {

        List<String> allLabelNameList = blogCategoryService.getAllLabelNameList();

        String categoryContent = title;
        int count = 0;
        for (int i = 0; i < enList.size() && count<3; i++) {
            String text = enList.get(i);
            if (org.apache.commons.lang3.StringUtils.isBlank(text)||text.startsWith("<img")){
                continue;
            }
            if (StringUtils.isEmpty(text) || text.length() < 10) {
                continue;
            }
            categoryContent += " " + text;
            count++;
        }
        List<String> categoryList = lakeBlogCategoryService.callCategory(thirdId,categoryContent,allLabelNameList);
        return categoryList;
    }

    private void updateCrawler2blogSuccess(int id) {
        BlogCrawlerPo update = new BlogCrawlerPo();
        update.setId(id);
        update.setProcessStatus(5);
        blogCrawlerDao.update(update);
    }

    private void uploadHtml(BlogCrawlerPo crawlerPo, BlogInfoPo po, List<String> enList, List<String> zhList) {
        String zhContent = HtmlContentUtil.getContent(zhList);
        String zhenContent = HtmlContentUtil.getZhenContent(enList, zhList);


        String crawlerOssUrl = crawlerPo.getOssUrl();
        if (StringUtils.isEmpty(crawlerOssUrl)) {
            throw new IllegalArgumentException("没有源ossUrl");
        }
        String ossContent = RestUtil.get(crawlerOssUrl, null);
        if (StringUtils.isEmpty(ossContent)) {
            throw new IllegalArgumentException("获取原文内容失败");
        }
        ossContent = ossContent.replaceFirst("<link type=\"text/css\".*?/>", "");
        ossContent = ossContent.replaceFirst("<link .*?type=\"text/css\".*?/>", "");
//                ossApi.upload(ossPath,ossContent.getBytes(),"text/html; charset=utf-8");
        String sourceUrl = po.getSourceUrl();
        String md5 = Md5Util.getMd5(sourceUrl);
        String key = StringUtils.isEmpty(md5) ? String.valueOf(po.getThirdId()) : md5;

        String ossPath = "biz/blog/source/" + key + ".html";
        uploadOss(ossContent, ossPath);
        po.setOssUrl(ossPath);

        String transPath = "biz/blog/trans/" + key + ".html";
        String zhenPath = "biz/blog/zhen/" + key + ".html";

        uploadOss(zhContent, transPath);
        po.setTranslateUrl(transPath);

        uploadOss(zhenContent, zhenPath);
        po.setZhenUrl(zhenPath);
    }

    private void uploadOss(String ossContent, String ossPath) {
        String uploadUrl = ossService.uploadHtml(ossContent.getBytes(), ossPath);
        if (StringUtils.isEmpty(uploadUrl)) {
            throw new IllegalArgumentException("上传oss失败" + uploadUrl);
        }
    }


    private void addTranslateLog(BlogInfoPo po, List<String> enList, List<String> zhList) {
        try {
            BlogTranslatePo translatePo = new BlogTranslatePo();

            translatePo.setThirdId(po.getThirdId());
            translatePo.setContent(JSON.toJSONString(enList));
            translatePo.setTranslateContent(JSON.toJSONString(zhList));
            blogTranslateDao.addOrUpdate(translatePo);
        } catch (Exception ex) {
            log.error("add2blogTranslate,thirdId:{}", po.getThirdId(), ex);
        }
    }

    private List<String> getZnList(Integer thirdId, List<String> enList, BlogCrawlerConfig config) {

        config.setCanUseDbTranslate(true);
        if (config.canUseDbTranslate){
            BlogTranslatePo po = blogTranslateDao.getByKey(thirdId);
            if (po!=null){
                String translateContent = po.getTranslateContent();
                if (!StringUtils.isEmpty(translateContent)){
                    JSONArray objects = JSON.parseArray(translateContent);

                    List<String> list = new ArrayList<>();
                    for (int i = 0; i < objects.size(); i++) {
                        list.add(objects.getString(i));
                    }
                    if (enList.size() == list.size()){
                        return list;
                    }
                }
            }
        }
        List<String> waitList = new ArrayList<>();

        Map<Integer, Integer> indexMap = new HashMap<>();
        for (int i = 0; i < enList.size(); i++) {
            String content = enList.get(i);
            if (content.matches("<img\\s+src=.*>") || content.matches("<video.*>")) {
                continue;
            }
            indexMap.put(i, waitList.size());
            waitList.add(content);
        }
        List<String> tranResultList = lakeTranslateService.en2cn(waitList);
        if (CollectionUtils.isEmpty(tranResultList) || tranResultList.size() != waitList.size()) {
            log.error("blog:thirdId:{}", thirdId);
            throw new IllegalArgumentException("翻译失败");
        }
        List<String> zhList = new ArrayList<>();
        for (int i = 0; i < enList.size(); i++) {
            String en = enList.get(i);
            Integer index = indexMap.get(i);
            if (index == null) {
                zhList.add(en);
            } else {
                String trans = tranResultList.get(index);
                if (StringUtils.isEmpty(trans)) {
                    trans = en;
                }
                zhList.add(trans);
            }
        }
        return zhList;
    }

    @Data
    public static class BlogCrawlerConfig {
        boolean ignoreSummary;
        boolean ignoreTranslate;
        boolean canUseDbTranslate;
    }
}
