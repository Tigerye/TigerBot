package com.tigerobo.pai.biz.test.service.test;

import com.alibaba.fastjson.JSON;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.auth.service.UserService;
import com.tigerobo.x.pai.api.vo.WebRepVo;
import com.tigerobo.x.pai.dal.auth.dao.UserDao;
import com.tigerobo.x.pai.dal.auth.entity.UserDo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class UserServiceTest extends BaseTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDao userDao;



//    @Autowired
//    private UserWebService userWebService;
    @Test
    public void createUserTest(){

        String reqJson = "{\"params\":{\"user\":{\"name\":\"7881wsen\",\"account\":\"wsen7881\",\"mobile\":\"18301987881\",\"avatar\":\"\",\"password\":\"wsen7881@\",\"website\":\"https://wsen7881.com\",\"intro\":\"https://wsen7881.com个人简介\",\"wechat\":\"\",\"email\":\"wsen@tigerobo.com\"}}}";

        WebRepVo requestVo = JSON.parseObject(reqJson, WebRepVo.class);
//
//        Authorization authorization = userWebService.userRegister(requestVo);
//
//        System.out.println(JSON.toJSONString(authorization));

    }

    @Test
    public void getTest(){

        String uuid = "75cc4a4db9ea8948a0316ffe3e376f83";
        UserDo userDo = userDao.get(uuid);

        System.out.println(JSON.toJSONString(userDo));
    }

    @Test
    public void groupTest(){
//
//        QueryVo queryVo = new QueryVo();
//        PageInfo<GroupVo> groupVoPageInfo = groupService.queryCompany(queryVo);
//
//        System.out.println(JSON.toJSONString(groupVoPageInfo));
    }

    @Test
    public void getUserTest(){

//
//        String json = "{\"token\":\"c4d67d898c9cc8ce77ee610854aafd92-75ddb5778e9fe46fd17360dc73fd2afd-1000\",\"uid\":\"d2c1c4f00697ac39a4d8b9a4ca189d11\"}";
//        Authorization authorization = JSON.parseObject(json, Authorization.class);
//
//        User userByAuth = userService.getUserByAuth(authorization);
//
//        System.out.println(JSON.toJSONString(userByAuth));
    }
}
