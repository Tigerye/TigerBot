package com.tigerobo.pai.common.util.qiyuesuo;

import com.alibaba.fastjson.JSON;
import com.qiyuesuo.sdk.v2.SdkClient;
import com.qiyuesuo.sdk.v2.bean.*;
import com.qiyuesuo.sdk.v2.json.JSONUtils;
import com.qiyuesuo.sdk.v2.request.*;
import com.qiyuesuo.sdk.v2.response.*;
import com.qiyuesuo.sdk.v2.utils.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class QysUserTest {


    private static final Logger logger = LoggerFactory.getLogger(QysUserTest.class);
    public static final String key = "PhbPPimEUj";
    public static final String secret = "oiFaixSB3h05AsCplLCyGRh33IpV9O";
    public static final String url = "https://openapi.qiyuesuo.cn";


    public static final String CLIENT_COMPANY_NAME = "上海虎烨信息科技有限公司";

    public static String myCompanyName = "虎博科技技术有限公司";
//    public static final String mobile = "13093553089";

    public static final String mobile = "18301966691";
    public static final String companyName = "上海虎烨信息科技有限公司";
    private static SdkClient client = null;
    public static SdkClient getClient() {

        if (client == null){
            String accessKey = key;
            String accessSecret = secret;
            client = new SdkClient(url, accessKey, accessSecret);
        }

        return client;
    }

    public static void main(String[] args) throws Exception{


//        draft(getClient());

        long contractId = 2879718414039974430L;
//        drawAndPageUrl();
//        pageUrl(contractId);
//        document();
//        viewPerson();
//        person();
        enterprise();
//        vimEnterprise(companyName);
//        contractProcess();
    }

    public static void vimEnterprise(String companyName){
        CompanyAuthResultRequest request = new CompanyAuthResultRequest();
//        request.setRequestId("f8967fcd-6bdc-42a2-a136-17dd6225d0dd");
        request.setCompanyName(companyName);
        String response = getClient().service(request);
        SdkResponse <CompanyAuthResult> resultResponse = JSONUtils.toQysResponse(response,CompanyAuthResult.class);
        if(resultResponse.getCode() == 0) {
            CompanyAuthResult result = resultResponse.getResult();
            logger.info("请求成功，认证状态:{}", result.getStatus().toString());
        } else {
            logger.info("请求失败，错误码:{}，错误信息:{}", resultResponse.getCode(), resultResponse.getMessage());
        }
        System.out.println(JSONUtils.toJson(resultResponse));
    }

    public static void drawAndPageUrl(){
        SdkClient client = getClient();
        SdkResponse<Contract> draft = draft(client);

        Contract result = draft.getResult();
        Long id = result.getId();
        pageUrl(id);
    }


    public static void pageUrl(Long contractId){
        ContractPageRequest request = new ContractPageRequest(contractId,
                new User(mobile, "MOBILE"), "");
        String response = getClient().service(request);
        SdkResponse<ContractPageResult> responseObj = JSONUtils.toQysResponse(response, ContractPageResult.class);
        if(responseObj.getCode() == 0) {
            ContractPageResult result = responseObj.getResult();
            logger.info("合同页面地址为:{}", result.getPageUrl());
        } else {
            logger.info("请求失败，错误码:{}，错误信息:{}", responseObj.getCode(), responseObj.getMessage());
        }
    }

    public static void document()throws Exception{

        DocumentDownloadRequest request = new DocumentDownloadRequest(2879628702566580697L);
        FileOutputStream fos = new FileOutputStream("E:/test/doc.pdf");
        getClient().download(request, fos);
        IOUtils.safeClose(fos);
        logger.info("下载合同文档成功");
    }

    public static void person() {
        SdkClient sdkClient = getClient();
        UserAuthPageRequest request = new UserAuthPageRequest();

        request.setMode("IVS");
        request.setUser(new User(mobile, "MOBILE"));
        List<String> otherModes = new ArrayList<>();
        otherModes.add("ALIPAY");
        otherModes.add("MANUAL");
        request.setOtherModes(otherModes);

        String response = sdkClient.service(request);
        SdkResponse<UserAuthPageResult> responseObj = JSONUtils.toQysResponse(response, UserAuthPageResult.class);
        if (responseObj.getCode() == 0) {
            UserAuthPageResult result = responseObj.getResult();
            logger.info("请求成功，认证链接:{}", result.getAuthUrl());
        } else {
            logger.info("请求失败，错误码:{}，错误信息:{}", responseObj.getCode(), responseObj.getMessage());
        }
    }

    public static void viewPerson() {
        SdkClient sdkClient = getClient();
        UserAuthResultRequest request = new UserAuthResultRequest(new User("18301966691", "MOBILE"));
        String response = sdkClient.service(request);
        SdkResponse<UserAuthResult> responseObj = JSONUtils.toQysResponse(response, UserAuthResult.class);
        if (responseObj.getCode() == 0) {
            logger.info("请求成功，实名认证状态:{}", responseObj.getResult().getRealName().toString());
        } else {
            logger.info("请求失败，错误码:{}，错误信息:{}", responseObj.getCode(), responseObj.getMessage());
        }
    }

    public static void enterprise() {
        SdkClient sdkClient = getClient();
        User applicant = new User("王焕勇", "18301966691", "MOBILE");

        CompanyAuthH5PageRequest request = new CompanyAuthH5PageRequest(companyName, applicant);
        String response = sdkClient.service(request);
        SdkResponse<CompanyAuthPageResult> pageResultResponse = JSONUtils.toQysResponse(response, CompanyAuthPageResult.class);
        if (pageResultResponse.getCode() == 0) {
            logger.info("请求成功，认证链接:{}", pageResultResponse.getResult().getAuthUrl());
        } else {
            logger.info("请求失败，错误码:{}，错误信息:{}", pageResultResponse.getCode(), pageResultResponse.getMessage());
        }
        System.out.println(JSON.toJSONString(pageResultResponse));
    }


    //分类 2878178442010952646
    private static SdkResponse<Contract> draft(SdkClient sdkClient) {
        // 初始化sdkClient

//        SdkClient sdkClient = getClient();
// 合同基本参数
        Contract contract = new Contract();
        contract.setSubject("虎博测试合同0926-04");
        contract.setCategory(new Category(2878178442010952646L));
        contract.setBizId("0926-04");
//        contract.setSend(true);

        Signatory signatory1 = new Signatory();
        signatory1.setTenantName(CLIENT_COMPANY_NAME);
        signatory1.setTenantType("COMPANY");
        signatory1.setReceiver(new User("王焕勇", "18301966691", "MOBILE"));
        signatory1.setSerialNo(1);

        Signatory signatory2 = new Signatory();

        signatory2.setTenantName(myCompanyName);
        signatory2.setTenantType("COMPANY");
        signatory2.setReceiver(new User("信占轩", "13093553089", "MOBILE"));
        signatory2.setSerialNo(2);
        Action action = new Action("COMPANY", 2);
        signatory2.addAction(action);
// 设置签署方
        contract.addSignatory(signatory1);
        contract.addSignatory(signatory2);
// 设置模板参数
        contract.addTemplateParam(new TemplateParam("models", "模型名称参数-名称使用"));
//        contract.addTemplateParam(new TemplateParam("param2", "参数2"));
        contract.setSend(true);
// 设置模板参数：表格类型的参数
//List> tableVal = new ArrayList<>();
//Map row1 = new HashMap<>();
//row1.put("column1", "v11");
//row1.put("column2", "v12");
//row1.put("column3", "v13");
//Map row2 = new HashMap<>();
//row2.put("column1", "v21");
//row2.put("column2", "v22");
//row2.put("column3", "v23");
//tableVal.add(row1);
//tableVal.add(row2);
//contract.addTemplateParam(new TemplateParam("表格", JSONUtils.toJson(tableVal)));
// 创建合同
        ContractDraftRequest request = new ContractDraftRequest(contract);
        String response = sdkClient.service(request);
        SdkResponse<Contract> responseObj = JSONUtils.toQysResponse(response, Contract.class);
// 返回结果
        if(responseObj.getCode() == 0) {
            Contract result = responseObj.getResult();
            logger.info("创建合同成功，合同ID:{}", result.getId());
            System.out.println(JSONUtils.toJson(result));
        } else {
            logger.info("请求失败，错误码:{}，错误信息:{}", responseObj.getCode(), responseObj.getMessage());
        }
        return responseObj;
    }
}
