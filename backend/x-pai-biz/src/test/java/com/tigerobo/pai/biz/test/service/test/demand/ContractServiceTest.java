package com.tigerobo.pai.biz.test.service.test.demand;

import com.alibaba.fastjson.JSON;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.vo.biz.contract.ContractDraftVo;
import com.tigerobo.x.pai.api.vo.biz.req.DemandSignContractReq;
import com.tigerobo.x.pai.biz.biz.service.DemandContractService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ContractServiceTest extends BaseTest {

    @Autowired
    private DemandContractService demandContractService;

    @Test
    public void generateContractTest(){
        DemandSignContractReq req = new DemandSignContractReq();
        req.setUuid("71d137714bb84f43f30a6092081df0d6");

        ThreadLocalHolder.setUserId(3);

        ContractDraftVo contractDraftVo = demandContractService.generateDemandContractDraft(req);

        System.out.println(JSON.toJSONString(contractDraftVo));
    }

    @Test
    public void viewTest(){
        DemandSignContractReq req = new DemandSignContractReq();
        req.setUuid("a943cd0eed091453a89a97ab1907244e");

        ThreadLocalHolder.setUserId(3);

        ContractDraftVo contractDraftVo = demandContractService.view(req);

        System.out.println(JSON.toJSONString(contractDraftVo));
    }
}
