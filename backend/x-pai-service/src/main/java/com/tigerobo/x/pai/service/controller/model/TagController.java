package com.tigerobo.x.pai.service.controller.model;

import com.tigerobo.x.pai.api.dto.model.ModelCategoryDto;
import com.tigerobo.x.pai.biz.biz.service.WebModelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tag")
@Api(value = "标签接口", position = 2900, tags = "标签接口")
public class TagController {

    @Autowired
    private WebModelService webModelService;

    @ApiOperation(value = "模型/类型")
    @GetMapping(path ="getModelTags")
    public List<ModelCategoryDto> modelSidebar(@RequestParam("type") Integer type) {
        return webModelService.getBasicTags(type);
    }

}
