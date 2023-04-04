package com.tigerobo.pai.biz.test.ai;

import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.ai.req.ArtImageOnlineReq;
import com.tigerobo.x.pai.biz.ai.multi.object.track.AiMultiObjectTrackService;
import com.tigerobo.x.pai.biz.ai.spatio.action.AiSpatioActionOperateService;
import com.tigerobo.x.pai.biz.ai.spatio.action.AiSpatioActionService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SpatioActionTest extends BaseTest {

    @Autowired
    private AiSpatioActionService aiSpatioActionService;

    @Autowired
    private AiSpatioActionOperateService aiSpatioActionOperateService;

    @Autowired
    private AiMultiObjectTrackService aiMultiObjectTrackService;
    @Test
    public void runTest(){
        aiSpatioActionService.dealPrepareTask(true);
    }
    @Test
    public void runMultiObjTrackTest(){
        aiMultiObjectTrackService.dealPrepareTask(true);
    }

    @Test
    public void getList(){
        ArtImageOnlineReq req = new ArtImageOnlineReq();
        req.setId(492);
        req.setDesc("test desc");
        req.setTitle("test title");
        ThreadLocalHolder.setUserId(3);
        aiSpatioActionOperateService.online(req,null);

    }
}
