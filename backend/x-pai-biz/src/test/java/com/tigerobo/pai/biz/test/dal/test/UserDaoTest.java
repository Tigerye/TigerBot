package com.tigerobo.pai.biz.test.dal.test;

import com.alibaba.fastjson.JSON;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.dal.auth.dao.GroupDao;
import com.tigerobo.x.pai.dal.auth.dao.UserDao;
import com.tigerobo.x.pai.dal.auth.entity.GroupDo;
import com.tigerobo.x.pai.dal.auth.entity.UserDo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class UserDaoTest extends BaseTest {

    @Autowired
    private UserDao userDao;

    @Autowired
    private GroupDao groupDao;

    @Test
    public void groupNameTest(){


        String name = "虎博";
        List<GroupDo> groupDos = groupDao.likeNameList(name);
        System.out.println(JSON.toJSONString(groupDos));
    }

    @Test
    public void getByUUidTest(){
        String uuid = "d2c1c4f00697ac39a4d8b9a4ca189d11";

        UserDo userDo = userDao.getByUuid(uuid);
        System.out.println(JSON.toJSONString(userDo));

    }
}
