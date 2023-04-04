package com.tigerobo.x.pai.biz.utils.feed;

import com.tigerobo.x.pai.biz.utils.EncryptionUtil;

public class ClientTokenUtil {


    public static String getToken(String v){


        String prefix = "basket_"+v;
        return EncryptionUtil.encrypt4NetAndJava(prefix);
    }




    public static String getTokenByGroupId(int userId){
        String prefix = "group_"+userId;
        return EncryptionUtil.encrypt4NetAndJava(prefix);
    }

    public static Integer getGroupIdByToken(String token){

        String value = EncryptionUtil.decrypt4NetAndJava(token);
        if (value == null||value.isEmpty()){
            return null;
        }

        String left = value.replace("group_", "");

        if (left.matches("\\d+")){
            return Integer.parseInt(left);
        }
        return null;
    }



    public static void main(String[] args) {
        String key = getToken("xiao_zhu");


        String tokenById = getTokenByGroupId(100000011);

        System.out.println(tokenById);
        String token = "8045d457777c7969e25c4e324bd6898d";
        Integer userIdByToken = getGroupIdByToken(token);

        System.out.println(userIdByToken);



    }

}
