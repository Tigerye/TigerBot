package com.tigerobo.x.pai.service.controller.aml;


import com.github.pagehelper.PageInfo;
import com.tigerobo.x.pai.api.aml.dto.evaluate.EvaSentence;
import com.tigerobo.x.pai.api.aml.dto.evaluate.EvaluationConfidenceDto;
import com.tigerobo.x.pai.api.aml.engine.dto.train.TrainResultDto;
import com.tigerobo.x.pai.api.aml.req.AmlConfidenceEvaluationReq;
import com.tigerobo.x.pai.api.auth.aspect.Authorize;
import com.tigerobo.x.pai.api.auth.service.UserService;
import com.tigerobo.x.pai.api.exception.AuthorizeException;
import com.tigerobo.x.pai.api.exception.ResultCode;
import com.tigerobo.x.pai.api.vo.IdVo;
import com.tigerobo.x.pai.biz.aml.service.AmlViewServiceImpl;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/aml/evaluate/")
@Api(value = "自主训练评测接口", position = 5400, tags = "自主训练流-评测接口")
public class AmlEvaluateController {

    @Autowired
    private AmlViewServiceImpl amlViewService;

    @Autowired
    private UserService userService;
    @ApiOperation(value = "评测-统计")
    @PostMapping(path = "/evaluate_statistic", consumes = "application/json", produces = "application/json")
    @Authorize
    public List<TrainResultDto.LabelItem> evaluate_statistic(@RequestBody IdVo req) {

        Integer userId = ThreadLocalHolder.getUserId();
        if (userId == null){
            throw new AuthorizeException(ResultCode.USER_NOT_LOGIN, "not Login!", null);
        }
        List<TrainResultDto.LabelItem> labelItems = amlViewService.viewStatisticEvaluation(req.getId(), userId);

        return labelItems;
    }

    @ApiOperation(value = "评测-置信度")
    @PostMapping(path = "/evaluate_confidence", consumes = "application/json", produces = "application/json")
    @Authorize
    public EvaluationConfidenceDto evaluate_confidence(@RequestBody AmlConfidenceEvaluationReq req) {
        Integer userId = ThreadLocalHolder.getUserId();
        if (userId == null){
            throw new AuthorizeException(ResultCode.USER_NOT_LOGIN, "not Login!", null);
        }
        EvaluationConfidenceDto confidenceDto = amlViewService.viewConfidenceEvaluation(req, userId);
        return confidenceDto;
    }


    @ApiOperation(value = "评测-预测标签数据类型分页")
    @PostMapping(path = "/evaluate_data_type_page", consumes = "application/json", produces = "application/json")
    @Authorize
    public PageInfo<EvaSentence> evaluate_data_type_page(@RequestBody AmlConfidenceEvaluationReq req) {
        Integer userId = ThreadLocalHolder.getUserId();
        if (userId == null){
            throw new AuthorizeException(ResultCode.USER_NOT_LOGIN, "not Login!", null);
        }
        PageInfo<EvaSentence> pageInfo = amlViewService.viewEvaluationTypePage(req, userId);
        if (pageInfo == null){
            return new PageInfo<EvaSentence>();
        }
        return pageInfo;
    }



    @ApiOperation(value = "评测-预测标签数据类型下载")
    @PostMapping(path = "/download_evaluate_data_type", consumes = "application/json", produces = "application/json")
    @Authorize
    public Map<String,Object> download_evaluate_data_type(@RequestBody AmlConfidenceEvaluationReq req) {
        Integer userId = ThreadLocalHolder.getUserId();
        if (userId == null){
            throw new AuthorizeException(ResultCode.USER_NOT_LOGIN, "not Login!", null);
        }
        String s = amlViewService.downloadEvaluationType(req, userId);

        Map<String,Object> data = new HashMap<>();

        if (!StringUtils.isEmpty(s)){
            data.put("url",s);
            return data;
        }
        return null;
    }
}
