package com.tigerobo.pai.common.util.qiyuesuo;

import com.qiyuesuo.sdk.v2.SdkClient;
import com.qiyuesuo.sdk.v2.bean.User;
import com.qiyuesuo.sdk.v2.json.JSONUtils;
import com.qiyuesuo.sdk.v2.request.ContractPageRequest;
import com.qiyuesuo.sdk.v2.response.ContractPageResult;
import com.qiyuesuo.sdk.v2.response.SdkResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.tigerobo.pai.common.util.qiyuesuo.QysUserTest.getClient;

public class DrawContractPage {

    private static final Logger logger = LoggerFactory.getLogger(DrawContractPage.class);


    public static void main(String[] args) {
        Long id = 2879718414039974430L;
        draw(id);
    }

    public static void draw(Long id){
        SdkClient sdkClient = getClient();
        ContractPageRequest request = new ContractPageRequest(id,
                new User("13093553089", "MOBILE"), "");
        String response = sdkClient.service(request);
        SdkResponse<ContractPageResult> responseObj = JSONUtils.toQysResponse(response, ContractPageResult.class);
        if(responseObj.getCode() == 0) {
            ContractPageResult result = responseObj.getResult();
            logger.info("合同页面地址为:{}", result.getPageUrl());
            System.out.println(JSONUtils.toJson(result));
        } else {
            logger.info("请求失败，错误码:{}，错误信息:{}", responseObj.getCode(), responseObj.getMessage());
        }
    }
}
