package com.tigerobo.pai.biz.test.dal.test;

import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.dal.biz.dao.pub.PubCrawlerSiteRelDao;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SiteDaoTest extends BaseTest {

    @Autowired
    private PubCrawlerSiteRelDao pubCrawlerSiteRelDao;

    @Test
    public void test(){
        int id = -1;
        Object siteIdBySrcId = pubCrawlerSiteRelDao.getSiteIdBySrcId(id);
        System.out.println(siteIdBySrcId);
    }
}
