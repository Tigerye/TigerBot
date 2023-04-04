package com.tigerobo.x.pai.service.controller.basket;

import com.tigerobo.x.pai.api.basket.BasketModelCallReportReq;
import com.tigerobo.x.pai.api.basket.BasketModelCallTotalCommitReq;
import com.tigerobo.x.pai.api.vo.ResultVO;
import com.tigerobo.x.pai.biz.basket.BasketModelCallService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/basket/call")
@Api(value = "模型调用数量反馈", tags = "模型调用数量反馈")
public class BasketModelCallController {

    @Autowired
    BasketModelCallService basketModelCallService;

    @ApiOperation(value = "模型调用数量反馈")
    @PostMapping(path = "collect", produces = "application/json")
    public ResultVO collect(HttpServletRequest request, @Valid @RequestBody BasketModelCallReportReq req) {
        basketModelCallService.add(req);
        return ResultVO.success();
    }

    @ApiOperation(value = "模型调用总量数量反馈")
    @PostMapping(path = "commitTotal", produces = "application/json")
    public ResultVO commitTotal(HttpServletRequest request, @Valid @RequestBody BasketModelCallTotalCommitReq req) {
        basketModelCallService.addTotalNum(req);
        return ResultVO.success();
    }

}
