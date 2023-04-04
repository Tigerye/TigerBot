package com.tigerobo.x.pai.service.controller.search;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tigerobo.pai.search.api.dto.PaiIndexPropertyDto;
import com.tigerobo.pai.search.api.dto.PropertyShowDto;
import com.tigerobo.pai.search.api.enums.EsTypePropertyMap;
import com.tigerobo.pai.search.api.enums.EsTypeSupportEnum;
import com.tigerobo.pai.search.api.enums.PropertyHandlerEnum;
import com.tigerobo.pai.search.api.req.base.IdUserReq;
import com.tigerobo.pai.search.api.req.property.PropertyCommitReq;
import com.tigerobo.pai.search.api.req.property.PropertyShowReq;
import com.tigerobo.x.pai.api.auth.aspect.Authorize;
import com.tigerobo.x.pai.biz.micro.search.SearchIndexPropertyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/indexProperty/")
@Api(value = "索引-属性", tags = "索引-属性")
public class IndexPropertyController {

    @Autowired
    private SearchIndexPropertyService searchIndexPropertyService;


    @ApiOperation(value = "索引-属性列表", tags = "索引-属性列表")
    @PostMapping(path = "getIndexProperties",consumes = "application/json", produces = "application/json")
    public List<PaiIndexPropertyDto> getIndexProperties(@RequestBody IdUserReq idUserReq){

        return searchIndexPropertyService.getIndexProperties(idUserReq);

    }


    @ApiOperation(value = "提交属性，重建索引", tags = "提交属性，重建索引")
    @Authorize
    @PostMapping(path = "commitProperties",consumes = "application/json", produces = "application/json")
    public JSONObject commitProperties(@RequestBody PropertyCommitReq propertyCommitReq){
        return searchIndexPropertyService.commitProperties(propertyCommitReq);
    }

    @ApiOperation(value = "获取展示属性列表", tags = "获取展示属性列表")
    @PostMapping(path = "getIndexShowProperties",consumes = "application/json", produces = "application/json")
    public List<PaiIndexPropertyDto> getShowProperties(@RequestBody IdUserReq idUserReq){
        return searchIndexPropertyService.getShowProperties(idUserReq);
    }

    @ApiOperation(value = "设置展示属性", tags = "设置展示属性")
    @Authorize
    @PostMapping(path = "commitShowProperties",consumes = "application/json", produces = "application/json")
    public void commitShowProperties(@RequestBody PropertyShowReq propertyShowReq){
        searchIndexPropertyService.commitShowProperties(propertyShowReq);
    }
    @ApiOperation(value = "索引-支持属性类型列表", tags = "索引-支持属性类型列表")
    @PostMapping(path = "getSupportDataTypeList",produces = "application/json")
    public List getSupportDataTypeList() {
        return searchIndexPropertyService.getSupportDataTypeList();
    }
//
//    @ApiOperation(value = "索引-属性-处理器列表", tags = "索引-属性-处理器列表")
//    @PostMapping(path = "getHandlerList",produces = "application/json")
//    public List<HandlerType> getHandlerList() {
//        List<HandlerType> list = new ArrayList<>();
//        for (PropertyHandlerEnum value : PropertyHandlerEnum.values()) {
//            list.add(new HandlerType(value.toString().toLowerCase(),value.getText()));
//        }
//        return list;
//    }

}
