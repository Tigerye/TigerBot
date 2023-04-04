package com.tigerobo.pai.biz.test.common.test;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

import java.util.Random;
import java.util.UUID;

public class MathTest {


    @Test
    public void passwordTest(){
        String MD5_SALT_AUTH_USER_PASSWORD = "auth-user-password";
        String password = "Ssmarchit2021@";

        String back = "13501839845";//3d1ef4951126d682557d3aff7f8af770
//        password = "Tigerobo0726@";
        String s = DigestUtils.md5Hex(String.join("-", MD5_SALT_AUTH_USER_PASSWORD, password));
        System.out.println(s);
    }
    @Test
    public void randomTest(){
        Random random = new Random(10);
        for (int i = 0; i < 10; i++) {
            int abs = Math.abs(random.nextInt());
            System.out.println(abs);
        }
    }

    @Test
    public void uuidTest(){
        String s = UUID.randomUUID().toString().replaceAll("-","");
        System.out.println(s);
        System.out.println(s.length());
    }



}
