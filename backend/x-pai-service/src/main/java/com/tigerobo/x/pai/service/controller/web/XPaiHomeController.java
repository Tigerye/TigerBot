package com.tigerobo.x.pai.service.controller.web;

import com.tigerobo.x.pai.api.biz.service.SummaryService;
import com.tigerobo.x.pai.api.config.ConditionConfig;
import com.tigerobo.x.pai.api.vo.IndexVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description: 业务模块-前端服务-首页接口
 * @modified By:
 * @version: $
 */
@RestController
@RequestMapping("/web/")
@Api(value = "业务模块-前端服务-首页接口", position = 2900, tags = "业务模块-前端服务-首页接口")
//@Conditional(ConditionConfig.XPaiBizCondition.class)
public class XPaiHomeController extends XPaiWebController {

    @Autowired
    private SummaryService summaryService;

    @ApiOperation(value = "首页-指标", position = 1)
    @GetMapping(path = {"home/indices", "home-indices"})
    public IndexVo homeIndices() {
        return summaryService.homeIndices();
    }
}