package com.tigerobo.pai.biz.test.service.test.uc;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.enums.BusinessEnum;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.biz.PageReqVo;
import com.tigerobo.x.pai.api.vo.user.comment.CommentQueryReq;
import com.tigerobo.x.pai.api.vo.user.thumb.ThumbChainVo;
import com.tigerobo.x.pai.api.vo.user.thumb.ThumbReq;
import com.tigerobo.x.pai.biz.user.comment.BusinessCommentService;
import com.tigerobo.x.pai.biz.user.thumb.ThumbOptService;
import com.tigerobo.x.pai.biz.user.thumb.UserThumbService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.dal.biz.entity.BizCountPo;
import com.tigerobo.x.pai.dal.biz.mapper.user.UserThumbMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ThumbServiceTest extends BaseTest {

    @Autowired
    private BusinessCommentService businessCommentService;

    @Autowired
    private UserThumbService userThumbService;

    @Autowired
    private UserThumbMapper userThumbMapper;

    @Autowired
    private ThumbOptService thumbOptService;

    @Test
    public void thumbTest(){
        ThumbReq req = new ThumbReq();
        req.setBizId("625");
        req.setBizType(BusinessEnum.ART_IMAGE.getType());
        thumbOptService.thumbUp(req);
    }
    @Test
    public void groupCountTest(){

        Integer type = 31;
        PageHelper.startPage(2,10);
        final List<BizCountPo> list = userThumbMapper.groupBizCount(type);

        for (BizCountPo o : list) {
            System.out.println(o.toString());
        }
        Page page = (Page) list;
        System.out.println("total:"+page.getTotal());

    }

    @Test
    public void reloadTest(){

        thumbOptService.reloadBizCache(BusinessEnum.ART_IMAGE.getType());
    }
    @Test
    public void getTest(){
        CommentQueryReq req = new CommentQueryReq();

        businessCommentService.getBizCommentList(req);
    }

    @Test
    public void getMyThumbListTest(){

        ThreadLocalHolder.setUserId(3);


        final long l = System.currentTimeMillis();
        final PageVo<ThumbChainVo> thumb2me = userThumbService.getMyThumbPage(new PageReqVo());

        System.out.println(System.currentTimeMillis()-l);

        System.out.println(JSON.toJSONString(thumb2me));

    }


    @Test
    public void cancelThumbTest(){

        ThreadLocalHolder.setUserId(47);

        ThumbReq req = new ThumbReq();

        req.setActionType(0);
        req.setBizType(BusinessEnum.ART_IMAGE.getType());
        req.setBizId("638");

        thumbOptService.cancelThumbUp(req);

    }

    @Test
    public void getThumb2meListTest(){

        ThreadLocalHolder.setUserId(3);


        final long l = System.currentTimeMillis();
        final PageVo<ThumbChainVo> thumb2me = userThumbService.getThumb2mePage(new PageReqVo());

        System.out.println(System.currentTimeMillis()-l);

        System.out.println(JSON.toJSONString(thumb2me));

    }
}
