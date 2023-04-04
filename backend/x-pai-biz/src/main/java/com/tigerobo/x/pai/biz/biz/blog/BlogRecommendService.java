package com.tigerobo.x.pai.biz.biz.blog;

import com.github.pagehelper.Page;
import com.google.common.base.Preconditions;
import com.tigerobo.x.pai.api.enums.BlogTabEnum;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.biz.blog.BlogQueryVo;
import com.tigerobo.x.pai.api.vo.biz.blog.BlogVo;
import com.tigerobo.x.pai.api.vo.biz.blog.req.BlogHomeTabReq;
import com.tigerobo.x.pai.biz.auth.FollowService;
import com.tigerobo.x.pai.biz.biz.blog.base.BlogBaseService;
import com.tigerobo.x.pai.dal.biz.dao.blog.BlogSearchDao;
import com.tigerobo.x.pai.dal.biz.entity.blog.BlogInfoPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BlogRecommendService {

    @Autowired
    private BlogSearchService blogSearchService;
    @Autowired
    private FollowService followService;
//    @Autowired
//    private BlogInfoDao blogInfoDao;

    @Autowired
    private BlogSearchDao blogSearchDao;
    @Autowired
    private BlogBaseService blogBaseService;

    /**
     * 首页推荐列表
     * 1,今日份，合并用户关注列表与推荐列表
     * 2,黑科技，使用tagType=1
     * 3,大咖说,使用bigshot数据
     * @param req
     * @return
     */
    public PageVo<BlogVo> getPageList(BlogHomeTabReq req) {
        Preconditions.checkArgument(req!=null,"请求参数异常");
        String keyword = req.getTab();
        BlogQueryVo queryVo=new BlogQueryVo();
        queryVo.setPageNum(req.getPageNum());
        queryVo.setPageSize(req.getPageSize());
        Page<BlogInfoPo> poList=null;
        int insertPageSize=3;
        int space=queryVo.getPageSize()/insertPageSize;

        final Integer startId = blogBaseService.getStartId();


        if("recommend".equals(keyword)){
            queryVo.setTabType(BlogTabEnum.FOLLOW.name());

            PageVo<BlogVo> followList= blogSearchService.getPageList(queryVo);
            poList = blogSearchDao.getRecommendBlogs(req,insertPageSize,startId);
            return unionList(space,poList, followList);

        }else if("blackTech".equals(keyword)){
            //site
//            queryVo.setTabType(BlogTabEnum.SITE.name());
//            PageVo<BlogVo> siteList= blogSearchService.getPageList(queryVo);
//            poList=blogInfoDao.getBlackTechBlogs(req,insertPageSize);
//            return unionList(space,poList, siteList);
            poList=blogSearchDao.getBlackTechBlogs(req, req.getPageSize(),startId);
            final List<BlogVo> blogVos = blogSearchService.pos2vos(poList);
            PageVo<BlogVo> vos=new PageVo<>();
            vos.setList(blogVos);
            if (poList!=null){
                vos.setTotal(poList.getTotal());
            }

            vos.setPageNum(req.getPageNum());
            vos.setPageSize(req.getPageSize());
            vos.setHasMore(vos.isHasMore());
            return vos;

        }else if("bigshot".equals(keyword)){

            queryVo.setTabType(BlogTabEnum.BIGSHOTS.name());
            return blogSearchService.getPageList(queryVo);
        }else {
            return new PageVo<>();
        }



    }
    private PageVo<BlogVo> unionList( int space,Page<BlogInfoPo> poList,PageVo<BlogVo> followList){
//        PageVo<BlogVo> recommendVo = blogSearchService.buildPage(queryVo, poList, false, false);

        final List<BlogVo> recommendVos = blogSearchService.pos2vos(poList);
        final List<BlogVo> res = followList.getList();
        //combine recommendVos followVos
        if(CollectionUtils.isEmpty(recommendVos)){
            return followList;
        }else {
            for (int i = 0; i < recommendVos.size(); i++) {
                res.add(i*space,recommendVos.get(i));
            }
            final List<BlogVo> collect = res.stream().distinct().collect(Collectors.toList());
            followList.setList(collect);
        }

        return followList;
    }


}
