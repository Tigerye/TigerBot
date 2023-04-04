package com.tigerobo.x.pai.biz.message.aliyun;
import com.alibaba.fastjson.JSON;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author:Wsen
 * @time: 2020/7/22
 **/
@Slf4j
public class AliMessage {
    private static AliMessage aliMessage = null;
    static {
        aliMessage = new AliMessage();
    }
    private AliMessage(){}
    public static AliMessage getInstance(){
        return aliMessage;
    }

    private static String mainlandTemplateId = "SMS_197390050";
    private static String outerTemplateId = "SMS_149101051";
    private static String accessKeyId = "LTAI4G6L1KYASHGdHeFgcTSq";
    private static String secret = "GAjsslAt28U8ZvQ03G2MBosLbTG0eN";
    private static String mainlandSign = "虎博科技";
    private static String outerSign = "虎烨科技";

    public void send(String area,String mobile,String code){
        if (StringUtils.isEmpty(area)||area.equals("+86")||area.equals("86")){
            sendMainland(mobile,code);
        }else {
            String fullMobile;
            if (area.startsWith("+")){
                fullMobile = area.substring(1);
            }else {
                fullMobile = area;
            }
            fullMobile += mobile;
            sendOuter(fullMobile,code);
        }
    }

    public void sendMainland(String mobile,String code){
        doSend(mobile,code,mainlandTemplateId,mainlandSign);
    }

    public void sendOuter(String mobile,String code){
        doSend(mobile,code,outerTemplateId,outerSign);
    }

    private void doSend(String mobile,String code,String templateId,String signName) {
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, secret);
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", mobile);
        request.putQueryParameter("SignName", signName);
        request.putQueryParameter("TemplateCode", templateId);
        Map<String,Object> templateMap = new HashMap<>();
        templateMap.put("code",code);
        String codeJson = JSON.toJSONString(templateMap);
        request.putQueryParameter("TemplateParam", codeJson);
        try {
            CommonResponse response = client.getCommonResponse(request);
            if (response!=null&& !StringUtils.isEmpty(response.getData())&&response.getData().contains("OK")){
            }else {
                log.error("aliyun-mobile:{},err:{}",mobile,JSON.toJSONString(response));
            }
        } catch (ClientException e) {
            log.error("aliyun-send:modile:"+mobile+",code:"+code+",template:"+templateId,e);
        }
    }

    public static void main(String[] args) {
        String mobile = "18301966691";
        String code = "222224";

        getInstance().send("+86",mobile,code);
    }
}
