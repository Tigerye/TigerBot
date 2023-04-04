package com.tigerobo.x.pai.api.vo;

import com.tigerobo.x.pai.api.exception.ResultCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2020/12/18 1:15
 * @description: 全局返回对象
 * @modified By：
 * @version: 1.0$
 */
@Data
@ApiModel(value = "全局返回对象")
public class ResultVO<T> {
    @ApiModelProperty(name = "状态码，比如1000代表响应成功")
    private int code;
    @ApiModelProperty(name = "响应信息，用来说明响应情况")
    private String msg;
    @ApiModelProperty(name = "响应的具体数据")

    private T data;


    public ResultVO() {
        this(ResultCode.SUCCESS, null);
    }
    public ResultVO(T data) {
        this(ResultCode.SUCCESS, data);
    }

    public ResultVO(int code, String msg) {
        this.code = code;
        this.msg = msg;
//        this.data = data;
    }

    public ResultVO(ResultCode resultCode, T data) {
        this.code = resultCode.getCode();
        this.msg = resultCode.getMsg();
        this.data = data;
    }

    public static ResultVO success(){
        return new ResultVO(ResultCode.SUCCESS,null);
    }

    public static ResultVO fail(String msg){
        return new ResultVO(ResultCode.FAILED.getCode(),msg);
    }


}

