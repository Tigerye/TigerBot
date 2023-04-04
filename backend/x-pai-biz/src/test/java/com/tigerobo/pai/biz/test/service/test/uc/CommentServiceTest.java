package com.tigerobo.pai.biz.test.service.test.uc;

import com.alibaba.fastjson.JSON;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.biz.blog.req.UserPageReq;
import com.tigerobo.x.pai.api.vo.user.comment.CommentChainVo;
import com.tigerobo.x.pai.api.vo.user.comment.CommentQueryReq;
import com.tigerobo.x.pai.api.vo.user.comment.CommentVo;
import com.tigerobo.x.pai.biz.user.comment.BusinessCommentService;
import com.tigerobo.x.pai.biz.user.comment.UserCommentService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CommentServiceTest extends BaseTest {
    @Autowired
    private BusinessCommentService businessCommentService;

    @Autowired
    private UserCommentService userCommentService;

    @Test
    public void commentListTest(){

        UserPageReq req = new UserPageReq();
        ThreadLocalHolder.setUserId(3);
        final long l = System.currentTimeMillis();
        final PageVo<CommentChainVo> commentList = userCommentService.getMyCommentList(req);

        System.out.println("ll delta:"+(System.currentTimeMillis()-l));
        System.out.println(JSON.toJSONString(commentList));
    }

    @Test
    public void getTest(){

        CommentQueryReq req = new CommentQueryReq();

        req.setBizId("953e84a54e3940a3a4a331453176ec76");
        req.setBizType(2);
        req.setPageSize(100);
        ThreadLocalHolder.setUserId(18);
        PageVo<CommentVo> bizCommentList = businessCommentService.getBizCommentList(req);

        System.out.println(bizCommentList);
    }

    @Test
    public void getListTest(){

    }

    @Test
    public void getMineListTest(){

//        commentService.getUserCommentList()
    }
}
