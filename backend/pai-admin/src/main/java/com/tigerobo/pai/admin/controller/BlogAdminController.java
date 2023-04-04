package com.tigerobo.pai.admin.controller;

import com.tigerobo.x.pai.api.admin.BlogAdminVo;
import com.tigerobo.x.pai.api.admin.auth.AdminAuthorize;
import com.tigerobo.x.pai.api.admin.req.AdminOnlineStatusReq;
import com.tigerobo.x.pai.api.admin.req.BlogAdminQueryReq;
import com.tigerobo.x.pai.api.admin.req.blog.BlogTagReq;
import com.tigerobo.x.pai.api.admin.req.blog.BlogRecommendReq;
import com.tigerobo.x.pai.api.dto.blog.BlogCategoryDto;
import com.tigerobo.x.pai.api.vo.IdReqVo;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.ResultVO;
import com.tigerobo.x.pai.biz.admin.BlogAdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/blog/")
@Api(value = "blog", tags = "blog")

public class BlogAdminController {
    @Autowired
    private BlogAdminService blogAdminService;

    @AdminAuthorize
    @ApiOperation(value = "后台blog-列表")
    @PostMapping(path = {"list"}, consumes = "application/json", produces = "application/json")
    public PageVo<BlogAdminVo> blogList(@Valid @RequestBody BlogAdminQueryReq requestVo) {
        return blogAdminService.getPageList(requestVo);
    }


    @AdminAuthorize
    @ApiOperation(value = "blog上下线")
    @PostMapping(path = {"online"}, consumes = "application/json", produces = "application/json")
    public ResultVO blogOnline(@Valid @RequestBody AdminOnlineStatusReq req) {
        blogAdminService.opeOnlineStatus(req);
        return ResultVO.success();

    }

    @AdminAuthorize
    @ApiOperation(value = "blog删除")
    @PostMapping(path = {"delete"}, consumes = "application/json", produces = "application/json")
    public ResultVO blogDel(@Valid @RequestBody IdReqVo reqVo) {
        blogAdminService.blogDelete(reqVo.getId());
        return ResultVO.success();

    }

    @AdminAuthorize
    @ApiOperation(value = "blogCategory")
    @PostMapping(path = {"category"}, produces = "application/json")
    public List<BlogCategoryDto> categoryList() {
        return blogAdminService.categoryQuery();
    }


    @AdminAuthorize
    @ApiOperation(value = "推荐")
    @PostMapping(path = {"updateRecommend"}, produces = "application/json")
    public ResultVO updateRecommend(@RequestBody BlogRecommendReq req) {
        blogAdminService.updateRecommend(req);
        return ResultVO.success();
    }


    @AdminAuthorize
    @ApiOperation(value = "更新标签")
    @PostMapping(path = {"updateTag"}, produces = "application/json")
    public ResultVO updateTag(@RequestBody BlogTagReq req) {
        blogAdminService.updateTag(req);
        return ResultVO.success();
    }


}
