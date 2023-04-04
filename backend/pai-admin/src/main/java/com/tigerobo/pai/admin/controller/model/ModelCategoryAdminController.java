package com.tigerobo.pai.admin.controller.model;

import com.tigerobo.x.pai.api.dto.model.ModelCategoryDto;
import com.tigerobo.x.pai.biz.biz.service.WebModelService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/model/category/")
public class ModelCategoryAdminController {

    @Autowired
    private WebModelService webModelService;

    @ApiOperation(value = "模型类型列表")
    @PostMapping(path = {"list"}, produces = "application/json")
    public List<ModelCategoryDto> modelSidebar() {

        return webModelService.getBasicTags();
    }
}
