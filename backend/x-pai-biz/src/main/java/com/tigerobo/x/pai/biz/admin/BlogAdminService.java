package com.tigerobo.x.pai.biz.admin;

import com.github.pagehelper.Page;
import com.google.common.base.Preconditions;
import com.tigerobo.x.pai.api.admin.req.BlogAdminQueryReq;
import com.tigerobo.x.pai.api.admin.BlogAdminVo;
import com.tigerobo.x.pai.api.admin.req.AdminOnlineStatusReq;
import com.tigerobo.x.pai.api.admin.req.blog.BlogTagReq;
import com.tigerobo.x.pai.api.admin.req.blog.BlogRecommendReq;
import com.tigerobo.x.pai.api.auth.entity.User;
import com.tigerobo.x.pai.api.auth.service.UserService;
import com.tigerobo.x.pai.api.dto.blog.BlogCategoryDto;
import com.tigerobo.x.pai.api.dto.blog.BlogCategoryRelDto;
import com.tigerobo.x.pai.api.enums.BlogSourceFromEnum;
import com.tigerobo.x.pai.api.enums.BusinessEnum;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.biz.PageReqVo;
import com.tigerobo.x.pai.api.vo.biz.blog.BlogChatVo;
import com.tigerobo.x.pai.api.vo.biz.pub.PubSiteVo;
import com.tigerobo.x.pai.biz.biz.blog.BlogCategoryRelService;
import com.tigerobo.x.pai.biz.biz.blog.BlogCategoryService;
import com.tigerobo.x.pai.biz.biz.blog.BlogChatService;
import com.tigerobo.x.pai.biz.biz.blog.BlogService;
import com.tigerobo.x.pai.biz.biz.log.ShareLogService;
import com.tigerobo.x.pai.biz.biz.pub.PubSiteService;
import com.tigerobo.x.pai.biz.converter.BlogAdminConvert;
import com.tigerobo.x.pai.biz.converter.BlogCategoryConvert;
import com.tigerobo.x.pai.biz.user.comment.BusinessCommentService;
import com.tigerobo.x.pai.biz.user.thumb.UserThumbService;
import com.tigerobo.x.pai.dal.admin.dao.BlogAdminDao;
import com.tigerobo.x.pai.dal.biz.dao.blog.BlogInfoDao;
import com.tigerobo.x.pai.dal.biz.dao.blog.BlogSearchDao;
import com.tigerobo.x.pai.dal.biz.dao.pub.PubBigShotDao;
import com.tigerobo.x.pai.dal.biz.entity.blog.BlogInfoPo;
import com.tigerobo.x.pai.dal.biz.entity.pub.PubBigShotPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BlogAdminService {

    @Autowired
    private BlogAdminDao blogAdminDao;
    @Autowired
    private BlogInfoDao blogInfoDao;

    @Autowired
    private BlogSearchDao blogSearchDao;

    @Autowired
    private BlogService blogService;
    @Autowired
    private PubSiteService pubSiteService;

    @Autowired
    private PubBigShotDao pubBigShotDao;

    @Autowired
    private UserService userService;

    @Autowired
    private BusinessCommentService businessCommentService;

    @Autowired
    private ShareLogService shareLogService;

    @Autowired
    private UserThumbService userThumbService;

    @Autowired
    private BlogChatService blogChatService;

    @Autowired
    private BlogCategoryRelService blogCategoryRelService;
    @Autowired
    private BlogCategoryService blogCategoryService;


    public void updateRecommend(BlogRecommendReq req){

        Validate.isTrue(req != null,"参数不正确");
        Validate.isTrue(req.getId()!=null,"参数为空");
        Validate.isTrue(req.getRecommend()!=null,"recommend为空");
        BlogInfoPo po = new BlogInfoPo();
        po.setId(req.getId());
        if (req.getRecommend()==1){
            if (req.getRecommendTime() == null){
                req.setRecommendTime(new Date());
            }
        }
        po.setRecommend(req.getRecommend());
        po.setRecommendTime(req.getRecommendTime());
        blogSearchDao.update(po);
    }

    public void updateTag(BlogTagReq req){

        Validate.isTrue(req.getId()!=null,"id为空");


        BlogInfoPo po = new BlogInfoPo();
        po.setId(req.getId());
        po.setTagType(req.getTagType());
        blogSearchDao.update(po);
    }


    public PageVo<BlogAdminVo> getPageList(BlogAdminQueryReq queryVo) {
        Page<Integer> ids = blogAdminDao.searchBlogPage(queryVo);
        if(CollectionUtils.isEmpty(ids)){
            return new PageVo<>();
        }
        List<BlogInfoPo> pos = blogInfoDao.getByIds(ids,"id desc");
        return buildPage(queryVo,pos,(int)ids.getTotal());

    }


    private PageVo<BlogAdminVo> buildPage(PageReqVo queryVo, List<BlogInfoPo> poList, int total) {

        List<Integer> blogIds = poList.stream().map(b -> b.getId()).collect(Collectors.toList());

        Map<Integer, List<BlogCategoryRelDto>> blogCategoryMap = blogCategoryRelService.getBlogCategoryMap(blogIds);

        List<BlogAdminVo> blogVos = poList.stream().map(po -> {
            BlogAdminVo blogVo = BlogAdminConvert.convert(po);
            Integer id = blogVo.getId();
            PubSiteVo site = pubSiteService.getFromCache(blogVo.getSiteId(), null);

//            blogVo.setSite(site);
            if (site != null) {
                blogVo.setSourceLogo(site.getLogoOss());
                blogVo.setSourceName(site.getName());
            }

            List<BlogCategoryRelDto> blogCategoryRelDtos = blogCategoryMap.get(id);
            if (!CollectionUtils.isEmpty(blogCategoryRelDtos)) {
                List<BlogCategoryDto> blogCategoryDtos = BlogCategoryConvert.convertList(blogCategoryRelDtos);
                blogVo.setBlogCategoryList(blogCategoryDtos);
            }

            BlogSourceFromEnum sourceFrom = BlogSourceFromEnum.getByType(blogVo.getSourceFrom());

            if (BlogSourceFromEnum.SITE == sourceFrom && site != null) {
                blogVo.setSourceLogo(site.getLogoOss());
                blogVo.setSourceName(site.getName());
            } else if (BlogSourceFromEnum.ALGOLET == sourceFrom) {

                User user = userService.getCacheUserByOther(blogVo.getUserId());
                blogVo.setSourceName("algolet");
                if (user != null) {
                    blogVo.setSourceLogo(user.getAvatar());
                    blogVo.setAuthorName(user.getName());
                }

            } else if (BlogSourceFromEnum.BIG_SHOT == sourceFrom) {

                Integer bigShotId = blogVo.getBigShotId();

                PubBigShotPo pubBigShotPo = pubBigShotDao.load(bigShotId);
                if (pubBigShotPo != null) {
                    blogVo.setSourceLogo(pubBigShotPo.getLogo());
                    blogVo.setAuthorName(pubBigShotPo.getName());
                }
                blogVo.setSourceName("twitter");
                Boolean hasReplyChain = blogVo.getHasReplyChain();
                if (hasReplyChain != null && hasReplyChain) {
                    List<BlogChatVo> chatList = blogChatService.getBlogChainByBlogId(po.getId(), po.getSpecId());
                    if (!CollectionUtils.isEmpty(chatList)) {
                        blogVo.setReplyList(chatList);
                    } else {
                        blogVo.setHasReplyChain(false);
                    }
                }

                blogVo.setTitle(blogVo.getSummary());
                blogVo.setTranslateTitle(blogVo.getTranslateSummary());
            }
            return blogVo;
        }).collect(Collectors.toList());

        completeCount(blogVos);

        PageVo<BlogAdminVo> pageInfo = new PageVo<>();
        pageInfo.setPageNum(queryVo.getPageNum());
        pageInfo.setPageSize(queryVo.getPageSize());
        pageInfo.setList(blogVos);
        pageInfo.setTotal(total);
        return pageInfo;
    }



    private void completeCount(List<BlogAdminVo> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        list.parallelStream().forEach(blogVo -> {
            try {
                int view = blogService.getView(blogVo.getId());
                blogVo.setViewNum(view);
                Integer id = blogVo.getId();

                blogVo.setThumbUpNum(userThumbService.count(String.valueOf(id),BusinessEnum.BLOG.getType()));
//                blogVo.setThumbDownNum(userThumbService.countBlogThumbDown(id));

                int commentNum = businessCommentService.countFromCache(String.valueOf(id), BusinessEnum.BLOG.getType());

                blogVo.setCommentNum(commentNum);
                int shareNum = shareLogService.getCount(String.valueOf(id), BusinessEnum.BLOG.getType());
                blogVo.setShareNum(shareNum);
            } catch (Exception ex) {
                log.error("completeCount:{}", blogVo.getId(), ex);
            }
        });
    }



    public void opeOnlineStatus(AdminOnlineStatusReq req){
        Preconditions.checkArgument(req!=null,"请求参数为空");

        Integer blogId = req.getBlogId();
        Integer onlineStatus = req.getOnlineStatus();
        Preconditions.checkArgument(blogId!=null&&blogId>0,"请求id异常");
        Preconditions.checkArgument(onlineStatus!=null&&(onlineStatus.equals(0)||onlineStatus.equals(1)),"请求操作无效");
        BlogInfoPo load = blogInfoDao.load(blogId);
        if(load==null){
            return ;
        }
        if(load.getOnlineStatus().equals(onlineStatus)){
            return ;
        }
        BlogInfoPo po=new BlogInfoPo();
        po.setId(blogId);
        po.setOnlineStatus(onlineStatus);
        blogSearchDao.update(po);

    }

    public void  blogDelete(Integer blogId){
        Preconditions.checkArgument(blogId!=null,"请求id为空");
        BlogInfoPo load = blogInfoDao.load(blogId);
        if(load==null){
            return ;
        }
        if(load.getIsDeleted()){
            return;
        }
        BlogInfoPo po=new BlogInfoPo();
        po.setId(blogId);
        po.setIsDeleted(true);
        blogSearchDao.update(po);

    }

    public List<BlogCategoryDto> categoryQuery(){
        return blogCategoryService.getAllShow();
    }
}
