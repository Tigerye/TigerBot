package com.tigerobo.x.pai.service.aspect.exception;

import com.dianping.cat.Cat;
import com.fasterxml.jackson.core.io.JsonEOFException;
import com.tigerobo.x.pai.api.exception.APIException;
import com.tigerobo.x.pai.api.exception.AuthorizeException;
import com.tigerobo.x.pai.api.exception.ResultCode;
import com.tigerobo.x.pai.api.exception.ToBeContinuedException;
import com.tigerobo.x.pai.api.vo.ResultVO;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2020/12/18 1:23
 * @description: 全局异常统一处理
 * @modified By:
 * @version:
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 自定义异常APIException
     *
     * @param e 接口异常
     * @return
     */
    @ExceptionHandler(APIException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultVO<Object> APIExceptionHandler(APIException e) {
//        log.error("API接口异常: ", e);
        logError(e);
        return new ResultVO<>(e.getResultCode().getCode(), e.getMsg());
    }

    /**
     * 用户验证 AuthorizationException
     *
     * @param e 授权异常
     * @return
     */
    @ExceptionHandler(AuthorizeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultVO<Object> AuthorizationExceptionHandler(AuthorizeException e) {
//        log.error("用户验证异常: ", e);
        logError(e);
        // 然后抽取错误提示信息进行返回
        return new ResultVO<>(e.getResultCode().getCode(), e.getMsg());
    }

    @ExceptionHandler(ToBeContinuedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultVO<Object> ToBeContinuedExceptionHandler(ToBeContinuedException e) {
//        log.error("功能待实现: ", e);
        logError(e);
        // 然后抽取错误提示信息进行返回
        return new ResultVO<>(ResultCode.TO_BE_CONTINUED.getCode(), e.getMsg());
    }

    /**
     * 方法参数错误异常
     *
     * @param e 参数异常
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultVO<Object> MethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
//        log.error("参数验证异常", e);
        logError(e);
        List<String> list = new ArrayList<>();        // 从异常对象中拿到ObjectError对象
        if (!e.getBindingResult().getAllErrors().isEmpty()) {
            for (ObjectError error : e.getBindingResult().getAllErrors()) {
                if (list.size()>3){
                    break;
                }
                list.add(error.getDefaultMessage().toString());
            }
        }
        // 然后抽取错误提示信息进行返回
        return new ResultVO<>(ResultCode.VALIDATE_FAILED, list);
    }


    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultVO<Object> IllegalArgumentExceptionHandler(IllegalArgumentException e) {
//        log.error("参数不合法: ", e);
        logError(e);
        // 然后抽取错误提示信息进行返回
        return new ResultVO<>(ResultCode.VALIDATE_FAILED.getCode(), e.getMessage());
    }

    @ExceptionHandler(HttpClientErrorException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultVO<Object> HttpClientErrorExceptionHandler(HttpClientErrorException e) {
        logError(e);
//        log.error("远程服务请求异常: ", e);
        // 然后抽取错误提示信息进行返回
        final String message = e.getMessage();
//        final HttpServletRequest request =
//                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String regex = "412\\s:\\s\\[(.+)]";
        if (message!=null){
            Pattern pattern = Pattern.compile(regex);
            final Matcher matcher = pattern.matcher(message);
            if (matcher.find()){
                return new ResultVO<>(ResultCode.VALIDATE_FAILED.getCode(),matcher.group(1));
            }
        }
        return new ResultVO<>(ResultCode.VALIDATE_FAILED.getCode(),"服务调用异常");
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResultVO<Object> ExceptionHandler(Exception e) {
        // 然后抽取错误提示信息进行返回
        logError(e);

        if (e instanceof JsonEOFException|| e instanceof HttpMessageNotReadableException){
            return new ResultVO<>(ResultCode.FAILED.getCode(),"入参格式不正确");
        }
        return new ResultVO<>(ResultCode.FAILED.getCode(),"服务调用失败");
    }


    private void logError(Exception ex){
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) ra).getRequest();
//        String url = request.getRequestURL().toString();
        String method = request.getMethod();
        String uri = request.getRequestURI();
        final Integer userId = ThreadLocalHolder.getUserId();
        final String bodyData = getBodyData(request);
        log.error("REQUEST: uri->{},userId:{} method->{}，body:{}", uri, method,userId,bodyData,ex);


        String msg = "uri->"+uri+",body="+bodyData;
        Cat.logError(msg,ex);
    }

    private String getBodyData(HttpServletRequest request) {
        StringBuffer data = new StringBuffer();
        String line = null;
        BufferedReader reader = null;
        try {
            reader = request.getReader();
            while (null != (line = reader.readLine()))
                data.append(line);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
        return data.toString();
    }
}