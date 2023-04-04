package com.tigerobo.pai.biz.test.service.test.blog;


import com.alibaba.fastjson.JSON;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.dto.UserCommitSiteDto;
import com.tigerobo.x.pai.api.enums.BlogSourceFromEnum;
import com.tigerobo.x.pai.api.req.UserCommitPageReq;
import com.tigerobo.x.pai.api.req.UserCommitSiteReq;
import com.tigerobo.x.pai.api.req.UserNotifyPageReq;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.notify.NotifyCountVo;
import com.tigerobo.x.pai.api.vo.notify.NotifyVo;
import com.tigerobo.x.pai.biz.biz.NotifyService;
import com.tigerobo.x.pai.biz.biz.member.MemberService;
import com.tigerobo.x.pai.biz.biz.pub.MediaService;
import com.tigerobo.x.pai.biz.user.UserCommitService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class BlogMemberTest extends BaseTest {


    @Autowired
    private UserCommitService userCommitService;

    @Autowired
    private NotifyService notifyService;

    @Autowired
    private MemberService memberService;

    @Test
    public void adoptMemberTest(){


        int userId =3;
        boolean member = memberService.isMember(userId);
        System.out.println("isMember pre:"+member);
        memberService.adoptMember(userId);

        member = memberService.isMember(userId);
        System.out.println("isMember pre:"+member);
        memberService.adoptMember(userId);
    }

    @Test
    public void addMediaTest(){
        UserCommitSiteReq req =new UserCommitSiteReq();
        ThreadLocalHolder.setUserId(3);
        req.setName("马斯克");

        Integer commitId = userCommitService.addCommit(req);
        Integer mediaType = BlogSourceFromEnum.BIG_SHOT.getType();

        Integer mediaId = 31;


        printPage();
    }

    @Test
    public void commitSuccessTest(){
        int commitId = 9;
        Integer mediaType = BlogSourceFromEnum.SITE.getType();Integer mediaId = 8;

        userCommitService.commitSuccess(commitId,mediaType,mediaId);
    }
    @Test
    public void commitFailTest(){
        int commitId =    9;
        Integer mediaType = BlogSourceFromEnum.BIG_SHOT.getType();

        Integer mediaId = 31;
        userCommitService.commitFail(commitId,"搜索源违规");
    }


    @Test
    public void addMediaFailTest(){
        UserCommitSiteReq req =new UserCommitSiteReq();
        ThreadLocalHolder.setUserId(3);
        req.setName("蔡英文");

        Integer commitId = userCommitService.addCommit(req);
        Integer mediaType = BlogSourceFromEnum.BIG_SHOT.getType();

        Integer mediaId = 31;

        userCommitService.commitFail(commitId,"搜索源违规");
        printPage();
    }

    @Test
    public void getUserCommitTest(){

        printPage();
    }

    private void printPage() {
        UserCommitPageReq req = new UserCommitPageReq();
        req.setUserId(3);
        ThreadLocalHolder.setUserId(3);

        PageVo<UserCommitSiteDto> userCommitPage = userCommitService.getUserCommitPage(req);

        System.out.println(JSON.toJSONString(userCommitPage));
    }

    @Test
    public void notifyListTest(){

        UserNotifyPageReq req = new UserNotifyPageReq();

        ThreadLocalHolder.setUserId(18);

        PageVo<NotifyVo> notifyPage = notifyService.getNotifyPage(req);

        System.out.println(JSON.toJSONString(notifyPage));
        int i = notifyService.countUnRead();
        System.out.println(i);
    }

    @Test
    public void notifyCountTest(){

        ThreadLocalHolder.setUserId(3);
        int i = notifyService.countUnRead();
        System.out.println(i);

    }

    @Test
    public void notifyCountGroupTest(){

        ThreadLocalHolder.setUserId(18);
        final List<NotifyCountVo> vos = notifyService.countUnReadGroupByType();
        System.out.println(JSON.toJSONString(vos));

    }
}
