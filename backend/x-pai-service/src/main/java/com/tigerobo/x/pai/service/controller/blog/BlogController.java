package com.tigerobo.x.pai.service.controller.blog;

import com.google.common.base.Preconditions;
import com.tigerobo.x.pai.api.auth.entity.User;
import com.tigerobo.x.pai.api.vo.IdReqVo;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.biz.blog.*;
import com.tigerobo.x.pai.api.vo.biz.blog.req.*;
import com.tigerobo.x.pai.api.vo.user.follow.FollowVo;
import com.tigerobo.x.pai.biz.biz.blog.BlogChatService;
import com.tigerobo.x.pai.biz.biz.blog.BlogRecommendService;
import com.tigerobo.x.pai.biz.biz.blog.BlogSearchService;
import com.tigerobo.x.pai.biz.biz.blog.BlogService;
import com.tigerobo.x.pai.biz.converter.FollowBizConvert;
import com.tigerobo.x.pai.biz.data.es.EsBlogViewService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/web/blog/")
@Api(value = "blog",  tags = "blog")
public class BlogController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private BlogSearchService blogSearchService;

    @Autowired
    private EsBlogViewService esBlogViewService;

    @Autowired
    private BlogChatService blogChatService;

    @Autowired
    private BlogRecommendService blogRecommendService;

    @ApiOperation(value = "我的blog-列表")
    @PostMapping(path = {"getMineBlogList"}, consumes = "application/json", produces = "application/json")
    public PageVo<BlogVo> getMineBlogList(@Valid @RequestBody BlogQueryVo requestVo) {
        Integer userId = ThreadLocalHolder.getUserId();
        requestVo.setUserId(userId);
        return blogSearchService.getUserBlogs(requestVo);
    }

    @ApiOperation(value = "用户blog-列表")
    @PostMapping(path = {"getUserBlogList"}, consumes = "application/json", produces = "application/json")
    public PageVo<BlogVo> getUserBlogList(@Valid @RequestBody BlogQueryVo requestVo) {
        return blogSearchService.getUserBlogs(requestVo);
    }
    @ApiOperation(value = "blog-列表")
    @PostMapping(path = {"list"}, consumes = "application/json", produces = "application/json")
    public PageVo<BlogVo> blogList(@Valid @RequestBody BlogQueryVo requestVo) {
        return blogSearchService.getPageList(requestVo);
    }

    @ApiOperation(value = "blog-列表-by分类")
    @PostMapping(path = {"getListByCategory"}, consumes = "application/json", produces = "application/json")
    public PageVo<BlogVo> getListByCategory(@Valid @RequestBody BlogCategoryQueryVo requestVo) {
        return blogSearchService.getCategoryBlogs(requestVo);
    }
    @ApiOperation(value = "bigshot会话分页列表")
    @PostMapping(path = {"getBigShotChatPage"}, consumes = "application/json", produces = "application/json")
    public PageVo<BlogChatVo> getBigShotChatPage(@Valid @RequestBody ChatPageReq requestVo) {
        return blogChatService.getChatPage(requestVo);
    }

    @ApiOperation(value = "blog首页推荐列表")
    @PostMapping(path = {"getBlogByTag"}, consumes = "application/json", produces = "application/json")
    public PageVo<BlogVo> getBlogByTag(@Valid @RequestBody BlogHomeTabReq requestVo) {
        return blogRecommendService.getPageList(requestVo);
    }

    @ApiOperation(value = "会话详情")
    @PostMapping(path = {"getChatDetail"}, consumes = "application/json", produces = "application/json")
    public BlogMainChatVo getChatDetail(@Valid @RequestBody ChatDetailReq requestVo) {
        Preconditions.checkArgument(!StringUtils.isEmpty(requestVo.getId()),"会话id为空");
        return blogChatService.getChatDetail(requestVo.getId());
    }
    @ApiOperation(value = "blog-详情")
    @PostMapping(path = {"detail"}, consumes = "application/json", produces = "application/json")
    public BlogVo detail(@Valid @RequestBody BlogDetailReq requestVo) {
        BlogVo detail = null;
        try {
            Integer id = requestVo.getId();
            esBlogViewService.add(id);
            detail = blogService.getPageDetail(requestVo);
        }finally {
            Integer sourceFrom = detail == null ? null : detail.getSourceFrom();
            blogService.incrView(requestVo.getId(),sourceFrom);
        }
        return detail;
    }

    @Deprecated
    @ApiOperation(value = "推荐用户")
    @PostMapping(path = {"recommendUsers"}, produces = "application/json")
    public List<FollowVo> recommendUsers() {
        List<User> recommendUsers = blogService.getRecommendUsers();

        if (CollectionUtils.isEmpty(recommendUsers)){
            return new ArrayList<>();
        }

        return recommendUsers.stream().map(u-> FollowBizConvert.convert(u)).collect(Collectors.toList());
    }

    @ApiOperation(value = "blog作者分页列表")
    @PostMapping(path = {"getBloggerPageList"},consumes = "application/json", produces = "application/json")
    public PageVo<FollowVo> getBloggerPageList(@RequestBody UserPageReq req) {
        return blogService.getBloggerPageList(req);
    }

    @ApiOperation(value = "blog上一篇下一篇")
    @PostMapping(path = {"getBlogNextOrPre"},consumes = "application/json", produces = "application/json")
    public SiteRelBlogVo getBloggerPageList(@RequestBody IdReqVo req) {
        return blogService.getSiteRelBlog(req.getId());
    }

    @ApiOperation(value = "相关指标")
    @PostMapping(path = {"metric"}, produces = "application/json")
    public BlogMetric metric() {

        int blogCount = blogService.getBlogCount();
        int bloggerCount = blogService.getBloggerCount();
        List<User> topUsers = blogService.getTopUsers();

        if (topUsers == null){
            topUsers = new ArrayList<>();
        }
        return BlogMetric.builder()
                .bloggerNum(bloggerCount)
                .blogNum(blogCount)
                .topUsers(topUsers)
                .build();
    }

    @Data
    @Builder
    private static class BlogMetric{
        int blogNum;
        int bloggerNum;
        List<User> topUsers;
    }

}
