package com.tigerobo.x.pai.service.controller.blog;

import com.tigerobo.x.pai.api.dto.blog.BlogCategoryDto;
import com.tigerobo.x.pai.api.vo.IdReqVo;
import com.tigerobo.x.pai.api.vo.IdVo;
import com.tigerobo.x.pai.biz.biz.blog.BlogCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/blog/category/")
@Api(value = "blog分类", tags = "blog分类")
public class SiteCategoryController{

    @Autowired
    private BlogCategoryService blogCategoryService;


    @ApiOperation(value = "获取blog分类标签列表")
    @PostMapping(path = {"getCategoryList"}, produces = "application/json")
    public List<BlogCategoryDto> getCategoryList() {
        return blogCategoryService.getAllShow();
    }

    @ApiOperation(value = "获取blog分类明细")
    @PostMapping(path = {"getCategoryDetail"}, produces = "application/json")
    public BlogCategoryDto getCategoryList(@RequestBody IdReqVo idVo) {
        return blogCategoryService.getDetail(idVo.getId());
    }

}
