package com.tigerobo.x.pai.api.exception;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description: 结果状态码
 * @modified By:
 * @version: $
 */
public enum ResultCode {
    ERROR(-1,"错误"),
    CONFIRM_CODE_ERROR(12,"您输入的短信验证码错误"),
    PASSWORD_ERROR(13,"您输入的账号或密码不正确"),
    DuplicateMessage(14,"请勿重复发送验证码"),
    MessageServerError(15,"短信服务暂时不可用，请稍后"),
    MobileAlreadyRegister(16,"该手机号已注册账号"),
    WrongMobile(17,"当前手机号不合法"),
    MessageMaxError(18,"您已超发送过每日短信限额"),
    ConfirmCodeErrorMax(19,"您的错误次数已超3次，请15分钟后重试"),
    CAPTCHA_WRONG(20,"您输入的图片验证码错误"),
    WeChatAuthError(24,"微信授权失败"),
    WeChatAlreadyBind(25,"您已绑定过微信"),
    MobileAlreadyBind(26,"您已绑定该手机号"),
    MobileNotBind(28,"您的账号未绑定手机号，不能解绑微信"),
    WeChatNotAvailable(29,"该微信已经绑定账号"),



    // 1000系列通用错误
    SUCCESS(200, "操作成功"),

    // 2000系列用户错误
    USER_VALID_FAIL(2000, "用户验证异常"),
    USER_NOT_EXIST(2001, "用户不存在"),
    USER_LOGIN_FAIL(2002, "用户名或密码错误"),
    USER_NOT_LOGIN(2003, "用户还未登录,请先登录！"),
    USER_WECHAT_LOGIN_FAIL(2004, "用户微信登陆失败！"),
    USER_NO_PERMISSION(2009, "用户没权限"),

    USER_ACCOUNT_NULL(2110, "用户名不能为空！"),
    USER_ACCOUNT_INVALID(2111, "用户名格式不合法！"),
    USER_ACCOUNT_EXIST(2112, "用户名已存在！"),

    USER_MOBILE_NULL(2120, "手机号不能为空！"),
    USER_MOBILE_INVALID(2121, "手机号格式不合法！"),
    USER_MOBILE_EXIST(2122, "手机号已存在！"),

    USER_EMAIL_NULL(2130, "邮箱地址不能为空！"),
    USER_EMAIL_INVALID(2131, "邮箱地址格式不合法！"),
    USER_EMAIL_EXIST(2132, "邮箱地址已存在！"),

    USER_WECHAT_NULL(2140, "微信不能为空！"),
    USER_WECHAT_INVALID(2141, "微信不合法！"),
    USER_WECHAT_EXIST(2142, "微信已存在！"),

    USER_NAME_NULL(2150, "用户昵称不能为空！"),
    USER_NAME_EXIST(2152, "用户昵称已存在！"),

    USER_PASSWORD_INVALID(2191, "用户密码不合法!"),

    GROUP_NOT_EXIST(2201, "用户组不存在"),
    GROUP_NO_PERMISSION(2209, "用户组权限不足,请联系管理员"),

    GROUP_ACCOUNT_NULL(2210, "组名称不能为空！"),
    GROUP_ACCOUNT_INVALID(2211, "组名称不合法！"),
    GROUP_ACCOUNT_EXIST(2212, "组名称已存在！"),

    GROUP_NAME_NULL(2250, "组昵称不能为空！"),
    GROUP_NAME_EXIST(2252, "组昵称已存在！"),
    // 2900系列用户绑定
    BINDING_UNKNOWN_ERROR(2900, "绑定未知错误"),
    BIDDING_NOT_EXIST(2901, "绑定关系不存在"),
    BIDDING_EXIST(2902, "绑定关系已存在"),
    BIDDING_PLATFORM_EXIST(2912, "平台绑定关系不存在"),
    BIDDING_TOKEN_EXIST(2922, "第三方账号绑定关系已存在"),

    // 3000系列服务调用错误
    SERVING_ACCESS_FAIL(3001, "服务授权异常"),

    VALIDATE_FAILED(4000, "参数校验失败"),
    FAILED(5000, "服务器内部错误"),

    // 4500模型服务调用错误
    AML_SERVING_ACCESS_FAIL(6501, "模型调用失败"),
    // 9999系列
    PAGE_NOT_EXIST(9000, "页面不存在！"),
    TO_BE_CONTINUED(9999, "Coming Soon, 敬请期待！");


    private int code;
    private String msg;

    ResultCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
