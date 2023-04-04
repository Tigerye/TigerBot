package com.tigerobo.x.pai.api.exception;

import lombok.Data;

import static com.tigerobo.x.pai.api.exception.ResultCode.TO_BE_CONTINUED;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description:
 * @modified By:
 * @version: $
 */
@Data
public class ToBeContinuedException extends RuntimeException {
    private int code;
    private String msg;
    private ResultCode resultCode;

    public ToBeContinuedException() {
        setResultCode(TO_BE_CONTINUED);
        setCode(TO_BE_CONTINUED.getCode());
        setMsg(TO_BE_CONTINUED.getMsg());
    }

    public ToBeContinuedException(String message, Throwable cause) {
        super(message, cause);
    }
}
