package com.tigerobo.pai.biz.test.dal.test;

import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.dal.biz.entity.pub.PubBigShotPo;
import com.tigerobo.x.pai.dal.biz.mapper.pub.PubBigShotMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

public class BigShotDaoTest extends BaseTest {

    @Autowired
    private PubBigShotMapper pubBigShotMapper;

    @Test
    public void test(){
        Example example = new Example(PubBigShotPo.class);
        int count = pubBigShotMapper.selectCountByExample(example);
        System.out.println(count);
    }

}
