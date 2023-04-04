package com.tigerobo.x.pai.dal.biz.dao.user;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.tigerobo.x.pai.api.req.UserNotifyPageReq;
import com.tigerobo.x.pai.api.vo.biz.PageReqVo;
import com.tigerobo.x.pai.dal.biz.entity.user.UserNotifyPo;
import com.tigerobo.x.pai.dal.biz.mapper.user.UserNotifyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;

@Component
public class UserNotifyDao {

    @Autowired
    private UserNotifyMapper userNotifyMapper;

    public void add(UserNotifyPo po){
        userNotifyMapper.insertSelective(po);
    }

    public void update(UserNotifyPo po){
        userNotifyMapper.updateByPrimaryKeySelective(po);
    }

    public Page<UserNotifyPo> getUserNotifyPage(PageReqVo reqVo, Integer notifyType,Integer userId){
        PageHelper.startPage(reqVo.getPageNum(),reqVo.getPageSize());

        Example example = new Example(UserNotifyPo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);

        if (notifyType!=null){
            criteria.andEqualTo("notifyType",notifyType);
        }

        criteria.andEqualTo("isDeleted",0);

        example.setOrderByClause("id desc");
        return (Page<UserNotifyPo>)userNotifyMapper.selectByExample(example);
    }

    public int countUnRead(Date preTime,Integer userId,Integer notifyType){
        Example example = new Example(UserNotifyPo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("notifyType",notifyType);
        if (preTime!=null){
            criteria.andGreaterThan("createTime",preTime);
        }
        criteria.andEqualTo("isDeleted",0);
        return userNotifyMapper.selectCountByExample(example);
    }
}
