package com.tigerobo.x.pai.service.controller.pay;

import com.tigerobo.x.pai.api.auth.aspect.Authorize;
import com.tigerobo.x.pai.api.pay.dto.OrderDto;
import com.tigerobo.x.pai.api.pay.req.CreateOrderReq;
import com.tigerobo.x.pai.api.pay.req.OrderViewReq;
import com.tigerobo.x.pai.api.pay.req.UserPayOrderQueryReq;
import com.tigerobo.x.pai.api.pay.vo.CreateOrderVo;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.biz.pay.OrderQueryService;
import com.tigerobo.x.pai.biz.pay.OrderService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Api(value = "order", description = "订单服务")
@Slf4j
@RestController
@RequestMapping(value = "/order/")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderQueryService orderQueryService;

    @ApiOperation(value = "创建订单")
    @RequestMapping(value = "/createOrder", method = POST)
    @Authorize
    public CreateOrderVo createOrder(@Valid @RequestBody CreateOrderReq req) {
        return orderService.createOrder(req);
    }


    @ApiOperation(value = "查看订单")
    @RequestMapping(value = "/viewOrder", method = POST)
    @Authorize
    public OrderDto viewOrder(@Valid @RequestBody OrderViewReq req) {
        return orderService.getOrderByOrderNo(req.getOrderNo(), true);
    }


    @ApiOperation(value = "查看已购买订单列表")
    @RequestMapping(value = "/getUserPurchaseList", method = POST)
    @Authorize
    public PageVo<OrderDto> getUserPurchaseList(@Valid @RequestBody UserPayOrderQueryReq req) {

        Integer userId = ThreadLocalHolder.getUserId();
        return orderQueryService.getUserPayOrderList(userId,req);
    }


}