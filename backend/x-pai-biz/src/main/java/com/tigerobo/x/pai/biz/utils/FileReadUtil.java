package com.tigerobo.x.pai.biz.utils;

import java.io.BufferedReader;
import java.io.FileReader;

public class FileReadUtil {

    public static String readOneLineContent(String path)throws Exception{

        StringBuilder builder = new StringBuilder();
        int size = 512;
        char[] buffer = new char[size];

        try(BufferedReader reader = new BufferedReader(new FileReader(path))){

            while (reader.read(buffer)!=-1){
                builder.append(buffer);
                buffer = new char[size];
            }
        }
        return builder.toString();
    }

    public static void main(String[] args) throws Exception{
        String path = "E:\\pai\\engine\\demo\\risk\\test_results_v2.json";

        String s = readOneLineContent(path);
        System.out.println(s);
    }
}
