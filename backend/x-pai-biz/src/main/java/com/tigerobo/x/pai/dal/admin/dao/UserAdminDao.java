package com.tigerobo.x.pai.dal.admin.dao;

import com.github.pagehelper.PageHelper;
import com.tigerobo.x.pai.api.admin.req.user.UserSearchReq;
import com.tigerobo.x.pai.dal.auth.entity.UserDo;
import com.tigerobo.x.pai.dal.auth.mapper.UserMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Component
public class UserAdminDao {

    @Autowired
    private UserMapper userMapper;

    public List<UserDo> query(UserSearchReq req, List<Integer> blackUserIds){


        Example example = new Example(UserDo.class);

        final Example.Criteria criteria = example.createCriteria();

        if (req.getId()!=null){
            criteria.andEqualTo("id",req.getId());
            buildBlack(blackUserIds, criteria);
        }else if (StringUtils.isNotBlank(req.getUuid())){
            criteria.andEqualTo("uuid",req.getUuid());
            buildBlack(blackUserIds, criteria);
        }else if (StringUtils.isNotBlank(req.getAccount())){
            criteria.andEqualTo("account",req.getAccount());
            buildBlack(blackUserIds, criteria);
        }else {

            if (StringUtils.isNotBlank(req.getMobile())){
                criteria.andEqualTo("mobile",req.getMobile());
            }
            if (StringUtils.isNotBlank(req.getWechatUUid())){
                criteria.andEqualTo("wechat",req.getWechatUUid());
            }
            buildBlack(blackUserIds, criteria);
            if (req.getRole()!=null){
                criteria.andEqualTo("role",req.getRole());
            }
            if (StringUtils.isNotBlank(req.getAccessSource())){
                criteria.andEqualTo("accessSource",req.getAccessSource());
            }

            if (req.getStartCreateTime()!=null){
                criteria.andGreaterThanOrEqualTo("createTime",req.getStartCreateTime());
            }
            if (req.getEndCreateTime()!=null){
                criteria.andGreaterThanOrEqualTo("createTime",req.getEndCreateTime());
            }
            if (StringUtils.isNotBlank(req.getName())){
                criteria.andLike("name","%"+req.getName()+"%");
            }
            if (StringUtils.isNotBlank(req.getWechatName())){
                criteria.andLike("wechatName","%"+req.getWechatName()+"%");
            }
        }

        criteria.andEqualTo("isDeleted",false);
        PageHelper.startPage(req.getPageNum(),req.getPageSize());
        return userMapper.selectByExample(example);

    }

    private void buildBlack(List<Integer> blackUserIds, Example.Criteria criteria) {
        if (!CollectionUtils.isEmpty(blackUserIds)){
            criteria.andIn("userId", blackUserIds);
        }
    }
}
