package com.tigerobo.pai.biz.test.service.test;

import com.alibaba.fastjson.JSON;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.dto.ModelCommitDto;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.user.UserModelVo;
import com.tigerobo.x.pai.biz.user.UserModelService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class UserTaskServiceTest extends BaseTest {

    @Autowired
    private UserModelService userTaskService;

    @Test
    public void queryTest(){
        int userId = 11;
        ThreadLocalHolder.setUserId(userId);
        PageVo<UserModelVo> userTask = userTaskService.getUserTaskList(userId);

        System.out.println(JSON.toJSONString(userTask.getList()));
    }

    @Test
    public void detailTest(){

        String uuid = "60fc79acdb834c87a7c54f2b3c4b8e08";
        UserModelVo modelDetail = userTaskService.getModelDetail(uuid);

        System.out.println(JSON.toJSONString(modelDetail));

    }

    @Test
    public void addCommitTest(){


        ModelCommitDto commitDto = new ModelCommitDto();

        String uuid = "096302a148a04a57ed5cd3f94f3c18a4";
        commitDto.setModelId(uuid);
        commitDto.setFilePath("http://www.tigerobo.com/1.zip");
        commitDto.setName("wsen-test");
        commitDto.setMemo("wsen-memo");


        ThreadLocalHolder.setUserId(3);
        userTaskService.addCommit(commitDto);
    }

    @Test
    public void getCommitTest(){

        String modelId = "096302a148a04a57ed5cd3f94f3c18a4";
        ThreadLocalHolder.setUserId(3);
        List<ModelCommitDto> commitList = userTaskService.getCommitList(modelId);

        System.out.println(JSON.toJSONString(commitList));

    }

    @Test
    public void delCommitTest(){

        userTaskService.deleteCommit(1);
    }
}
