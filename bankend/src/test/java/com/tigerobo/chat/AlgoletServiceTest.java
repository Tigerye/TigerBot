package com.tigerobo.chat;

import com.tigerbot.chat.service.AlgoletService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class AlgoletServiceTest extends BaseTest {

    @Autowired
    private AlgoletService algoletService;

    @Test
    public void reqImgTest(){

        String text = "一只哈巴狗";
        final Long reqId = algoletService.imgProduceReq(text);
        System.out.println("reqId="+reqId);

    }

}
