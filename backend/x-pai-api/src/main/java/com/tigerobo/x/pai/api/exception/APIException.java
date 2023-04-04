package com.tigerobo.x.pai.api.exception;

import lombok.Data;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description: 全局接口异常
 * @modified By:
 * @version: $
 */
@Data
public class APIException extends RuntimeException {
    private int code;
    private String msg;
    private ResultCode resultCode;

    public APIException() {
        this(ResultCode.FAILED);
    }

    public APIException(ResultCode failed) {
        this.resultCode = failed;
        this.code = failed.getCode();
        this.msg = failed.getMsg();
    }

    public APIException(ResultCode failed, String message, Throwable cause) {
        super(message, cause);
        setResultCode(failed);
        setCode(failed.getCode());
        setMsg(message);
    }
}
