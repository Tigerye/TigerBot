package com.tigerobo.x.pai.service.controller.blog;

import com.tigerobo.x.pai.api.auth.aspect.Authorize;
import com.tigerobo.x.pai.api.auth.entity.User;
import com.tigerobo.x.pai.api.vo.IdReqVo;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.ResultVO;
import com.tigerobo.x.pai.api.vo.biz.blog.BlogCategoryQueryVo;
import com.tigerobo.x.pai.api.vo.biz.blog.BlogQueryVo;
import com.tigerobo.x.pai.api.vo.biz.blog.BlogVo;
import com.tigerobo.x.pai.api.vo.biz.blog.req.BlogAddReq;
import com.tigerobo.x.pai.api.vo.biz.blog.req.BlogDetailReq;
import com.tigerobo.x.pai.api.vo.biz.blog.req.UserPageReq;
import com.tigerobo.x.pai.api.vo.user.follow.FollowVo;
import com.tigerobo.x.pai.biz.biz.blog.BlogOperateService;
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
@Api(value = "blog操作",  tags = "blog操作")
public class BlogOperateController {

    @Autowired
    private BlogOperateService blogOperateService;

    @ApiOperation(value = "blog-保存")
    @PostMapping(path = "saveBlog", consumes = "application/json", produces = "application/json")
    @Authorize
    public BlogVo saveBlog(@Valid @RequestBody BlogAddReq req) {
        return blogOperateService.addOrUpdateBlog(req);
    }
    @ApiOperation(value = "blog-上线")
    @PostMapping(path = "online", consumes = "application/json", produces = "application/json")
    @Authorize
    public ResultVO online(@Valid @RequestBody IdReqVo req) {
        blogOperateService.online(req.getId());
        return ResultVO.success();
    }
    @ApiOperation(value = "blog-下线")
    @PostMapping(path = "offline", consumes = "application/json", produces = "application/json")
    @Authorize
    public ResultVO saveBlog(@Valid @RequestBody IdReqVo req) {
        blogOperateService.offline(req.getId());
        return ResultVO.success();
    }
    @ApiOperation(value = "blog-删除")
    @PostMapping(path = "delete", consumes = "application/json", produces = "application/json")
    @Authorize
    public ResultVO delete(@Valid @RequestBody IdReqVo req) {
        blogOperateService.delete(req.getId());
        return ResultVO.success();
    }

}
