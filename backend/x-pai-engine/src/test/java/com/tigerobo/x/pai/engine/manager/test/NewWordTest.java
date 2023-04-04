package com.tigerobo.x.pai.engine.manager.test;

import com.tigerobo.x.pai.engine.EngineBaseTest;
import com.tigerobo.x.pai.engine.model.NewWordModelManager;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

public class NewWordTest extends EngineBaseTest {

    @Autowired
    private NewWordModelManager newWordModelManager;

    @Test
    public void addDayTest()throws Exception{

        int start = 20211005;
        int end =  20211007;

        int day = start;

        while (day<=end){
            newWordModelManager.addDay(day);
            day++;
        }


    }
}
