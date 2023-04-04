package com.tigerobo.x.pai.biz.pay.notify;

import com.algolet.pay.api.dto.AlgCoinAddDto;
import com.algolet.pay.api.enums.AlgCoinBusinessType;
import com.algolet.pay.biz.service.AlgCoinService;
import com.alibaba.fastjson.JSONObject;
import com.tigerobo.x.pai.api.enums.ProductType;
import com.tigerobo.x.pai.api.product.dto.ProductSkuDto;
import com.tigerobo.x.pai.biz.biz.member.MemberService;
import com.tigerobo.x.pai.biz.pay.payment.PaymentBillService;
import com.tigerobo.x.pai.biz.product.convert.SkuConvert;
import com.tigerobo.x.pai.dal.pay.dao.SkuDao;
import com.tigerobo.x.pai.dal.pay.entity.OrderPo;
import com.tigerobo.x.pai.dal.pay.entity.ProductSkuPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Map;

@Slf4j
@Service
public class PayNotifyHandleService {

    @Autowired
    private SkuDao skuDao;
    @Autowired
    private MemberService memberService;
    @Autowired
    private PaymentBillService paymentBillService;

    @Autowired
    private AlgCoinService algCoinService;

    public void orderSuccessNotify(OrderPo orderPo){

        Integer skuId = orderPo.getSkuId();

        ProductSkuPo sku = skuDao.load(skuId);

        if (sku == null){
            log.error("sku:{},不存在",skuId);
            return;
        }
        final ProductSkuDto skuDto = SkuConvert.convert(sku);
        final ProductType productType = ProductType.getByType(sku.getProductId());
        if (productType == null){
            return;
        }
        if (ProductType.MEMBER == productType){
            memberService.addMember(orderPo.getUserId(),sku,orderPo.getOrderNo());
        }else if (ProductType.ALG_COIN == productType){
            final Map<String, Object> skuProperties = skuDto.getSkuProperties();
            if (CollectionUtils.isEmpty(skuProperties)){
                return;
            }

            final JSONObject jsonObject = new JSONObject(skuProperties);
            final Integer coinNum = jsonObject.getInteger("coinNum");
            if (coinNum == null||coinNum == 0){
                return;
            }
            final Integer userId = orderPo.getUserId();

            AlgCoinAddDto addReq = AlgCoinAddDto.builder()
                    .userId(userId)
                    .num(coinNum)
                    .bizType(AlgCoinBusinessType.BUY)
                    .refId(String.valueOf(orderPo.getOrderNo()))
                    .build();

            algCoinService.addBusiness(addReq);
        }else if (ProductType.MODEL_API == productType){
            paymentBillService.paySuccess(String.valueOf(orderPo.getOrderNo()));
        }

    }
}
