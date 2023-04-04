package com.tigerobo.pai.biz.test.service.test.blog;

import com.alibaba.fastjson.JSON;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.vo.biz.blog.BlogChatVo;
import com.tigerobo.x.pai.api.vo.biz.blog.BlogMainChatVo;
import com.tigerobo.x.pai.biz.biz.blog.BlogChatService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class BlogChatServiceTest extends BaseTest {

    @Autowired
    private BlogChatService blogChatService;

    @Test
    public void pageTest(){


        String chatId = "1458762069445156871";

        BlogMainChatVo chatDetail = blogChatService.getChatDetail(chatId);
        System.out.println(JSON.toJSONString(chatDetail));
    }

    @Test
    public void blogIdTest(){
        Integer blogId = 95929;

        List<BlogChatVo> vos = blogChatService.getBlogChainByBlogId(blogId, null);
        System.out.println(JSON.toJSONString(vos));
    }

}
