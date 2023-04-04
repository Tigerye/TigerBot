package com.tigerobo.pai.biz.test.ai;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.ai.base.IAiUserInteract;
import com.tigerobo.x.pai.api.ai.req.interact.UserInteractPublicPageReq;
import com.tigerobo.x.pai.api.enums.BusinessEnum;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.biz.ai.AiUserInteractService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.dal.ai.dao.AiUserInterActDao;
import com.tigerobo.x.pai.dal.ai.entity.AiUserInteractPo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class AiUserInteractServiceTest extends BaseTest {
    @Autowired
    private AiUserInteractService aiUserInteractService;


    @Autowired
    private AiUserInterActDao aiUserInterActDao;

    @Test
    public void getInterActListTest(){
        UserInteractPublicPageReq req = new UserInteractPublicPageReq();
        req.setPageNum(3);
        req.setPageSize(10);
        final PageVo<? extends IAiUserInteract> newPublishList = aiUserInteractService.getNewPublishList(req);

        System.out.println(JSON.toJSONString(newPublishList));
    }
    @Test
    public void myListTest(){
        UserInteractPublicPageReq req = new UserInteractPublicPageReq();
        req.setPageNum(3);
        req.setPageSize(10);
        ThreadLocalHolder.setUserId(3);
//        req.setBizType(BusinessEnum.STYLE_TRANSFER.getType());
        final PageVo<IAiUserInteract> myList = aiUserInteractService.getMyList(req);

        System.out.println(JSON.toJSONString(myList));

    }


    @Test
    public void publishListTest(){
        UserInteractPublicPageReq req = new UserInteractPublicPageReq();
//        ThreadLocalHolder.setUserId(3);
//        req.setBizType(BusinessEnum.STYLE_TRANSFER.getType());
//        req.setTabType("hot");
        final PageVo publishList = aiUserInteractService.getNewPublishList(req);

        System.out.println(JSON.toJSONString(publishList));

    }
}
