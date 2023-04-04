package com.tigerobo.pai.biz.test.service.test.blog;

import com.alibaba.fastjson.JSON;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.biz.blog.BlogVo;
import com.tigerobo.x.pai.api.vo.biz.blog.req.BlogHomeTabReq;
import com.tigerobo.x.pai.biz.biz.blog.BlogRecommendService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class BlogRecommendTest extends BaseTest {
    @Autowired
    private BlogRecommendService blogRecommendService;

    @Test
    public void getPageList(){
        BlogHomeTabReq req=new  BlogHomeTabReq();
//        req.setTab("blackTech");

        req.setTab("blackTech");
        final PageVo<BlogVo> pageList = blogRecommendService.getPageList(req);

        System.out.println(JSON.toJSONString(pageList));
    }
}
