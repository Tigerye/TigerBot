package com.tigerobo.x.pai.service.controller.api;

import com.tigerobo.x.pai.api.pay.req.ApiAgreementReq;
import com.tigerobo.x.pai.api.vo.ResultVO;
import com.tigerobo.x.pai.api.vo.api.AgreementVo;
import com.tigerobo.x.pai.api.vo.biz.mine.UserIdVo;
import com.tigerobo.x.pai.biz.pay.api.ApiAgreementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description: 服务模块-API请求接口
 * @modified By:
 * @version: $
 */
@RestController
@RequestMapping("/api/agreement")
@Api(value = "api协议", position = 1, tags = "api协议")
public class ApiAgreementController {

    @Autowired
    private ApiAgreementService apiAgreementService;

    @ApiOperation(value = "添加api协议", position = 30)
    @PostMapping(path = "/addAgreement", consumes = "application/json", produces = "application/json")
    public AgreementVo addAgreement(@Valid @RequestBody ApiAgreementReq apiReqVo) {
        return apiAgreementService.addAgreement(apiReqVo);
    }

    @ApiOperation(value = "查看用户协议列表", position = 30)
    @PostMapping(path = "/viewAgreements", consumes = "application/json", produces = "application/json")
    public List<AgreementVo> viewAgreements(@Valid @RequestBody UserIdVo req) {

        final List<AgreementVo> agreementVos = apiAgreementService.viewAgreements(req.getUserId());
        return agreementVos;
    }

}
