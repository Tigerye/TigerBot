package com.tigerobo.pai.biz.test.service.test;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.vo.TaskQueryVo;
import com.tigerobo.x.pai.api.vo.biz.TaskVo;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.QueryVo;
import com.tigerobo.x.pai.biz.biz.service.WebTaskService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TaskServiceTest extends BaseTest {

    @Autowired
    private WebTaskService webTaskService;


    @Test
    public void test(){

        String uid = "open_qa";

        TaskVo detail = webTaskService.getDetailByAppName(uid);

        System.out.println(JSON.toJSONString(detail));
    }
    @Test
    public void getMineTest(){

        PageInfo<TaskVo> homeTask = webTaskService.getHomeTask(3);

        System.out.println(JSON.toJSONString(homeTask.getList()));
    }
    @Test
    public void queryTest(){

        String json = "{\"pageNum\":1,\"pageSize\":100,\"params\":{\"type\":\"TASK\",\"status\":\"COMPLETED\",\"tag\":[\"internet\"]},\"authorization\":{\"token\":\"157b9143bdaaabd14f9919c1eec2240f\",\"uid\":\"ba78b5ba8483a0a40ad0b480b82b2916\"}}";

//        json = "{\"pageNum\":1,\"pageSize\":100,\"params\":{\"type\":\"TASK\",\"status\":\"COMPLETED\",\"tag\":[\"internet\"]},\"authorization\":{\"token\":\"157b9143bdaaabd14f9919c1eec2240f\",\"uid\":\"ba78b5ba8483a0a40ad0b480b82b2916\"}}";
//        QueryVo queryVo = JSON.parseObject(json,QueryVo.class);

        long start = System.currentTimeMillis();
        TaskQueryVo queryVo = new TaskQueryVo();


//        queryVo.setBaseModelUid("machine-translation");
//        queryVo.setIndustryTagUid("Internet");
//        queryVo.setIndustryTagUid("Internet");

        queryVo.setKeyword("GPT");
//        ThreadLocalHolder.setUserId(3);
        PageVo<TaskVo> query = webTaskService.query(queryVo);
        System.out.println("delta:"+(System.currentTimeMillis()-start));
        System.out.println(JSON.toJSONString(query));



    }
}
