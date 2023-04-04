package com.tigerobo.x.pai.dal.auth.dao;

import com.tigerobo.x.pai.dal.auth.entity.MemberPo;
import com.tigerobo.x.pai.dal.auth.mapper.MemberMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Slf4j
@Component
public class MemberDao {

    @Autowired
    private MemberMapper memberMapper;

    public void add(MemberPo po){
        memberMapper.insertSelective(po);
    }

    public int update(MemberPo update){
        return memberMapper.updateByPrimaryKeySelective(update);
    }
    public MemberPo get(Integer userId){

        Example example = new Example(MemberPo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("isDeleted",0);

        List<MemberPo> memberPos = memberMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(memberPos)){
            return null;
        }
        return memberPos.get(0);
    }
}
