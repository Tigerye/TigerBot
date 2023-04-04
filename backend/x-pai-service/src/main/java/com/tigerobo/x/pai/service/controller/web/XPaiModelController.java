package com.tigerobo.x.pai.service.controller.web;

import com.tigerobo.x.pai.api.biz.entity.Model;
import com.tigerobo.x.pai.api.biz.entity.Tag;
import com.tigerobo.x.pai.api.dto.model.ModelCategoryDto;
import com.tigerobo.x.pai.api.vo.biz.ModelApiReq;
import com.tigerobo.x.pai.api.vo.biz.ModelQueryVo;
import com.tigerobo.x.pai.api.vo.biz.ModelVo;
import com.tigerobo.x.pai.api.config.ConditionConfig;
import com.tigerobo.x.pai.api.exception.APIException;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.ResultVO;
import com.tigerobo.x.pai.api.vo.WebRepVo;
import com.tigerobo.x.pai.biz.biz.service.WebModelService;
import com.tigerobo.x.pai.biz.com.aspect.ViewRecord;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.tigerobo.x.pai.api.exception.ResultCode.PAGE_NOT_EXIST;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description: 业务模块-前端服务-模型接口
 * @modified By:
 * @version: $
 */
@RestController
@RequestMapping("/web/")
@Api(value = "业务模块-前端服务-模型接口", position = 2900, tags = "业务模块-前端服务-模型接口")
//@Conditional(ConditionConfig.XPaiBizCondition.class)
public class XPaiModelController extends XPaiWebController {
    @Autowired
    private WebModelService webModelService;


    @Deprecated
    @ApiOperation(value = "模型/项目-侧边栏")
    @GetMapping(path = {"model-sidebar","model/sidebar"})
    public List<ModelCategoryDto> modelSidebar() {

        return webModelService.getBasicTags();
    }

//
//    @Deprecated
//    @ApiOperation(value = "模型/项目-列表")
//    @PostMapping(path = {"model-list","model/list"}, consumes = "application/json", produces = "application/json")
//    public PageVo<ModelVo> modelList(@Valid @RequestBody ModelQueryVo requestVo) {
//        PageVo<ModelVo> query = webModelService.query(requestVo);
//        return query;
//    }

//
//    @ApiOperation(value = "模型-详情")
//    @PostMapping(path = {"model/detail"}, consumes = "application/json", produces = "application/json")
//    @ViewRecord
//    public ModelVo modelDetail(@Valid @RequestBody WebRepVo requestVo) {
//        // TODO 模型 获取
//        Model model = requestVo.getOrDefault("model", Model.class, null);
//        if (model == null|| StringUtils.isEmpty(model.getUuid())){
//            throw new APIException(PAGE_NOT_EXIST, "参数为空", null);
//        }
//
////        // TODO 页面信息获取
////        ModelVo modelVo = this.modelService.build(model.getId());
////        // TODO 页面角色定义
////        Authorization auth = this.authorizeService.<Model>authorize(requestVo.getAuthorization(), modelVo.getModel());
////        modelVo.setRole(auth.getRole());
//
//        return webModelService.getDetail(model.getUuid());
//    }
//

    @ApiOperation(value = "现在手动-任务-领取", position = 2250)
    @PostMapping(path = {"model/complete"}, consumes = "application/json", produces = "application/json")
    public ResultVO complete(@Valid @RequestBody ModelApiReq requestVo) {
        webModelService.completeModel(requestVo);
        return ResultVO.success();
    }

}