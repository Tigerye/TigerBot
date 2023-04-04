package com.tigerobo.pai.common.util;

import com.alibaba.fastjson.JSON;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TextFileUtil {

    public static List<Integer> getIntList(String fileName){

        List<Integer> list = new ArrayList<>();
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(fileName))));){
            String line=null;
            while ((line=reader.readLine())!=null){
                if (line.matches("\\d+")){
                    list.add(Integer.parseInt(line));
                }
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return list;
    }

    public static void main(String[] args) {
        String path = "E:\\pai\\blog\\爬虫数据\\twitter-视频更新ID.txt";
        List<Integer> list = getIntList(path);

        System.out.println("size:"+list.size());

        System.out.println(JSON.toJSONString(list));
    }
}
