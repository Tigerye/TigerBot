package com.tigerobo.x.pai.api.exception;

import lombok.Data;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description: 全局授权异常
 * @modified By:
 * @version: $
 */
@Data
public class AuthorizeException extends RuntimeException {
    private int code;
    private String msg;
    private ResultCode resultCode;

    public AuthorizeException() {
        this(ResultCode.USER_VALID_FAIL);
    }


    public AuthorizeException(String failed) {
        this.resultCode = ResultCode.ERROR;
        this.code = resultCode.getCode();
        this.msg = failed;
    }

    public AuthorizeException(ResultCode failed) {
        this.resultCode = failed;
        this.code = failed.getCode();
        this.msg = failed.getMsg();
    }

    public AuthorizeException(ResultCode failed, String message, Throwable cause) {
        super(message, cause);
        this.setResultCode(failed);
        this.setCode(failed.getCode());
        this.setMsg(message);
    }
}
