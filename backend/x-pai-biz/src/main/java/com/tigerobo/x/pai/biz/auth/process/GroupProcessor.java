package com.tigerobo.x.pai.biz.auth.process;

import com.tigerobo.x.pai.api.auth.entity.Group;
import com.tigerobo.x.pai.api.exception.AuthorizeException;
import com.tigerobo.x.pai.api.exception.ResultCode;
import com.tigerobo.x.pai.dal.auth.dao.GroupDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.regex.Pattern;


/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description: 用户认证-处理器-用户组处理器
 * @modified By:
 * @version: $
 */
@Slf4j
@Component
//@Lazy
public class GroupProcessor {

    @Autowired
    private GroupDao groupDao;
    // 至少4个字符，字母开头，仅包含a-z0-9-_
    private final static Pattern ACCOUNT_PATTERN = Pattern.compile("^[a-z]{1}[a-z0-9-_]{2,}$");

    public void checkAccount(Group group) {
        if (StringUtils.isEmpty(group.getAccount()))
            throw new AuthorizeException(ResultCode.GROUP_ACCOUNT_NULL, "组名称不能为空!", null);
        if (!ACCOUNT_PATTERN.matcher(group.getAccount()).find())
            throw new AuthorizeException(ResultCode.GROUP_ACCOUNT_INVALID, "组名称格式不合法！", null);

        if (groupDao.getByAccount(group.getAccount()) != null)
            throw new AuthorizeException(ResultCode.GROUP_ACCOUNT_EXIST, "组名称已存在!", null);
    }

    public void checkName(Group group) {
        if (StringUtils.isEmpty(group.getName()))
            throw new AuthorizeException(ResultCode.GROUP_NAME_NULL, "名称不能为空!", null);
        if (!CollectionUtils.isEmpty(this.groupDao.getByName(group.getName())))
            throw new AuthorizeException(ResultCode.GROUP_NAME_EXIST, "名称已存在!", null);
    }

    private final static String GROUP_LOGO_DEFAULT = "https://x-pai.oss-cn-shanghai.aliyuncs.com/auth/group/logo_default.png";

}
