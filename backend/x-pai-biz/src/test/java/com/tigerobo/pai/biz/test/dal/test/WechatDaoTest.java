package com.tigerobo.pai.biz.test.dal.test;

import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.dal.auth.dao.WechatInfoDao;
import com.tigerobo.x.pai.dal.auth.entity.WechatInfoPo;
//import org.junit.jupiter.api.Test;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class WechatDaoTest extends BaseTest {

    @Autowired
    private WechatInfoDao wechatInfoDao;

    @Test
    public void test(){

        WechatInfoPo po = new WechatInfoPo();
        po.setUnionId("11");
        po.setContent("{}");

        wechatInfoDao.insertOrUpdate(po);

    }
}
