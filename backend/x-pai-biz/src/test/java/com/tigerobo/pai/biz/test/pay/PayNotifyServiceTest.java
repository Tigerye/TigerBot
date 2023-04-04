package com.tigerobo.pai.biz.test.pay;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.biz.pay.PayNotifyService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Slf4j
public class PayNotifyServiceTest extends BaseTest {


    @Autowired
    private PayNotifyService payNotifyService;

    @Test
    public void wechatNotifyTest(){


        String json = "{\"transaction_id\":\"4200001397202201055427958298\",\"nonce_str\":\"JsP3hvhqsitxiW3XbJkTL1egGZpWPKh7\",\"bank_type\":\"OTHERS\",\"openid\":\"omvch6Tlwy_aU8aTG2Q-MDP2mGnU\",\"sign\":\"16038E962844BB34ECE3B6CF96E07765AC9F34499E120021023C4C85751E4311\",\"fee_type\":\"CNY\",\"mch_id\":\"1525536381\",\"cash_fee\":\"1\",\"out_trade_no\":\"1838463043001912\",\"appid\":\"wx7dce890fbc536f39\",\"total_fee\":\"1\",\"trade_type\":\"NATIVE\",\"result_code\":\"SUCCESS\",\"time_end\":\"20220105194821\",\"is_subscribe\":\"Y\",\"return_code\":\"SUCCESS\"}";


        SortedMap<String,String> map = new TreeMap<>();

        JSONObject jsonObject = JSON.parseObject(json);

        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            map.put(entry.getKey(),String.valueOf(entry.getValue()));
        }
        payNotifyService.wechatPayNotify(map);
    }

}
