package com.tigerobo.pai.common.util.qiyuesuo;

import com.qiyuesuo.sdk.v2.SdkClient;
import com.qiyuesuo.sdk.v2.bean.*;
import com.qiyuesuo.sdk.v2.json.JSONUtils;
import com.qiyuesuo.sdk.v2.param.SignParam;
import com.qiyuesuo.sdk.v2.request.ContractDraftRequest;
import com.qiyuesuo.sdk.v2.request.ContractPageRequest;
import com.qiyuesuo.sdk.v2.request.ContractSignCompanyRequest;
import com.qiyuesuo.sdk.v2.response.ContractPageResult;
import com.qiyuesuo.sdk.v2.response.SdkResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.tigerobo.pai.common.util.qiyuesuo.QysUserTest.getClient;

/**
 * 创建合同草稿-发起合同-签署方签署-完成签署
 * @author Administrator
 *
 */
public class ContractProcess {

	private static final Logger logger = LoggerFactory.getLogger(ContractProcess.class);

	public static final String CLIENT_COMPANY_NAME = "上海虎烨信息科技有限公司";

    public static String myCompanyName = "虎博科技技术有限公司";
    public static void main(String[] args) {
        draft();
    }
	public static void draft(){
        // 初始化sdkClient

        SdkClient sdkClient = getClient();
// 合同基本参数
        Contract contract = new Contract();
        contract.setSubject("虎博测试合同0926-03");
        contract.setCategory(new Category(2878178442010952646L));
        contract.setBizId("0926-03");
        contract.setSend(false);

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
        contract.addTemplateParam(new TemplateParam("models", "模型名称参数1"));
        contract.addTemplateParam(new TemplateParam("param2", "参数2"));
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
    }


}
