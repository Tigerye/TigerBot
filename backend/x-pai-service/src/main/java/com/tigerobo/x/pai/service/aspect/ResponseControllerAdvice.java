package com.tigerobo.x.pai.service.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tigerobo.x.pai.api.exception.APIException;
import com.tigerobo.x.pai.api.exception.ResultCode;
import com.tigerobo.x.pai.api.vo.ResultVO;
import com.tigerobo.x.pai.service.controller.pay.AliPayController;
import com.tigerobo.x.pai.service.controller.pay.WechatPayNotifyController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @author ：cailiang
 * @date: Created in 2020/12/18 1:27
 * @description: 全局处理增强版Controller，避免Controller里返回数据每次都要用响应体来包装
 * @modified By:
 * @version: $
 */
@Slf4j
@RestControllerAdvice(basePackages = {"com.tigerobo.x.pai.service.controller"}) // 注意哦，这里要加上需要扫描的包
public class ResponseControllerAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> aClass) {
        String simpleName = returnType.getClass().getSimpleName();
//        log.info("resp:advice:name:{}",simpleName);
        // 如果接口返回的类型本身就是ResultVO那就没有必要进行额外的操作，返回false
        try {
            String name = returnType.getMethod().getDeclaringClass().getName();
            if (WechatPayNotifyController.class.getName().equalsIgnoreCase(name)) {
                return false;
            }
            if (AliPayController.class.getName().equalsIgnoreCase(name)){
                return false;
            }
        }catch (Exception ex){
            log.error("",ex);
        }

        return !returnType.getGenericParameterType().equals(ResultVO.class);
    }

    @Override
    public Object beforeBodyWrite(Object data, MethodParameter returnType, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
//        log.debug("{}#{} response: {}", returnType.getContainingClass().getSimpleName(), returnType.getMethod().getName(), JSON.toJSONString(data));
        // String类型不能直接包装，所以要进行些特别的处理
        if (returnType.getGenericParameterType().equals(String.class)) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                // 将数据包装在ResultVO里后，再转换为json字符串响应给前端
                return objectMapper.writeValueAsString(new ResultVO<>(data));
            } catch (JsonProcessingException e) {
                throw new APIException(ResultCode.FAILED);
            }
        }
        // 将原本的数据包装在ResultVO里
        return new ResultVO<>(data);
    }
}
