package com.tigerobo.pai.biz.test.eye;

import com.alibaba.fastjson.JSON;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.eye.req.ModelBoardReq;
import com.tigerobo.x.pai.biz.eye.BoardCallService;
import com.tigerobo.x.pai.biz.eye.offline.CallOfflineService;
import com.tigerobo.x.pai.biz.utils.TimeUtil;
import com.tigerobo.x.pai.dal.biz.dao.call.ModelCallDao;
import com.tigerobo.x.pai.dal.biz.entity.call.ModelCallPo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class BoardServiceTest extends BaseTest {


    @Autowired
    private BoardCallService boardCallService;


    @Autowired
    private ModelCallDao modelCallDao;

    @Autowired
    private CallOfflineService callOfflineService;
    @Test
    public void modalCallTest(){

        ModelBoardReq req = new ModelBoardReq();
        req.setUserId(3);
        List<ModelCallPo> callByDay = modelCallDao.getCallByDay(req, Arrays.asList(610762));

        System.out.println(JSON.toJSONString(callByDay));
    }

    @Test
    public void viewSourceTest(){

        ModelBoardReq req = new ModelBoardReq();
//        req.setUserId(3);
        req.setCallSource(2);
        BoardCallService.BoardStatistic statistic = boardCallService.statisticOnModelCall(req);

        System.out.println(JSON.toJSONString(statistic));
    }
    @Test
    public void refreshTest(){

        callOfflineService.refreshNum();
    }
    @Test
    public void refreshYesTest(){
        int dayValue = TimeUtil.getDayValue(new Date())-1;
        callOfflineService.refreshNum(dayValue);
    }

    @Test
    public void refreshSourceTest(){

        int day = 20211125;
        for (int i = 20211119; i <=day ; i++) {

        }
        callOfflineService.refreshSourceNum(day);
    }
}
