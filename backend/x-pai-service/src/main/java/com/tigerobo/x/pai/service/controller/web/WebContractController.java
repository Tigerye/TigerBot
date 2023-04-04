package com.tigerobo.x.pai.service.controller.web;

import com.tigerobo.x.pai.api.auth.aspect.Authorize;
import com.tigerobo.x.pai.api.vo.biz.contract.ContractDraftVo;
import com.tigerobo.x.pai.api.vo.biz.req.DemandContractSampleReq;
import com.tigerobo.x.pai.api.vo.biz.req.DemandSignContractReq;
import com.tigerobo.x.pai.api.vo.ResultVO;
import com.tigerobo.x.pai.biz.biz.service.DemandContractService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/web/contract/")
@Api(value = "合同",tags = "需求合同")
public class WebContractController {

    @Autowired
    private DemandContractService demandContractService;
    @ApiOperation(value = "生成合同")
    @PostMapping(path = {"generate"}, consumes = "application/json", produces = "application/json")
    @Authorize
    public ContractDraftVo generate(@RequestBody DemandSignContractReq req){
        return demandContractService.generateDemandContractDraft(req);
    }
    @ApiOperation(value = "生成线下合同")
    @PostMapping(path = {"generateOffLine"}, consumes = "application/json", produces = "application/json")
    @Authorize
    public ContractDraftVo generateOffLine(@RequestBody DemandSignContractReq req){
        return demandContractService.generateDemandContractOffline(req);
    }
    @ApiOperation(value = "查看需求合同")
    @PostMapping(path = {"view"}, consumes = "application/json", produces = "application/json")
    @Authorize
    public ContractDraftVo view(@RequestBody DemandSignContractReq req){
        return demandContractService.view(req);
    }


    @ApiOperation(value = "上传需求合同样例")
    @PostMapping(path = {"uploadSample"}, consumes = "application/json", produces = "application/json")
    @Authorize
    public ResultVO uploadSample(@RequestBody DemandContractSampleReq req){
        demandContractService.uploadDemandContractSample(req);
        return ResultVO.success();
    }
//
//
//    @ApiOperation(value = "view", position = 2100)
//    @PostMapping(path = {"view"}, consumes = "application/json", produces = "application/json")
//    @Authorize
//    public ContractDraftVo view(@RequestBody IdVo req){
//        return demandContractService.generateDemandContractDraft(req);
//    }


}
