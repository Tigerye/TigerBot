package com.tigerobo.x.pai.service.controller.pay;

import com.alibaba.fastjson.JSON;
import com.dianping.cat.Cat;
import com.tigerobo.x.pai.biz.pay.OrderService;
import com.tigerobo.x.pai.biz.pay.PayNotifyService;
import com.tigerobo.x.pai.biz.pay.WechatPayService;
import com.tigerobo.x.pai.biz.pay.util.WXPayUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Slf4j
@RestController
@RequestMapping(value = "/pay/wechat/")
@Api(value = "wechat-pay", tags = "微信支付通知接口")
public class WechatPayNotifyController {


    @Autowired
    private PayNotifyService payNotifyService;

    @ApiOperation(value = "微信支付通知")
    @RequestMapping(value = "/notify", method = POST, produces = MediaType.TEXT_HTML_VALUE)
    public String notify(HttpServletRequest request) {

        log.info("微信支付回调开始请求");

        ByteArrayOutputStream outputStream = getNotifyStream(request);
        if (outputStream == null) {
            log.error("提取数据失败");
            return getFailResponse("提取数据失败");
        }
        SortedMap<String, String> params = new TreeMap<String, String>();
        try {
            String reqData = new String(outputStream.toByteArray(), "UTF8");
            log.info("wechat-pay-notify:params:{}", reqData);
            Map reqMap = WXPayUtil.xmlToMap(reqData);
            log.info("微信支付:{}", JSON.toJSONString(reqMap));
            String returnCode = reqMap.get("return_code").toString();
            if (!"SUCCESS".equals(returnCode)) {
                return getFailResponse(reqMap.get("return_msg").toString());
            }
            for (Object name : reqMap.keySet()) {
                params.put(name.toString(), reqMap.get(name).toString());
            }
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage());
            return getFailResponse("数据转换成UTF8格式错误");
        } catch (Exception e) {
            log.error("xml解析数据失败", e);
            return getFailResponse("xml解析数据失败");
        }
//
        try {
            payNotifyService.wechatPayNotify(params);
            return getSuccessResponse();
        } catch (Exception e) {
//            orderService.updateOrderPayCompleted(orderDto);


            return getFailResponse(e.getMessage());
        }
    }

    private ByteArrayOutputStream getNotifyStream(HttpServletRequest request) {
        InputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;

        try {
            inputStream = request.getInputStream();
            outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception sube) {
                    log.error("wechat pay callback error", sube);
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Exception sube) {
                    log.error("wechat pay callback error", sube);
                }
            }
        }
        return outputStream;
    }


    private String getFailResponse(String errMsg) {

        SortedMap<String, String> map = new TreeMap<String, String>();
        map.put("return_code", "FAIL");
        map.put("return_msg", errMsg);

        return getXmlData(map);
    }

    private String getSuccessResponse() {

        SortedMap<String, String> map = new TreeMap<String, String>();
        map.put("return_code", "SUCCESS");
        map.put("return_msg", "OK");

        return getXmlData(map);
    }

    private String getXmlData(SortedMap<String, String> params) {

        StringBuffer buffer = new StringBuffer();
        buffer.append("<xml>\n");
        for (String key : params.keySet()) {

            String value = params.get(key);
            buffer.append("<" + key + ">" + value + "</" + key + ">\n");
        }
        buffer.append("</xml>\n");

        return buffer.toString();
    }


}
