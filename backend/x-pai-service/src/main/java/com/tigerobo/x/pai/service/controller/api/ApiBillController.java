package com.tigerobo.x.pai.service.controller.api;

import com.tigerobo.x.pai.api.pay.req.ApiAgreementReq;
import com.tigerobo.x.pai.api.pay.vo.api.ApiBillDetailStatisticVo;
import com.tigerobo.x.pai.api.pay.vo.api.ApiBillVo;
import com.tigerobo.x.pai.api.vo.ResultVO;
import com.tigerobo.x.pai.api.vo.api.ApiBillReq;
import com.tigerobo.x.pai.api.vo.biz.mine.UserIdVo;
import com.tigerobo.x.pai.biz.pay.api.ApiAgreementService;
import com.tigerobo.x.pai.biz.pay.payment.PaymentBillService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/bill")
@Api(value = "api账单", position = 1, tags = "api账单")
public class ApiBillController {



    @Autowired
    private PaymentBillService paymentBillService;
    @ApiOperation(value = "获取用户账单列表", position = 30)
    @PostMapping(path = "/getUserApiBillList", consumes = "application/json", produces = "application/json")
    public List<ApiBillVo> getUserApiBillList(@Valid @RequestBody UserIdVo req) {
        return paymentBillService.getUserBillList(req.getUserId());
    }
    @ApiOperation(value = "获取用户当月账单列表", position = 30)
    @PostMapping(path = "/getUserCurrentApiBillList", consumes = "application/json", produces = "application/json")
    public ApiBillVo getUserCurrentApiBillList(@Valid @RequestBody UserIdVo req) {
        return paymentBillService.getUserCurrentMonthBill(req.getUserId());
    }
    @ApiOperation(value = "获取用户前一个月账单列表", position = 30)
    @PostMapping(path = "/getUserPreApiBillList", consumes = "application/json", produces = "application/json")
    public ApiBillVo getUserPreApiBillList(@Valid @RequestBody UserIdVo req) {
        return paymentBillService.getUserPreApiBillList(req.getUserId());
    }


    @ApiOperation(value = "获取账单统计明细", position = 30)
    @PostMapping(path = "/getBillDetail", consumes = "application/json", produces = "application/json")
    public ApiBillDetailStatisticVo getBillDetail(@Valid @RequestBody ApiBillReq req) {
        return paymentBillService.getBillStatistic(req.getUserId(), req.getMonth());
    }


}
