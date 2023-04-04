package com.tigerobo.x.pai.biz.pay;

import com.github.pagehelper.Page;
import com.tigerobo.x.pai.api.enums.ProductType;
import com.tigerobo.x.pai.api.pay.dto.OrderDto;
import com.tigerobo.x.pai.api.pay.enums.OrderStatusEnum;
import com.tigerobo.x.pai.api.pay.enums.PayChannelTypeEnum;
import com.tigerobo.x.pai.api.pay.req.CreateOrderReq;
import com.tigerobo.x.pai.api.pay.req.UserPayOrderQueryReq;
import com.tigerobo.x.pai.api.pay.vo.CreateOrderVo;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.biz.biz.member.MemberService;
import com.tigerobo.x.pai.biz.cache.biz.MachineUtil;
import com.tigerobo.x.pai.biz.pay.convert.OrderConvert;
import com.tigerobo.x.pai.biz.pay.notify.PayNotifyHandleService;
import com.tigerobo.x.pai.biz.pay.payment.PaymentBillService;
import com.tigerobo.x.pai.biz.utils.IdGenerator;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.dal.pay.dao.OrderDao;
import com.tigerobo.x.pai.dal.pay.dao.SkuDao;
import com.tigerobo.x.pai.dal.pay.entity.OrderPo;
import com.tigerobo.x.pai.dal.pay.entity.PaymentApiBillPo;
import com.tigerobo.x.pai.dal.pay.entity.ProductSkuPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j
@Service
public class OrderService {

    @Autowired
    private OrderDao orderDao;
    @Autowired
    private SkuDao skuDao;
    @Autowired
    private MachineUtil machineUtil;

    @Autowired
    private PaymentBillService paymentBillService;

    @Autowired
    private PayNotifyHandleService payNotifyHandleService;
    public Long createBillOrder(PaymentApiBillPo billPo){
        Integer skuId = 3;
        ProductSkuPo sku = skuDao.load(skuId);
        Validate.isTrue(sku != null, "产品不存在");

        OrderPo orderPo = new OrderPo();
        Long orderNo = IdGenerator.getId(machineUtil.getMachineId());
        orderPo.setOrderNo(orderNo);
        orderPo.setUserId(billPo.getUserId());
        orderPo.setSkuId(skuId);
        orderPo.setAmount(1);
        orderPo.setName(sku.getName());
        orderPo.setTotalPrice(billPo.getTotalFee());
        orderPo.setProductId(sku.getProductId());
        orderPo.setStatus(1);

        final int add = orderDao.add(orderPo);
        Validate.isTrue(add==1&&orderPo.getId() != null && orderPo.getId() > 0, "创建订单失败");

        return orderNo;
    }

    public CreateOrderVo createOrder(CreateOrderReq req) {
        Validate.isTrue(req != null, "参数为空");
        Integer userId = ThreadLocalHolder.getUserId();
        Validate.isTrue(userId != null, "用户不存在");
        Integer skuId = req.getSkuId();
        ProductSkuPo sku = skuDao.load(skuId);
        Validate.isTrue(sku != null, "产品不存在");
        Validate.isTrue(sku.getPrice().compareTo(req.getPrice()) == 0, "产品价格已变更");

        OrderPo orderPo = new OrderPo();
        Long orderNo = IdGenerator.getId(machineUtil.getMachineId());
        orderPo.setOrderNo(orderNo);
        orderPo.setUserId(userId);
        orderPo.setSkuId(skuId);
        orderPo.setAmount(1);
        orderPo.setName(sku.getName());
        orderPo.setTotalPrice(sku.getPrice());
        orderPo.setProductId(sku.getProductId());
        orderPo.setStatus(1);

        orderDao.add(orderPo);
        Validate.isTrue(orderPo.getId() != null && orderPo.getId() > 0, "创建订单失败");
        CreateOrderVo orderVo = new CreateOrderVo();
        orderVo.setOrderNo(orderNo);
        orderVo.setPrice(sku.getPrice());
        orderVo.setProductName(sku.getName());
        return orderVo;
    }

    @Transactional(value = "paiTm")
    public void paySuccess(Long orderNo, String paymentNo, PayChannelTypeEnum payChannelTypeEnum){
        OrderPo orderPo = orderDao.load(orderNo);
        Validate.isTrue(orderPo!=null,"订单不存在");
        Integer status = orderPo.getStatus();

        if (OrderStatusEnum.HAS_PAY.getStatus().equals(status)){
            log.warn("orderNo:{},hasPay,paymentNo:{}",orderNo,paymentNo);
            return;
        }
        OrderPo update = new OrderPo();
        update.setStatus(OrderStatusEnum.HAS_PAY.getStatus());
        update.setPaymentNo(paymentNo);
        update.setPayChannel(payChannelTypeEnum.getType());
        update.setPayTime(new Date());
        int updateValue = orderDao.updateByStatusCondition(update, orderNo, status);

        if (updateValue<1){
            OrderPo load = orderDao.load(orderNo);
            if (OrderStatusEnum.HAS_PAY.getStatus().equals(load.getStatus())){
                return;
            }else {
                throw new IllegalArgumentException("订单更新失败");
            }
        }
        payNotifyHandleService.orderSuccessNotify(orderPo);
    }


    public OrderDto getOrderByOrderNo(Long orderNo) {
        return getOrderByOrderNo(orderNo, false);
    }

    public OrderDto getOrderByOrderNo(Long orderNo, boolean checkUserId) {
        OrderPo orderPo = orderDao.load(orderNo);
        if (orderPo == null) {
            return null;
        }
        if (checkUserId) {
            Integer userId = ThreadLocalHolder.getUserId();
            Validate.isTrue(orderPo.getUserId().equals(userId), "用户没有查看订单权限");
        }
        return OrderConvert.po2dto(orderPo);
    }


}
