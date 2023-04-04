package com.tigerobo.pai.biz.test.service.test.pub;

import com.alibaba.fastjson.JSON;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.vo.biz.pub.BigShotQueryReq;
import com.tigerobo.x.pai.api.vo.user.follow.FollowVo;
import com.tigerobo.x.pai.biz.biz.pub.MediaService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class MediaServiceTest extends BaseTest {

    @Autowired
    private MediaService mediaService;

    @Test
    public void searchTest(){

        BigShotQueryReq req = new BigShotQueryReq();
        List<FollowVo> search = mediaService.search(req);

        System.out.println(search.size());
        System.out.println(JSON.toJSONString(search));
        for (FollowVo followVo : search) {
            System.out.println(followVo.getBizType()+"\t"+followVo.getName()+"\t"+followVo.getPlatformName());
        }

    }
}
