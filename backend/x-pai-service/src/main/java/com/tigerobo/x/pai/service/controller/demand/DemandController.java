package com.tigerobo.x.pai.service.controller.demand;

import com.github.pagehelper.PageInfo;
import com.tigerobo.x.pai.api.vo.biz.DemandVo;
import com.tigerobo.x.pai.api.config.ConditionConfig;
import com.tigerobo.x.pai.api.vo.QueryVo;
import com.tigerobo.x.pai.biz.biz.service.DemandServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description: 业务模块-业务需求接口
 * @modified By:
 * @version: $
 */
@RestController
@RequestMapping("/demand")
@Api(value = "业务模块-业务需求接口", position = 2100, tags = "业务模块-业务需求接口")
//@Conditional(ConditionConfig.XPaiBizCondition.class)
public class DemandController{// extends GeneralController<DemandService, DemandReqVo, DemandVo> {

//    @Autowired
//    private WebDemandService webDemandService;
    @Autowired
    private DemandServiceImpl demandServiceV2;

//    @Override
    @ApiOperation(value = "查询-top", position = 53)
    @PostMapping(path = "/query", consumes = "application/json", produces = "application/json")
    public PageInfo<DemandVo> query(@Valid @RequestBody QueryVo queryVo) {

        return demandServiceV2.getTopList();
    }
}
