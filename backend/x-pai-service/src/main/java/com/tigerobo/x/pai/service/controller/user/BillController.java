package com.tigerobo.x.pai.service.controller.user;


import com.tigerobo.x.pai.api.auth.aspect.Authorize;
import com.tigerobo.x.pai.api.vo.biz.bill.UserBillReq;
import com.tigerobo.x.pai.api.vo.biz.bill.UserCallBillVo;
import com.tigerobo.x.pai.biz.biz.BillService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bill/")
@Api(value = "账单", position = 2900, tags = "账单")
public class BillController {


    @Autowired
    private BillService billService;

    @ApiOperation(value = "用户模型账单")
    @PostMapping(path = {"getUserModelBill"}, consumes = "application/json", produces = "application/json")
    @Authorize
    public UserCallBillVo getUserModelBill(@RequestBody UserBillReq req) {
        return billService.getUserBill(req);
    }



}
