package com.tigerobo.x.pai.biz.basket;

import com.tigerobo.x.pai.api.basket.BasketModelCallReportReq;
import com.tigerobo.x.pai.api.basket.BasketModelCallTotalCommitReq;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.biz.utils.feed.ClientTokenUtil;
import com.tigerobo.x.pai.dal.basket.dao.BasketModelCallDao;
import com.tigerobo.x.pai.dal.basket.entity.BasketModelCallPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BasketModelCallService {


    @Autowired
    private BasketModelCallDao basketModelCallDao;



    public void addTotalNum(BasketModelCallTotalCommitReq req) {

        Validate.isTrue(req != null, "参数错误");
        Validate.isTrue(StringUtils.isNotBlank(req.getAccessToken()), "token参数错误");
        Validate.isTrue(StringUtils.isNotBlank(req.getApiKey()), "apiKey参数错误");
        Validate.isTrue(req.getApiNum() != null, "num参数错误");

        Integer groupId = ClientTokenUtil.getGroupIdByToken(req.getAccessToken());
        Validate.isTrue(groupId!=null,"token不合法");

        BasketModelCallPo convert = convert(req);
        convert.setGroupId(groupId);

        basketModelCallDao.add(convert);
    }


    public void add(BasketModelCallReportReq req) {

        Validate.isTrue(req != null, "参数错误");
        Validate.isTrue(StringUtils.isNotBlank(req.getAccessToken()), "token参数错误");
        Validate.isTrue(StringUtils.isNotBlank(req.getApiKey()), "apiKey参数错误");
        Validate.isTrue(req.getApiNum() != null, "num参数错误");
//        Validate.isTrue(req.getApiNum() > 0, "num参数错误");


        Integer groupId = ClientTokenUtil.getGroupIdByToken(req.getAccessToken());
        Validate.isTrue(groupId!=null,"token不合法");

        BasketModelCallPo convert = convert(req);
        convert.setGroupId(groupId);

        basketModelCallDao.add(convert);
    }


    private BasketModelCallPo convert(BasketModelCallReportReq req) {
        BasketModelCallPo po = new BasketModelCallPo();

        po.setToken(req.getAccessToken());
        po.setApiKey(req.getApiKey());
        po.setCallNum(req.getApiNum());
        po.setStartCallTime(req.getStartCallTime());
        po.setEndCallTime(req.getEndCallTime());

        String ip = ThreadLocalHolder.getIp();
        po.setIp(ip);

        po.setMemo(req.getMemo());
        return po;
    }

    private BasketModelCallPo convert(BasketModelCallTotalCommitReq req) {
        BasketModelCallPo po = new BasketModelCallPo();

        po.setToken(req.getAccessToken());
        po.setApiKey(req.getApiKey());
        po.setCallNum(req.getApiNum());


        String ip = ThreadLocalHolder.getIp();
        po.setIp(ip);

        po.setType(1);
        return po;
    }
}
