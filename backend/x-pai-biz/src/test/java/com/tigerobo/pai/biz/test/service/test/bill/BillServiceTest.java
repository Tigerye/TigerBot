package com.tigerobo.pai.biz.test.service.test.bill;

import com.alibaba.fastjson.JSON;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.enums.ModelCallSourceEnum;
import com.tigerobo.x.pai.api.vo.biz.bill.UserBillReq;
import com.tigerobo.x.pai.api.vo.biz.bill.UserCallBillVo;
import com.tigerobo.x.pai.biz.biz.BillService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class BillServiceTest extends BaseTest {

    @Autowired
    private BillService billService;


    @Test
    public void test(){

        UserBillReq req = new UserBillReq();
        req.setUserId(15);

        ThreadLocalHolder.setUserId(15);
        req.setCallSource(ModelCallSourceEnum.INVOKE.getType());

        UserCallBillVo userBill = billService.getUserBill(req);

        System.out.println(JSON.toJSONString(userBill));
        for (UserCallBillVo.UserModelBillVo userModelBillVo : userBill.getModelCallList()) {
            System.out.println(userModelBillVo.getModelName()+"\t"+userModelBillVo.getNum()+"\t"+userModelBillVo.getCallSource());
        }

    }
}
