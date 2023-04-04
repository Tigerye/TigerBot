package com.tigerobo.x.pai.service.controller.demand;

import com.google.common.collect.Lists;
import com.tigerobo.x.pai.api.biz.entity.Tag;
import com.tigerobo.x.pai.api.vo.QueryVo;
import com.tigerobo.x.pai.biz.biz.service.TagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description: 业务模块-标签服务接口
 * @modified By:
 * @version: $
 */
@RestController
@RequestMapping("/tagging")
@Api(value = "业务模块-标签服务接口", position = 2000, tags = "业务模块-标签服务接口")
//@Conditional(ConditionConfig.XPaiBizCondition.class)
public class TaggingController{//} extends GeneralController<TaggingService, TaggingReqVo, TaggingVo> {
//    @Autowired
//    private TagFactory tagFactory;
    @Autowired
    private TagService tagService;

    @ApiOperation(value = "标签列表", position = 100)
    @PostMapping(path = "/tag-list", consumes = "application/json", produces = "application/json")
    public List<Tag> tagList(@NotNull @RequestBody QueryVo queryVo) {
        List<Tag> tagList = Lists.newArrayList();
        if (queryVo.hasValue("type")) {
            for (Tag.Type type : queryVo.get("type", Tag.Type.class)){
                tagList.addAll(this.tagService.get(type));
            }
        }
        return tagList;
    }

    @ApiOperation(value = "标签详情", position = 110)
    @PostMapping(path = "/tag", consumes = "application/json", produces = "application/json")
    public Tag tagDetail(@NotNull @RequestBody QueryVo queryVo) {
        Tag.Type type = queryVo.getOrDefault("type", Tag.Type.class, null);
        String uid = queryVo.getOrDefault("uid", String.class, null);
        return this.tagService.get(uid);
    }
}
