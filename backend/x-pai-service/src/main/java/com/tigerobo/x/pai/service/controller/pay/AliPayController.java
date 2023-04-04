package com.tigerobo.x.pai.service.controller.pay;

import com.alibaba.fastjson.JSON;
import com.tigerobo.x.pai.api.auth.aspect.Authorize;
import com.tigerobo.x.pai.api.pay.req.AliPayReq;
import com.tigerobo.x.pai.api.pay.req.WeChatPayReq;
import com.tigerobo.x.pai.api.vo.ResultVO;
import com.tigerobo.x.pai.biz.pay.AliPayService;
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

@Slf4j
@RestController
@RequestMapping(value = "/pay/ali/")
@Api(value = "ali-pay", tags = "支付宝回调通知接口")
public class AliPayController {

    @Autowired
    private AliPayService aliPayService;



    @Authorize
    @ApiOperation(value = "支付宝扫码支付二维码")
    @RequestMapping(value = "/qrCodePay", method = POST)
    public ResultVO qrCodePay(HttpServletRequest request, @Valid @RequestBody AliPayReq req) {

        Integer userId = ThreadLocalHolder.getUserId();

        log.info("支付宝扫码支付二维码:userId:{},req:{}", userId,JSON.toJSONString(req));
        ResultVO resultVO = new ResultVO();
        try {
            String codeUrl = aliPayService.qrCodePay(req.getOrderNo(),req.getCallbackUrl());
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


    @ApiOperation(value = "支付宝回调通知")
    @RequestMapping(value = "/notify", method = POST)
    public String notify(HttpServletRequest request) {

        log.info("ali-pay-notify-start");
        try {
            Map<String, String[]> parameterMap = request.getParameterMap();

            log.info("支付宝回调通知-AliPay-Notify:{}", JSON.toJSONString(parameterMap));
            aliPayService.callBackNotify(parameterMap);
        }catch (Exception ex){
            log.error("ali-pay-notify:err:",ex);
            return "fail";
        }
        return "success";
    }

}
