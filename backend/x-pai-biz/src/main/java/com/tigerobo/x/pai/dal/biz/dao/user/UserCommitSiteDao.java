package com.tigerobo.x.pai.dal.biz.dao.user;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.tigerobo.x.pai.api.admin.req.UserCommitSiteAdminReq;
import com.tigerobo.x.pai.api.vo.biz.PageReqVo;
import com.tigerobo.x.pai.dal.admin.mapper.UserCommitSiteAdminMapper;
import com.tigerobo.x.pai.dal.biz.entity.user.UserCommitSitePo;
import com.tigerobo.x.pai.dal.biz.mapper.user.UserCommitSiteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Component
public class UserCommitSiteDao {

    @Autowired
    private UserCommitSiteMapper userCommitSiteMapper;
    @Autowired
    private UserCommitSiteAdminMapper userCommitSiteAdminMapper;


    public UserCommitSitePo load(Integer id){

        UserCommitSitePo po = userCommitSiteMapper.selectByPrimaryKey(id);
        if (po == null){
            return null;
        }
        if (po.getIsDeleted() == null||po.getIsDeleted()){
            return null;
        }
        return po;
    }

    public List<UserCommitSitePo> getByIds(List<Integer> ids){

        Example example = new Example(UserCommitSitePo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id",ids);

        criteria.andEqualTo("isDeleted",0);

        return userCommitSiteMapper.selectByExample(example);

    }
    public List<UserCommitSitePo> getOnlyByIds(List<Integer> ids){
        if (CollectionUtils.isEmpty(ids)) {
            return null;
        }

        Example example = new Example(UserCommitSitePo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id",ids);

        return userCommitSiteMapper.selectByExample(example);

    }

    public int update(UserCommitSitePo po){
        return userCommitSiteMapper.updateByPrimaryKeySelective(po);
    }
    public void add(UserCommitSitePo po){
        userCommitSiteMapper.insertSelective(po);
    }


    public Page<UserCommitSitePo> getPage(PageReqVo reqVo,Integer userId){

        PageHelper.startPage(reqVo.getPageNum(),reqVo.getPageSize());

        Example example = new Example(UserCommitSitePo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("isDeleted",0);

        example.setOrderByClause("id desc");

        return (Page<UserCommitSitePo>)userCommitSiteMapper.selectByExample(example);
    }

    public Page<Integer> getUserComSiteIds(UserCommitSiteAdminReq req) {
        PageHelper.startPage(req.getPageNum(), req.getPageSize());

        return (Page<Integer>) userCommitSiteAdminMapper.query(req);
    }
}
