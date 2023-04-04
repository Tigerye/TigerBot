package com.tigerobo.x.pai.biz.message;


import com.tigerobo.x.pai.biz.message.aliyun.AliMessage;
import com.tigerobo.x.pai.biz.message.submail.SubmailMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;

/**
 * @author:Wsen
 * @time: 2020/4/21
 **/
@Slf4j
public class MessageSender {

    public static final String CODE_MSG = "【虎博科技】验证码：{0}，5分钟内输入有效，请尽快验证，如非本人操作请忽略.";
    public static void sendCode(String areaCode,String mobile,String code,String channel){
        if ("submail".equalsIgnoreCase(channel)){
            useSubmail(areaCode,mobile,code);
        }else {
            useAliyun(areaCode,mobile,code);
        }
//
    }

    private static void useAliyun(String areaCode, String mobile, String code){
        AliMessage.getInstance().send(areaCode,mobile,code);
        log.info("submail,mobile:{},result:{}",mobile);
    }


    private static void useSubmail(String areaCode, String mobile, String code) {
        String content = MessageFormat.format(CODE_MSG,code);
        SubmailMessage submailMessage = new SubmailMessage();
        String sendMobile = (areaCode==null||areaCode.isEmpty()||"+86".equals(areaCode))?mobile:areaCode+mobile;
        String send = submailMessage.send(sendMobile, content);

        log.info("submail,mobile:{},result:{}",mobile,send);
    }


    public static void main(String[] args) {
        String mobile = "13002150756";
        sendCode(null,mobile,"1234",null);
    }
}
