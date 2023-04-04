package com.tigerobo.x.pai.api.exception;

import lombok.Data;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description:
 * @modified By:
 * @version: $
 */
@Data
public class BindingException extends RuntimeException {
    private int code;
    private String msg;
    private ResultCode resultCode;

    public BindingException() {
        this(ResultCode.USER_VALID_FAIL);
    }

    public BindingException(ResultCode failed) {
        this.resultCode = failed;
        this.code = failed.getCode();
        this.msg = failed.getMsg();
    }

    public BindingException(ResultCode failed, String message, Throwable cause) {
        super(message, cause);
        this.setResultCode(failed);
        this.setCode(failed.getCode());
        this.setMsg(message);
    }
}
