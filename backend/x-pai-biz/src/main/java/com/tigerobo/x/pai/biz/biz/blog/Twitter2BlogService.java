package com.tigerobo.x.pai.biz.biz.blog;

import com.google.common.base.Preconditions;
import com.tigerobo.x.pai.api.enums.BlogSourceFromEnum;
import com.tigerobo.x.pai.api.enums.JobStatusEnum;
import com.tigerobo.x.pai.biz.biz.OssService;
import com.tigerobo.x.pai.biz.converter.BlogChatConvert;
import com.tigerobo.x.pai.biz.converter.BlogConvert;
import com.tigerobo.x.pai.biz.converter.PubBigShotConvert;
import com.tigerobo.x.pai.biz.lake.LakeTranslateService;
import com.tigerobo.x.pai.biz.utils.HtmlContentUtil;
import com.tigerobo.x.pai.biz.utils.Md5Util;
import com.tigerobo.x.pai.dal.biz.dao.blog.BlogBigshotReplyDao;
import com.tigerobo.x.pai.dal.biz.dao.blog.BlogSearchDao;
import com.tigerobo.x.pai.dal.biz.dao.blog.BlogTwitterCrawlerDao;
import com.tigerobo.x.pai.dal.biz.dao.pub.PubBigShotDao;
import com.tigerobo.x.pai.dal.biz.entity.blog.BlogBigshotReplyPo;
import com.tigerobo.x.pai.dal.biz.entity.blog.BlogChatPo;
import com.tigerobo.x.pai.dal.biz.entity.blog.BlogInfoPo;
import com.tigerobo.x.pai.dal.biz.entity.blog.BlogTwitterCrawlerPo;
import com.tigerobo.x.pai.dal.biz.entity.pub.PubBigShotPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class Twitter2BlogService {

    @Autowired
    private BlogTwitterCrawlerDao blogTwitterCrawlerDao;

//    @Autowired
//    private BlogInfoDao blogInfoDao;
    @Autowired
    private BlogSearchDao blogSearchDao;

    @Autowired
    private LakeTranslateService lakeTranslateService;
    @Autowired
    private OssService ossService;

    @Autowired
    private PubBigShotDao pubBigShotDao;

    @Autowired
    private BlogBigshotReplyDao blogBigshotReplyDao;

    @Autowired
    private BlogChatService blogChatService;


    public List<BlogTwitterCrawlerPo> getWaitDealList(boolean test){

        if (test){
            return blogTwitterCrawlerDao.getTestWaitList();
        }else {
            return blogTwitterCrawlerDao.getWaitList();
        }

    }

    @Transactional(value = "paiTm")
    public void save2blog(BlogTwitterCrawlerPo crawlerPo){

        BlogInfoPo po = BlogTwitterConvert.po2blog(crawlerPo);
        PubBigShotPo bigShot = pubBigShotDao.loadBySrcId(crawlerPo.getSrcId());
        if (bigShot == null){
            updateFail(crawlerPo.getId(),"bigshot映射srcId不存在");
            return;
        }
        String summary = initSummary(po);
        String headImgUrl = HtmlContentUtil.getHeadImgUrlFromContent(po.getContent());
        po.setHeadImg(headImgUrl);

        initZh(po, summary);
//        initOss(po, summary);

        List<BlogBigshotReplyPo> replyPos = PubBigShotConvert.convertReply(crawlerPo.getReplyChain(), crawlerPo.getSpecId(), crawlerPo.getChatId());

        if (!CollectionUtils.isEmpty(replyPos)){
            for (BlogBigshotReplyPo replyPo : replyPos) {
                initReplyContent(replyPo.getContent(),replyPo);
            }
        }else {
            BlogBigshotReplyPo replyPo = BlogConvert.blogPo2reply(po, crawlerPo.getSpecId());
            replyPos = new ArrayList<>();
            replyPos.add(replyPo);

            po.setSpecId(crawlerPo.getSpecId());
        }
        List<BlogChatPo> chatPoList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(replyPos)){
            chatPoList = BlogChatConvert.reply2chatPo(replyPos);
        }

        po.setOnlineStatus(1);
        po.setBigShotId(bigShot.getId());
        po.setVip(bigShot.getVip());
        operate(crawlerPo, po,replyPos,chatPoList);
    }


    private void initOss(BlogInfoPo po, String summary) {
        String sourceUrl = po.getSourceUrl();
        String md5 = Md5Util.getMd5(sourceUrl);
        String key = StringUtils.isEmpty(md5)?String.valueOf(po.getThirdId()):md5;

        String ossPath = "biz/blog/source/"+key+".html";

        String ossContentHtml = HtmlContentUtil.str2html(summary);
        doUpload(ossContentHtml,ossPath);

        po.setOssUrl(ossPath);
        po.setTranslateUrl(ossPath);
        po.setZhenUrl(ossPath);
    }

    private void initZh(BlogInfoPo po, String summary) {
        String zhContent = lakeTranslateService.en2cn(summary);
        if (zhContent == null){
            po.setTranslateSummary("");
        }else {
            po.setTranslateSummary(zhContent);
        }

        if (zhContent == null){
            zhContent = "";
        }else {
            zhContent = "<p>"+zhContent+"</p>";
        }
        po.setTranslateContent(zhContent);
    }


    private String initSummary(BlogInfoPo po) {
        String summary = po.getSummary();

        List<String> list = HtmlContentUtil.parseHtmlCleanContent(summary);
        if (StringUtils.isEmpty(list)){
            throw new IllegalArgumentException("解析失败");
        }


        summary = String.join(" ",list);
        po.setSummary(summary);
        return summary;
    }

    private void initReplyContent(String content, BlogBigshotReplyPo replyPo){
        replyPo.setSummary("");
        replyPo.setTranslateSummary("");
        replyPo.setTranslateContent("");

        if (StringUtils.isEmpty(content)){
            return ;
        }
        List<String> list = HtmlContentUtil.parseHtmlCleanContent(content);
        if (CollectionUtils.isEmpty(list)){
            return ;
        }
        String headImgUrl = HtmlContentUtil.getHeadImgUrlFromContent(content);
        replyPo.setHeadImg(headImgUrl);

        String summary = String.join(" ",list);

        replyPo.setSummary(summary);

        String zhSummary = lakeTranslateService.en2cn(summary);

        replyPo.setTranslateSummary(zhSummary);

        String zhContent = zhSummary;
        if (zhContent == null){
            zhContent = "";
        }else {
            zhContent = "<p>"+zhContent+"</p>";
        }
        replyPo.setTranslateContent(zhContent);
    }

    private void operate(BlogTwitterCrawlerPo crawlerPo, BlogInfoPo po,List<BlogBigshotReplyPo> replyPos
            ,List<BlogChatPo> chatPoList) {
        BlogInfoPo blogInDb = blogSearchDao.getByThirdId(po.getThirdId(), BlogSourceFromEnum.BIG_SHOT.getType());
        po.setHasReplyChain(replyPos!=null&&replyPos.size()>1);

        if (blogInDb==null){
            blogSearchDao.add(po);
        }else {
            po.setId(blogInDb.getId());
            blogSearchDao.update(po);
        }

        Integer id = po.getId();
        Preconditions.checkArgument(id!=null,"创建blog失败"+ po.getThirdId());

        addReply(replyPos,id);

        doAddChat(crawlerPo, chatPoList, id,po.getBigShotId());
        BlogTwitterCrawlerPo update = new BlogTwitterCrawlerPo();

        update.setId(crawlerPo.getId());
        update.setProcessStatus(1);
        update.setBlogId(id);
        blogTwitterCrawlerDao.update(update);
    }

    private void doAddChat(BlogTwitterCrawlerPo crawlerPo, List<BlogChatPo> chatPoList, Integer id,Integer bigShotId) {
        String specId = crawlerPo.getSpecId();
        if (StringUtils.isEmpty(specId)||CollectionUtils.isEmpty(chatPoList)){
            return;
        }

        String name = null;
        for (BlogChatPo chatPo : chatPoList) {
            if (specId.equalsIgnoreCase(chatPo.getSpecId())){
                chatPo.setBlogId(id);
                chatPo.setBigShotId(bigShotId);
                name = chatPo.getAuthor();
            }
            if (chatPo.getSpecId().equalsIgnoreCase(chatPo.getChatId())){
                chatPo.setMainChat(true);
            }
        }

        if (!StringUtils.isEmpty(name)&&chatPoList.size()>1){
            for (BlogChatPo chatPo : chatPoList) {
                if (chatPo.getBigShotId()==null||chatPo.getBigShotId()==0){
                    if (name.equalsIgnoreCase(chatPo.getAuthor())){
                        chatPo.setBigShotId(bigShotId);
                    }
                }
            }
        }
        blogChatService.add(chatPoList);
    }

    private void updateFail(Integer crawlerId,String msg){
        BlogTwitterCrawlerPo update = new BlogTwitterCrawlerPo();

        update.setId(crawlerId);
        update.setProcessStatus(JobStatusEnum.FAIL.getStatus());
        update.setErrMsg(msg);
        blogTwitterCrawlerDao.update(update);
    }

    private void addReply(List<BlogBigshotReplyPo> replyPos,Integer blogId){

        if (CollectionUtils.isEmpty(replyPos)){
            return ;
        }
        for (BlogBigshotReplyPo replyPo : replyPos) {
            replyPo.setBlogId(blogId);
        }

        blogBigshotReplyDao.delete(blogId);
        blogBigshotReplyDao.addList(replyPos);
    }


    private void doUpload(String content, String path){
        String uploadUrl = ossService.uploadHtml(content.getBytes(), path);
        if (StringUtils.isEmpty(uploadUrl)){
            throw new IllegalArgumentException("上传oss失败"+uploadUrl);
        }
    }


}
