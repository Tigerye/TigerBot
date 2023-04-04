package com.tigerobo.pai.biz.test.service.test.demand;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.vo.biz.DemandPhase;
import com.tigerobo.x.pai.api.vo.biz.DemandVo;
import com.tigerobo.x.pai.biz.biz.service.WebDemandService;
import com.tigerobo.x.pai.biz.biz.service.DemandServiceImpl;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class DemandTest extends BaseTest {

    @Autowired
    private WebDemandService webDemandService;
    @Autowired
    private DemandServiceImpl demandServiceV2;

    @Test
    public void updateTest(){
//
//        DemandDo demandUpdate = new DemandDo();
//        demandUpdate.setId(100000164);
//        demandUpdate.setContractId(12350);
//        demandUpdate.setPhase(Demand.Phase.HAS_SIGN_CONTRACT.getVal());
//        demandUpdate.setContractCompleteTime(new Date());
//        demandDao.update(demandUpdate);

    }

    @Test
    public void auditDeclineTest(){


        String uuid = "a5ff73a254a32eeda7fa612893067cd9";
        String reason = "test";
        Integer userId = 3;
        webDemandService.auditDecline(uuid,reason,userId);
    }


    @Test
    public void delTest(){
        String uuid = "c137725ce4fb9806a1c97b77bed9851c";

        String reason = "删除测试";

        ThreadLocalHolder.setUserId(3);
        webDemandService.deleteDemand(uuid,reason);
    }
    @Test
    public void mineDemandTest(){

        PageInfo<DemandVo> userDemand = demandServiceV2.getUserDemand(18);
        System.out.println(JSON.toJSONString(userDemand.getList()));

    }

    @Test
    public void getPhaseTest(){
        String demandId= "ab6c9eb7d04473deb82d87223044d4f8";
        List<DemandPhase> demandPhase = webDemandService.getDemandPhase(demandId);
        System.out.println(JSON.toJSONString(demandPhase));
    }
    @Test
    public void countTest(){

        PageInfo<DemandVo> topList = demandServiceV2.getTopList();

        for (DemandVo demandVo : topList.getList()) {
            System.out.println(demandVo.getDemand().getName());
        }

    }
}
