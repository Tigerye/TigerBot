package com.tigerobo.x.pai.biz.pay.api;

import com.tigerobo.x.pai.api.auth.entity.User;
import com.tigerobo.x.pai.api.auth.service.UserService;
import com.tigerobo.x.pai.dal.pay.dao.ApiUserConfigDao;
import com.tigerobo.x.pai.dal.pay.entity.ApiUserConfigPo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ApiUserConfigService {

    @Autowired
    private ApiUserConfigDao apiUserConfigDao;

    @Value("${pai.web.url}")
    String webUrl;

    String agreementUrl = "pricing?apiKey=";
    String billActiveKey = "activeKey=9";

    @Autowired
    private UserService userService;

    private volatile Map<Integer, ApiUserConfigPo> allUserMap = null;

    @PostConstruct
    public void init() {

        this.doInit();
        final ScheduledExecutorService executorService =
                Executors.newScheduledThreadPool(1);

        executorService.scheduleAtFixedRate(this::doInit, 30, 15, TimeUnit.SECONDS);
    }

    private synchronized void doInit() {

        final List<ApiUserConfigPo> all = apiUserConfigDao.getAll();

        Map<Integer, ApiUserConfigPo> map = new HashMap<>();

        if (!CollectionUtils.isEmpty(all)) {
            for (ApiUserConfigPo apiUserConfigPo : all) {
                map.put(apiUserConfigPo.getUserId(), apiUserConfigPo);
            }
        }

        Map<Integer, ApiUserConfigPo> pre = allUserMap;

        allUserMap = map;
        if (pre!=null){
            pre.clear();
        }
    }

    public String checkApi(Integer userId,String apiKey) {
        if (allUserMap == null){
            doInit();
        }
        final ApiUserConfigPo po = allUserMap.get(userId);
        if (po == null) {
            return null;
        }
        final Boolean whiteUser = po.getWhiteUser();
        if (whiteUser != null && whiteUser) {
            return null;
        }

        final Boolean stopCall = po.getStopCall();
        String warnMsg = null;
        final Integer warnType = po.getWarnType();
        WarnType warnTypeEnum = WarnType.getWarnType(warnType);
        if (warnTypeEnum != null) {
            if (WarnType.TRY_COUNT_NEAR_OVER == warnTypeEnum) {
                warnMsg = "接口试用即将结束,需要更多服务请查看：" + getAgreementUrl(apiKey);
            } else if (WarnType.TRY_COUNT_OVER == warnTypeEnum) {
                warnMsg = "接口试用结束了,需要更多服务请查看：" + getAgreementUrl(apiKey);
            } else if (WarnType.HAS_BILL_NOT_PAY == warnTypeEnum) {
                warnMsg = "有到期账单未支付,详情请查看:" + getBillUrl(userId);
            }
        }

        if (stopCall == null || stopCall) {
            if (warnMsg == null) {
                warnMsg = "用户不支持接口调用，如果有更多需求，请联系:" + "";
            }
            throw new IllegalArgumentException(warnMsg);
        }
        return warnMsg;
    }

    public ApiUserConfigPo getByUserId(Integer userId) {
        if (userId == null) {
            return null;
        }
        return allUserMap.get(userId);
    }

    public void cancelTrayOverWarn(Integer userId) {

        final ApiUserConfigPo apiUserConfigPo = allUserMap.get(userId);

        if (apiUserConfigPo != null && apiUserConfigPo.getWarnType() != null) {
            if (apiUserConfigPo.getWarnType() == 1 || apiUserConfigPo.getWarnType() == 2) {
                ApiUserConfigPo update = new ApiUserConfigPo();
                update.setId(apiUserConfigPo.getId());
                update.setWarnType(0);
                update.setStopCall(false);
                apiUserConfigDao.update(update);
            }
        }
    }

    private String getAgreementUrl(String apiKey){
        return webUrl+agreementUrl+apiKey;
    }
    private String getBillUrl(Integer userId){
        if (userId == null){
            return "";
        }
        final User user = userService.getFromCache(userId);
        if (user==null){
            return "";
        }
        final String account = user.getAccount();
        if (StringUtils.isEmpty(account)){
            return "";
        }
        return webUrl+account+"?"+billActiveKey;
    }

    @Getter
    @AllArgsConstructor
    enum WarnType {
        TRY_COUNT_NEAR_OVER(1, "接口试用即将结束"),
        TRY_COUNT_OVER(2, "接口试用结束了"),
        HAS_BILL_NOT_PAY(3, "有到期账单未支付,详情请查看%"),
        ;
        Integer type;
        String msg;

        private static WarnType getWarnType(Integer type) {
            if (type == null) {
                return null;
            }
            for (WarnType value : values()) {

                if (value.getType().equals(type)) {
                    return value;
                }
            }
            return null;

        }

    }
}
