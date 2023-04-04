package com.tigerobo.x.pai.biz.converter;

import com.tigerobo.x.pai.api.constants.OssConstant;
import com.tigerobo.x.pai.api.enums.BlogSourceFromEnum;
import com.tigerobo.x.pai.api.enums.LagTypeEnum;
import com.tigerobo.x.pai.api.vo.biz.blog.BlogVo;
import com.tigerobo.x.pai.api.vo.biz.blog.req.BlogAddReq;
import com.tigerobo.x.pai.dal.biz.entity.blog.BlogBigshotReplyPo;
import com.tigerobo.x.pai.dal.biz.entity.blog.BlogInfoPo;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class BlogConvert {


    public static BlogInfoPo convertAddBlog(BlogAddReq addReq) {
        BlogInfoPo po = new BlogInfoPo();
        po.setId(addReq.getId());
        po.setTitle(addReq.getTitle());
        po.setSummary(addReq.getSummary());
//        po.setHeadImg(addReq.getHeadImg());
        po.setOnlineStatus(addReq.getOnlineStatus());
        po.setContent(addReq.getContent());
        return po;
    }

    public static List<BlogVo> convertList(List<BlogInfoPo> pos) {
        if (CollectionUtils.isEmpty(pos)) {
            return new ArrayList<>();
        }
        return pos.stream().map(BlogConvert::convert).collect(Collectors.toList());
    }

    public static BlogVo convert(BlogInfoPo po) {
        if (po == null) {
            return null;
        }
        BlogVo blogVo = new BlogVo();
        blogVo.setId(po.getId());
        blogVo.setUserId(po.getUserId());
        String headImg = po.getHeadImg();
        if (!StringUtils.isEmpty(headImg)) {
            if (headImg.startsWith("http://")) {
                headImg.replaceFirst("http", "https");
            }
            blogVo.setHeadImg(headImg);
        }
        blogVo.setTitle(po.getTitle());
        blogVo.setSummary(po.getSummary());

        String ossUrl = po.getOssUrl();
        if (!StringUtils.isEmpty(ossUrl) && !ossUrl.startsWith("http")) {
            ossUrl = OssConstant.domainUrl + ossUrl;
        }
        blogVo.setOssUrl(ossUrl);
        blogVo.setPublishTime(po.getPublishTime());

        String translateUrl = po.getTranslateUrl();
        blogVo.setTranslateUrl(OssConstant.domainUrl + translateUrl);
        blogVo.setTranslateSummary(po.getTranslateSummary());
        blogVo.setTranslateTitle(po.getTranslateTitle());

//        String category = po.getCategory();
//        List<String> categoryList = new ArrayList<>();

//        if (!StringUtils.isEmpty(category)) {
//            String[] split = category.split(",");
//            categoryList = Arrays.asList(split);
//        }
//        blogVo.setCategoryList(categoryList);

        Integer sourceFrom = po.getSourceFrom();
        if (BlogSourceFromEnum.SITE.getType().equals(sourceFrom)) {
            blogVo.setEnTitle(po.getTitle());
            blogVo.setEnSummary(po.getSummary());

            blogVo.setZhTitle(po.getTranslateTitle());
            blogVo.setZhSummary(po.getTranslateSummary());

            blogVo.setLagType(LagTypeEnum.ZH_EN.getType());
            blogVo.setEnUrl(ossUrl);

            if (!StringUtils.isEmpty(translateUrl)) {
                blogVo.setZhUrl(OssConstant.domainUrl + translateUrl);
            }
            String zhenUrl = po.getZhenUrl();
            if (!StringUtils.isEmpty(zhenUrl)) {
                blogVo.setZhenUrl(OssConstant.domainUrl + zhenUrl);
            }

            blogVo.setLagType(LagTypeEnum.ZH_EN.getType());
        } else {
            blogVo.setLagType(LagTypeEnum.UNKNOWN.getType());
        }

        blogVo.setSourceUrl(po.getSourceUrl());

        blogVo.setSourceFrom(po.getSourceFrom());
        blogVo.setSourceName(po.getSourceName());
        blogVo.setCreateTime(po.getCreateTime());
        blogVo.setAuthorName(po.getAuthorName());
        blogVo.setOnlineStatus(po.getOnlineStatus());

        if (po.getPublishTime() != null) {
            boolean newBlog = DateUtils.addDays(new Date(), -2).before(po.getPublishTime());
            blogVo.setIsNew(newBlog);
        }
        blogVo.setSiteId(po.getSiteId());

        blogVo.setContent(po.getContent());
        blogVo.setTranslateContent(po.getTranslateContent());
        blogVo.setBigShotId(po.getBigShotId());

        blogVo.setHasReplyChain(po.getHasReplyChain());

        return blogVo;
    }

    public static BlogBigshotReplyPo blogPo2reply(BlogInfoPo po, String specId) {
        BlogBigshotReplyPo reply = new BlogBigshotReplyPo();

        reply.setSeq(0);
        reply.setAuthor(po.getAuthorName());
        reply.setPublishTime(po.getPublishTime());
        reply.setContent(po.getContent());
        reply.setTranslateContent(po.getTranslateContent());
        reply.setCurrentBlog(true);

        reply.setSpecId(specId);

        reply.setSummary(po.getSummary());

        reply.setTranslateSummary(po.getTranslateSummary());
        reply.setChatId(specId);
        reply.setHeadImg(po.getHeadImg());
        return reply;
    }
}
