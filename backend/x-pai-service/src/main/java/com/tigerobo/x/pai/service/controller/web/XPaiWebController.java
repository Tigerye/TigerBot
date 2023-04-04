package com.tigerobo.x.pai.service.controller.web;

import com.google.common.base.Preconditions;
import com.tigerobo.x.pai.api.auth.entity.Account;
import com.tigerobo.x.pai.api.config.ConditionConfig;
import com.tigerobo.x.pai.api.vo.WebRepVo;
import com.tigerobo.x.pai.biz.auth.process.AccountProcessor;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import io.swagger.annotations.Api;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description: 业务模块-前端服务-基础接口
 * @modified By:
 * @version: $
 */
@RestController
@RequestMapping("/web/")
@Api(value = "基础接口", position = 2900, tags = "基础接口")
//@Conditional(ConditionConfig.XPaiBizCondition.class)
public class XPaiWebController {
    @Autowired
    private AccountProcessor accountProcessor;

    protected Account otherHomeAccount(WebRepVo requestVo) {

        Integer userId = ThreadLocalHolder.getUserId();

        Account accountParam = requestVo.getOrDefault("account", Account.class, null);
        if (accountParam == null){
            accountParam = requestVo.getOrDefault("group", Account.class, null);
        }
        Preconditions.checkArgument(accountParam!=null&& StringUtils.isNotBlank(accountParam.getAccount()),"账号名称参数为空");
        Account account = this.accountProcessor.getAccount(accountParam.getAccount(),userId);

        return account;
    }

}