package com.tigerobo.x.pai.biz.biz;

import com.tigerobo.x.pai.api.exception.AuthorizeException;
import com.tigerobo.x.pai.api.exception.ResultCode;
import com.tigerobo.x.pai.biz.biz.member.MemberService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ActivityMemberService {

    @Autowired
    private MemberService memberService;
    public void adoptMember(){

        Integer userId = ThreadLocalHolder.getUserId();
        if (userId == null||userId<=0){
            throw new AuthorizeException(ResultCode.USER_NOT_LOGIN);
        }

        memberService.adoptMember(userId);
        //todo addMember

    }
}
