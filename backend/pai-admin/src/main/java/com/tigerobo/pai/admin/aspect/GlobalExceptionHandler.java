package com.tigerobo.pai.admin.aspect;

import com.tigerobo.x.pai.api.exception.APIException;
import com.tigerobo.x.pai.api.exception.AuthorizeException;
import com.tigerobo.x.pai.api.exception.ResultCode;
import com.tigerobo.x.pai.api.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

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
    @ResponseStatus(HttpStatus.OK)
    public ResultVO<Object> APIExceptionHandler(APIException e) {
        log.error("API接口异常: ", e);
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
        log.error("用户验证异常: ", e);
        // 然后抽取错误提示信息进行返回
//        final String message = e.getMessage();
//        final String msg = e.getMsg();

        return new ResultVO<>(e.getCode(),e.getMsg());
    }


    /**
     * 方法参数错误异常
     *
     * @param e 参数异常
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResultVO<Object> MethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        log.error("参数验证异常", e);
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
        log.error("参数不合法: ", e);
        // 然后抽取错误提示信息进行返回
        return new ResultVO<>(ResultCode.VALIDATE_FAILED.getCode(), e.getMessage());
    }
}