package com.tigerobo.x.pai.service.controller.ai;

import com.tigerobo.x.pai.api.dto.ai.ArtModifierModel;
import com.tigerobo.x.pai.biz.ai.art.image.AiParamDictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Api(value = "ai字典")
@Slf4j
@RestController
@RequestMapping(value = "/ai/dict/")
public class AiParamDictController {

    @Autowired

    private AiParamDictService aiParamDictService;


    @ApiOperation(value = "按类型查看modifier列表")
    @RequestMapping(value = "/getModifierListByType", method = POST)
    public ArtModifierModel getModifierListByType(@RequestBody ClassTypeReq classTypeReq) {
        return aiParamDictService.getByClassType(classTypeReq.getClassType());
    }

    @ApiOperation(value = "查看modifier类型列表")
    @RequestMapping(value = "/getModifierClassTypes", method = {POST,GET})
    public List<ArtModifierModel> getModifierClassTypes() {
        return aiParamDictService.getClassTypeDicts();
    }

    @Data
    private static class ClassTypeReq{
        String classType;
    }

}
