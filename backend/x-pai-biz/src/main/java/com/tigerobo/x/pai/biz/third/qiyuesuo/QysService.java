package com.tigerobo.x.pai.biz.third.qiyuesuo;

import com.alibaba.fastjson.JSON;
import com.qiyuesuo.sdk.v2.SdkClient;
import com.qiyuesuo.sdk.v2.bean.*;
import com.qiyuesuo.sdk.v2.json.JSONUtils;
import com.qiyuesuo.sdk.v2.request.*;
import com.qiyuesuo.sdk.v2.response.CompanyAuthPageResult;
import com.qiyuesuo.sdk.v2.response.CompanyAuthResult;
import com.qiyuesuo.sdk.v2.response.ContractPageResult;
import com.qiyuesuo.sdk.v2.response.SdkResponse;
import com.tigerobo.x.pai.api.auth.entity.OrgInfoDto;
import com.tigerobo.x.pai.dal.biz.entity.ContractInfoPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class QysService {
    private SdkClient client = null;

    @Value("${pai.qys.key:}")
    private String key = "PhbPPimEUj";
    @Value("${pai.qys.secret:}")
    String secret = "oiFaixSB3h05AsCplLCyGRh33IpV9O";
    @Value("${pai.qys.url:}")
    String url = "https://openapi.qiyuesuo.cn";

    @Value("${pai.qys.company.callback:}")
    private String companyCallbackUrl;
//
//    @Value("${pai.qys.category:}")
//    private Long qysCategory;

    private SdkClient getClient() {
        if (client == null){
            synchronized (QysService.class){
                if (client == null){
                    client = new SdkClient(url, key, secret);
                }
            }
        }
        return client;
    }


    public SdkResponse<Contract> draft(ContractInfoPo infoPo) {
        // 初始化sdkClient
        long startTime = System.currentTimeMillis();
        SdkClient sdkClient = getClient();
// 合同基本参数
        Contract contract = new Contract();
        contract.setSubject(infoPo.getSubject());
        if (!StringUtils.isEmpty(infoPo.getCategoryId())&&infoPo.getCategoryId().matches("\\d+")){
            contract.setCategory(new Category(Long.parseLong(infoPo.getCategoryId())));
        }

        contract.setBizId(String.valueOf(infoPo.getId()));

        Signatory signatory1 = new Signatory();
        signatory1.setTenantName(infoPo.getOrgFullName());
        signatory1.setTenantType("COMPANY");
        signatory1.setReceiver(new User(infoPo.getOrgContactName(), infoPo.getOrgContactMobile(), "MOBILE"));
        signatory1.setSerialNo(1);

        Signatory signatory2 = new Signatory();

        signatory2.setTenantName(infoPo.getSponsorName());
        signatory2.setTenantType("COMPANY");
        signatory2.setReceiver(new User(infoPo.getSponsorContactName(), infoPo.getSponsorMobile(), "MOBILE"));
        signatory2.setSerialNo(2);
        Action action = new Action("COMPANY", 2);
        signatory2.addAction(action);
// 设置签署方
        contract.addSignatory(signatory1);
        contract.addSignatory(signatory2);
// 设置模板参数
        contract.addTemplateParam(new TemplateParam("models", infoPo.getProductName()));
        contract.setSend(true);

// 创建合同
        ContractDraftRequest request = new ContractDraftRequest(contract);
        long preTime = System.currentTimeMillis();
        String response = sdkClient.service(request);

        log.info("draft-time:{}ms,callTime:{}ms",System.currentTimeMillis()-startTime,System.currentTimeMillis()-preTime);

        SdkResponse<Contract> responseObj = JSONUtils.toQysResponse(response, Contract.class);
// 返回结果

        return responseObj;
    }

    public SdkResponse<ContractPageResult> pageUrl(Long contractId,String mobile){
        ContractPageRequest request = new ContractPageRequest(contractId,
                new User(mobile, "MOBILE"), "");
        String response = getClient().service(request);
        SdkResponse<ContractPageResult> responseObj = JSONUtils.toQysResponse(response, ContractPageResult.class);

        return responseObj;
    }




    public  SdkResponse<CompanyAuthPageResult> enterprise(OrgInfoDto orgInfoDto) {
        try {
            SdkClient sdkClient = getClient();
            User applicant = new User(orgInfoDto.getContactName(), orgInfoDto.getContactMobile(), "MOBILE");

            CompanyAuthH5PageRequest request = new CompanyAuthH5PageRequest(orgInfoDto.getFullName(), applicant);
            String callbackUrl = companyCallbackUrl+orgInfoDto.getId();
            request.setCallbackUrl(callbackUrl);
            String response = sdkClient.service(request);
            SdkResponse<CompanyAuthPageResult> pageResultResponse = JSONUtils.toQysResponse(response, CompanyAuthPageResult.class);
            if (pageResultResponse.getCode() == 0) {
                log.info("契约锁请求成功，认证链接:{}", pageResultResponse.getResult().getAuthUrl());
            } else {
                log.info("请求失败，错误码:{}，错误信息:{}", pageResultResponse.getCode(), pageResultResponse.getMessage());
            }
            return pageResultResponse;
        }catch (Exception ex){
            log.error("契约锁:{}", JSON.toJSON(orgInfoDto),ex);
            return null;
        }
    }

    public SdkResponse <CompanyAuthResult> vimEnterprise(String companyName){
        CompanyAuthResultRequest request = new CompanyAuthResultRequest();
        request.setCompanyName(companyName);
        String response = getClient().service(request);
        SdkResponse <CompanyAuthResult> resultResponse = JSONUtils.toQysResponse(response,CompanyAuthResult.class);
        if(resultResponse.getCode() == 0) {
            CompanyAuthResult result = resultResponse.getResult();
            log.info("请求成功，认证状态:{}", result.getStatus().toString());
        } else {
            log.info("请求失败，错误码:{}，错误信息:{}", resultResponse.getCode(), resultResponse.getMessage());
        }
        return resultResponse;
    }

    public SdkResponse<Contract> viewContract(Long contractId){
        ContractDetailRequest request = new ContractDetailRequest(contractId);
        String response = getClient().service(request);
        SdkResponse<Contract> responseObj = JSONUtils.toQysResponse(response, Contract.class);
        if(responseObj.getCode() == 0) {
            Contract contract = responseObj.getResult();
            log.info("合同详情查询，合同主题：{}", contract.getSubject());
        } else {
            log.info("请求失败，错误码:{}，错误信息:{}", responseObj.getCode(), responseObj.getMessage());
        }
        return responseObj;
    }

}
