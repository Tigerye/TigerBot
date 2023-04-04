package com.tigerobo.x.pai.biz.utils;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class IdGenerator {

    public static String getId(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }


    public static String getUserBlogViewId(Integer blogId,Integer userId,String ip,Integer day){

        String text = "";

        if (userId==null||userId==0){
            text = day+"_"+ip+"_"+blogId;
        }else {
            text = day+"_"+userId+"_"+blogId;
        }
        return Md5Util.getMd5(text);
    }


    public static String getUserUniqueId(Integer userId,String ip,Integer day){

        String text = "";

        if (userId==null||userId==0){
            text = day+"_"+ip;
        }else {
            text = day+"_"+userId;
        }
        return Md5Util.getMd5(text);
    }

    public static final String DEMAND_PRE = "demand-";

    public static String getDemandId(String name,Integer userId){

        String text = DEMAND_PRE+userId+"-"+name+System.currentTimeMillis()/1000/100;
        return Md5Util.getMd5(text);
    }

    volatile private static int roteNum = 0;


    public static String getOrderNo(int machineIdx){
        return "AG"+getId(machineIdx);
    }

    public static long getId(int machineIdx){
        Long id;
        synchronized (IdGenerator.class) {

            long second = System.currentTimeMillis() / 1000;
            roteNum = (roteNum + 1) % 10000;
            id = second * 1000000 + machineIdx * 10000 + roteNum;
        }
        return buildCommonId(id);
    }

    public static long getBaseId(int machineIdx){
        Long id;
        synchronized (IdGenerator.class) {
            roteNum = (roteNum + 1) % 10000;
            id = System.currentTimeMillis() / 1000 * 1000000 + machineIdx * 10000 + roteNum;
        }
        return id;
    }
    private static long buildCommonId(Long id) {
        char[] arr = id.toString().toCharArray();
        arr = swap(arr, 1, 5);
        arr = swap(arr, 4, 9);
        arr = swap(arr, 11, 13);
        arr = swap(arr, 3, 12);
        arr = swap(arr, 7, 14);
        arr = swap(arr, 3, 10);
        arr = swap(arr, 2, 8);
        return Long.parseLong(new String(arr));
    }

    public static char[] swap(char[] arr, int i, int j) {
        char tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
        return arr;
    }

    public static void main(String[] args) {

        long baseId = getBaseId(1);

        System.out.println(String.valueOf(baseId).length());
    }
}
