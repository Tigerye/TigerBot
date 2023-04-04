package com.tigerobo.x.pai.biz.biz.member;

import com.alibaba.fastjson.JSON;
import com.tigerobo.x.pai.api.dto.MemberDto;
import com.tigerobo.x.pai.api.enums.MemberLevelEnum;
import com.tigerobo.x.pai.api.vo.biz.PageReqVo;
import com.tigerobo.x.pai.dal.auth.dao.MemberChangeLogDao;
import com.tigerobo.x.pai.dal.auth.dao.MemberDao;
import com.tigerobo.x.pai.dal.auth.entity.MemberChangeLogPo;
import com.tigerobo.x.pai.dal.auth.entity.MemberPo;
import com.tigerobo.x.pai.dal.pay.dao.SkuDao;
import com.tigerobo.x.pai.dal.pay.entity.ProductSkuPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

@Slf4j
@Service
public class MemberService {

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private SkuDao skuDao;

    @Autowired
    private MemberChangeLogDao memberChangeLogDao;
    public boolean isMember(Integer userId) {
        if (userId == null||userId==0){
            return false;
        }
        MemberPo memberPo = memberDao.get(userId);
        return isMember(memberPo);
    }

    private boolean isMember(MemberPo memberPo) {
        if (memberPo == null) {
            return false;
        }
        return memberPo.getStatus() == 1 && memberPo.getExpireDate().after(new Date());
    }

    public MemberDto getMember(Integer userId) {
        MemberPo memberPo = memberDao.get(userId);
        MemberDto memberDto = new MemberDto();
        boolean member = isMember(memberPo);
        memberDto.setIsMember(member);
        if (member) {
            memberDto.setExpireDate(memberPo.getExpireDate());
            memberDto.setLevel(memberPo.getLevel());
            MemberLevelEnum levelEnum = MemberLevelEnum.getByLevel(memberPo.getLevel());
            if (levelEnum == null){
                memberDto.setLevelName("");
            }else {
                memberDto.setLevelName(levelEnum.getName());
            }
        }
        return memberDto;
    }

    public void adoptMember(Integer userId){
        boolean member = isMember(userId);
        if (member){
            throw new IllegalArgumentException("用户已领取会员");
        }

        int skuId = 1;
        ProductSkuPo skuPo = skuDao.load(skuId);


        addMember(userId,skuPo,null);
    }


    public void addMember(Integer userId, ProductSkuPo skuPo,Long orderNo) {
        Validate.isTrue(userId!=null&&userId>0,"未传用户");
        Validate.isTrue(skuPo!=null,"未传产品");
        Integer plusDay = skuPo.getDays();
        if (skuPo.getProductId()!=1||plusDay<=0){
            log.warn("addMember :{},{},{},no days add",userId, JSON.toJSON(skuPo),orderNo);
            return;
        }
        Integer level = skuPo.getLevel();
        Date now = new Date();
        Date today = DateUtils.addSeconds(DateUtils.addDays(DateUtils.truncate(now, Calendar.DAY_OF_MONTH), 1), -1);
        MemberPo memberPo = memberDao.get(userId);
        Date expireDate = DateUtils.addDays(today,plusDay);

        MemberChangeLogPo memberChangeLogPo = new MemberChangeLogPo();
        memberChangeLogPo.setUserId(userId);
        memberChangeLogPo.setMemberLevel(level);
        memberChangeLogPo.setOrderNo(orderNo);
        memberChangeLogPo.setDesc(skuPo.getDesc());

        memberChangeLogPo.setDays(skuPo.getDays());
        memberChangeLogPo.setName(skuPo.getName());
        memberChangeLogPo.setMemberLevel(skuPo.getLevel());
        if (memberPo != null) {
            boolean member = isMember(memberPo);
            if (member) {
                Date dbExpireDate = memberPo.getExpireDate();
                if (dbExpireDate.after(today)){
                    expireDate = DateUtils.addDays(dbExpireDate,plusDay);
                }
            }
            MemberPo updatePo = new MemberPo();
            updatePo.setId(memberPo.getId());
            updatePo.setExpireDate(expireDate);
            updatePo.setStatus(1);
            if (memberPo.getLevel()==null||level>memberPo.getLevel()){
                updatePo.setLevel(level);
            }
            int update = memberDao.update(updatePo);
            if (update<1){
                log.error("addMember fail,userId:{},orderNo:{},skuId:{}",userId,orderNo,skuPo.getId());
                throw new IllegalArgumentException("会员更新失败");
            }
            memberChangeLogPo.setPreTime(memberPo.getExpireDate());
            memberChangeLogPo.setExpireTime(expireDate);
        }else {
            MemberPo addPo = new MemberPo();
            addPo.setUserId(userId);
            addPo.setExpireDate(expireDate);
            addPo.setLevel(level);
            addPo.setStatus(1);
            memberDao.add(addPo);
            memberChangeLogPo.setExpireTime(expireDate);
        }
        memberChangeLogDao.addLog(memberChangeLogPo);
    }
}
