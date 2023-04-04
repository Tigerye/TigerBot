package com.tigerobo.x.pai.service.controller.pay;

import com.alibaba.fastjson.JSON;
import com.tigerobo.x.pai.api.auth.aspect.Authorize;
import com.tigerobo.x.pai.api.pay.req.WeChatPayReq;
import com.tigerobo.x.pai.api.vo.ResultVO;
import com.tigerobo.x.pai.biz.pay.WechatPayService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author: xjx
 * @date: 2020/5/7 5:20 PM
 */
@Slf4j
@RestController
@RequestMapping(value =  "/pay/wechat/")
@Api(value = "pay", description = "微信支付")
public class WechatPayController {

    @Autowired
    private WechatPayService wechatPayService;

    @Authorize
    @ApiOperation(value = "微信扫码支付二维码")
    @RequestMapping(value = "/qrCodePay", method = POST)
    public ResultVO qrCodePay(HttpServletRequest request, @Valid @RequestBody WeChatPayReq req) {

        Integer userId = ThreadLocalHolder.getUserId();

        log.info("微信扫码支付:userId:{},req:{}", userId,JSON.toJSONString(req));
        ResultVO resultVO = new ResultVO();
        try {
            String codeUrl = wechatPayService.preOrderPayQr(req.getOrderNo());
            Map<String,Object> map = new HashMap<>();
            map.put("codeUrl",codeUrl);

            resultVO.setData(map);

        }catch (IllegalArgumentException ex){
            log.error("arg:QrCodePay:,order:{},e:{}",JSON.toJSONString(req),ex.getMessage());
            resultVO = ResultVO.fail(ex.getMessage());
        }catch (Exception ex){
            log.error("err:QrCodePay:,order:{},e:{}",JSON.toJSONString(req),ex.getMessage());
            resultVO = ResultVO.fail("生成付款码失败");
        }
        return resultVO;
    }

//
//    @ApiOperation(value = "微信查询订单")
//    @RequestMapping(value = "/OrderQuery", method = POST)
//    public Response<WeChatPayResponse> orderQuery(HttpServletRequest request, @Valid @RequestBody OrderQueryVo orderQueryVo) {
//
//        log.info("微信查询订单:{}", JacksonUtil.bean2Json(orderQueryVo));
//
//        try {
//            OrderQueryDto orderQueryDto = OrderQueryDto.builder()
//                    .out_trade_no(orderQueryVo.getOutTradeNo())
//                    .transaction_id(orderQueryVo.getTransactionId())
//                    .build();
//
//            WeChatPayResponse result = wechatPayService.queryOrder(orderQueryDto);
//            if(result != null) {
//                return Response.SUCCESS(result);
//            }
//        } catch (Exception e) {
//            log.error("微信公众号支付 error", e);
//        }
//
//        return Response.FAIL(ResCode.QUERY_ORDER_ERROR);
//    }

}
