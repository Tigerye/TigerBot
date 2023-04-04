package com.tigerobo.x.pai.biz.auth.process;

import com.tigerobo.x.pai.api.auth.entity.User;
import com.tigerobo.x.pai.api.exception.AuthorizeException;
import com.tigerobo.x.pai.api.exception.ResultCode;
import com.tigerobo.x.pai.dal.auth.dao.UserDao;
import com.tigerobo.x.pai.dal.auth.entity.UserDo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import java.lang.IllegalArgumentException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description: 用户认证-处理器-用户处理器
 * @modified By:
 * @version: $
 */
@Slf4j
@Component
//@Lazy
public class UserProcessor {

    @Autowired
    private UserDao userDao;

    private final static String MD5_SALT_AUTH_USER_PASSWORD = "auth-user-password";

    public String getPassword(User user) {
        if (StringUtils.isEmpty(user.getPassword()))
            throw new IllegalArgumentException("User.password");
        return DigestUtils.md5Hex(String.join("-", MD5_SALT_AUTH_USER_PASSWORD, user.getPassword()));
    }

    // 至少8个字符，大小写字母,数字,特殊字符至少包含三种
    private final static Pattern PASSWORD_PATTERN = Pattern.compile("^(?![A-Za-z]+$)(?![A-Z\\d]+$)(?![A-Z\\W]+$)(?![a-z\\d]+$)(?![a-z\\W]+$)(?![\\d\\W]+$)\\S{8,}$");

    public void checkPassword(User user) {
        if (StringUtils.isEmpty(user.getPassword()))
            throw new IllegalArgumentException("User.password");
        if (!PASSWORD_PATTERN.matcher(user.getPassword()).find())
            throw new AuthorizeException(ResultCode.USER_PASSWORD_INVALID, "密码不合法", null);
    }

    // 至少4个字符，字母开头，仅包含a-z0-9-_
    private final static Pattern ACCOUNT_PATTERN = Pattern.compile("^[a-z]{1}[a-z0-9-_]{2,}$");

    public void checkAccount(User user) {
        if (StringUtils.isEmpty(user.getAccount()))
            throw new AuthorizeException(ResultCode.USER_ACCOUNT_NULL, "用户名不能为空!", null);
        if (!ACCOUNT_PATTERN.matcher(user.getAccount()).find())
            throw new AuthorizeException(ResultCode.USER_ACCOUNT_INVALID, "用户名不能包含数字、小写字母、-、_以外字符！", null);

        UserDo byAccount = userDao.getByAccount(user.getAccount());
        if (byAccount!= null)
            throw new AuthorizeException(ResultCode.USER_ACCOUNT_EXIST, "用户名[" + user.getAccount() + "]已存在!", null);
    }

    private final static Pattern MOBILE_PATTERN = Pattern.compile("^(\\+86)?(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$");

    public void checkMobile(User user) {
        if (StringUtils.isEmpty(user.getMobile()))
            throw new AuthorizeException(ResultCode.USER_MOBILE_NULL, "手机号为空！", null);
        if (!MOBILE_PATTERN.matcher(user.getMobile()).find())
            throw new AuthorizeException(ResultCode.USER_MOBILE_INVALID, "手机号格式错误！", null);

        UserDo byMobile = userDao.getByMobile(user.getMobile());
        if (byMobile != null)
            throw new AuthorizeException(ResultCode.USER_MOBILE_EXIST, "手机号[" + user.getMobile() + "]已存在！", null);
    }

    private final static Pattern EMAIL_PATTERN = Pattern.compile("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");

    public void checkEmail(User user) {
        if (StringUtils.isEmpty(user.getEmail()))
            return;
        if (!EMAIL_PATTERN.matcher(user.getEmail()).find())
            throw new AuthorizeException(ResultCode.USER_EMAIL_INVALID, "邮箱地址不合法！", null);
//        if (this.mapper.selectByEmail(user.getEmail()) != null)
//            throw new AuthorizeException(ResultCode.USER_EMAIL_EXIST, "邮箱地址[" + user.getEmail() + "]已存在！", null);
    }

    public void checkName(User user) {
        if (StringUtils.isEmpty(user.getName()))
            throw new AuthorizeException(ResultCode.USER_NAME_NULL, "用户名称不能为空!", null);
//        if (!CollectionUtils.isEmpty(this.mapper.selectByName(user.getName())))
//            throw new AuthorizeException(ResultCode.USER_NAME_EXIST, "用户昵称[" + user.getName() + "]已存在!", null);
    }


    private final static String USER_AVATAR_DEFAULT = "https://x-pai.oss-cn-shanghai.aliyuncs.com/auth/user/avatar_default.png";

}

