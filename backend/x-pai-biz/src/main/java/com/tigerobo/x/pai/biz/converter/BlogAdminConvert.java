package com.tigerobo.x.pai.biz.converter;

import com.tigerobo.x.pai.api.admin.BlogAdminVo;
import com.tigerobo.x.pai.api.constants.OssConstant;
import com.tigerobo.x.pai.api.enums.BlogSourceFromEnum;
import com.tigerobo.x.pai.api.enums.LagTypeEnum;
import com.tigerobo.x.pai.dal.biz.entity.blog.BlogInfoPo;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BlogAdminConvert {


    public static List<BlogAdminVo> convertList(List<BlogInfoPo> pos) {
        if (CollectionUtils.isEmpty(pos)) {
            return new ArrayList<>();
        }
        return pos.stream().map(BlogAdminConvert::convert).collect(Collectors.toList());
    }

    public static BlogAdminVo convert(BlogInfoPo po) {
        if (po == null) {
            return null;
        }
        BlogAdminVo blogVo = new BlogAdminVo();
        blogVo.setId(po.getId());
        blogVo.setUserId(po.getUserId());
        blogVo.setHeadImg(po.getHeadImg());
        blogVo.setTitle(po.getTitle());
        blogVo.setTranslateTitle(po.getTranslateTitle());

        blogVo.setSummary(po.getSummary());
        blogVo.setTranslateSummary(po.getTranslateSummary());

        String ossUrl = po.getOssUrl();
        if (!StringUtils.isEmpty(ossUrl)) {
            ossUrl = OssConstant.domainUrl + ossUrl;
        }
        blogVo.setOssUrl(ossUrl);

        String translateUrl = po.getTranslateUrl();
        if (!StringUtils.isEmpty(translateUrl)) {
            translateUrl = OssConstant.domainUrl + translateUrl;
        }
        blogVo.setTranslateUrl(translateUrl);
        blogVo.setPublishTime(po.getPublishTime());

        Integer sourceFrom = po.getSourceFrom();
        if (BlogSourceFromEnum.SITE.getType().equals(sourceFrom) || BlogSourceFromEnum.BIG_SHOT.getType().equals(sourceFrom)) {
            blogVo.setLagType(LagTypeEnum.ZH_EN.getType());
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

        blogVo.setSiteId(po.getSiteId());

        blogVo.setContent(po.getContent());
        blogVo.setTranslateContent(po.getTranslateContent());
        blogVo.setBigShotId(po.getBigShotId());

        blogVo.setHasReplyChain(po.getHasReplyChain());
        blogVo.setVip(po.getVip());

        Boolean isDeleted = po.getIsDeleted();
        if (isDeleted!=null&&isDeleted){
            blogVo.setIsDeleted(1);
        }else {
            blogVo.setIsDeleted(0);
        }

        blogVo.setRecommend(po.getRecommend());

        blogVo.setTagType(po.getTagType());
        blogVo.setRecommendTime(po.getRecommendTime());

        return blogVo;
    }


}
