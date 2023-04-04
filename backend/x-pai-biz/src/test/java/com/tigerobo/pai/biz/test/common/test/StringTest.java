package com.tigerobo.pai.biz.test.common.test;

import com.tigerobo.x.pai.api.entity.Pair;
import org.checkerframework.checker.units.qual.A;
import org.junit.Test;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringTest {



    @Test
    public void regexTest(){

        String text = "412 : [我错了]";
        String regex = "412\\s:\\s\\[(.+)]";
        Pattern pattern = Pattern.compile(regex);

        final Matcher matcher = pattern.matcher(text);
        if (matcher.matches()){
            System.out.println(matcher.group(1));
        }
        System.out.println("fail");
    }
    @Test
    public void lengthTest(){

        System.out.println(String.valueOf(Integer.MAX_VALUE).length());

        String s = "https://x-pai.algolet.com/biz/blog/source/9875f9447dd4a0f71a5d8caafbd0f028.html?Expires=1642054816&OSSAccessKeyId=LTAI5t8HoYusAPr5MffHTauz&Signature=%2BzoywFaDI7qrFX5ziCHLMFhkFII%3D";
        System.out.println(s.length());
    }

    @Test
    public void listTest(){

        List<String> list = new ArrayList<>(10);

        System.out.println(list.size());
        for (String s : list) {
            System.out.println(s);
        }

        ArrayList<String> strings = new ArrayList<>(list);


    }

}
