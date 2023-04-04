package com.tigerobo.x.pai.biz.biz.pub;

import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.biz.blog.req.UserPageReq;
import com.tigerobo.x.pai.api.vo.biz.pub.BigShotQueryReq;
import com.tigerobo.x.pai.api.vo.user.follow.FollowVo;
import com.tigerobo.x.pai.biz.biz.blog.BlogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class MediaService {

    @Autowired
    private PubSiteService pubSiteService;

    @Autowired
    private PubBigShotService pubBigShotService;

    @Autowired
    private BlogService blogService;

    public List<FollowVo> search(BigShotQueryReq req){

        req.setPageNum(1);
        PageVo<FollowVo> sitePage = pubSiteService.getPage(req,false);

        PageVo<FollowVo> bigShotPage = pubBigShotService.getBigShotPage(req,false);

        UserPageReq userReq = new UserPageReq();
        userReq.setKeyword(req.getKeyword());
        userReq.setPageNum(req.getPageNum());
        userReq.setPageSize(req.getPageSize());

        PageVo<FollowVo> bloggerPage = blogService.getBloggerPageList(userReq,false);

        return searchCombine(sitePage,bigShotPage,bloggerPage,req.getPageSize());
    }

    private List<FollowVo> searchCombine(PageVo<FollowVo> sitePage,PageVo<FollowVo> bigshotPage,PageVo<FollowVo> bloggerPage
            ,int pageSize){


        List<FollowVo> siteList = new ArrayList<>();
        if (sitePage!=null&& !CollectionUtils.isEmpty(sitePage.getList())){
            siteList = sitePage.getList();
        }

        List<FollowVo> bigshotList = new ArrayList<>();
        if (bigshotPage!=null&& !CollectionUtils.isEmpty(bigshotPage.getList())){
            bigshotList = bigshotPage.getList();
        }

        List<FollowVo> algoletList = new ArrayList<>();
        if (bloggerPage!=null&& !CollectionUtils.isEmpty(bloggerPage.getList())){
            algoletList = bloggerPage.getList();
        }
        int maxSize = Math.max(Math.max(siteList.size(),bigshotList.size()),algoletList.size());
        if (maxSize==0){
            return new ArrayList<>();
        }
        List<FollowVo> targetList = new ArrayList<>();
        for (int i = 0; i < maxSize; i++) {

            if (siteList.size()>i){
                targetList.add(siteList.get(i));
                if (targetList.size()>=pageSize){
                    break;
                }
            }
            if (bigshotList.size()>i){
                targetList.add(bigshotList.get(i));
                if (targetList.size()>=pageSize){
                    break;
                }
            }

            if (algoletList.size()>i){
                targetList.add(algoletList.get(i));
                if (targetList.size()>=pageSize){
                    break;
                }
            }
        }
        return targetList;
    }
}
