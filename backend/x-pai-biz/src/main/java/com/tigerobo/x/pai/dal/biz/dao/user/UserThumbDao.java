package com.tigerobo.x.pai.dal.biz.dao.user;

import com.github.pagehelper.PageHelper;
import com.tigerobo.x.pai.api.vo.biz.PageReqVo;
import com.tigerobo.x.pai.api.vo.biz.blog.req.UserPageReq;
import com.tigerobo.x.pai.dal.biz.entity.BizCountPo;
import com.tigerobo.x.pai.dal.biz.entity.user.UserCommentPo;
import com.tigerobo.x.pai.dal.biz.entity.user.UserThumbPo;
import com.tigerobo.x.pai.dal.biz.mapper.user.UserThumbMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Component
public class UserThumbDao {

    @Autowired
    private UserThumbMapper userThumbUpMapper;

    public List<UserThumbPo> getThumb2userList(Integer userId, PageReqVo req){

        Example example = new Example(UserThumbPo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("notifyUserId",userId);
        criteria.andEqualTo("isDeleted",0);
        example.setOrderByClause("id desc");
        PageHelper.startPage(req.getPageNum(),req.getPageSize());

        return userThumbUpMapper.selectByExample(example);
    }

    public List<UserThumbPo> getuserThumbList(Integer userId, PageReqVo req){

        Example example = new Example(UserThumbPo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("isDeleted",0);
        example.setOrderByClause("id desc");
        PageHelper.startPage(req.getPageNum(),req.getPageSize());

        return userThumbUpMapper.selectByExample(example);
    }


    public List<UserThumbPo> getList(Integer userId, List<String> bizIds, Integer bizType){

        Example example = new Example(UserThumbPo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        criteria.andIn("bizId",bizIds);
        criteria.andEqualTo("bizType",bizType);

        criteria.andEqualTo("isDeleted",0);

        return userThumbUpMapper.selectByExample(example);
    }


    public UserThumbPo get(Integer userId, String bizId, Integer bizType){

        Example example = new Example(UserThumbPo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("bizId",bizId);
        criteria.andEqualTo("bizType",bizType);
        criteria.andEqualTo("isDeleted",0);

        example.setOrderByClause("id desc");

        List<UserThumbPo> userThumbUpPos = userThumbUpMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(userThumbUpPos)){
            return null;
        }
        return userThumbUpPos.get(0);
    }

    public void add(UserThumbPo po){
        userThumbUpMapper.insertSelective(po);
    }
    public void update(UserThumbPo po){
        userThumbUpMapper.updateByPrimaryKeySelective(po);
    }

    public void delete(Integer id) {
        UserThumbPo po = new UserThumbPo();
        po.setId(id);
        po.setIsDeleted(true);
        update(po);
    }

    public void deleteByBiz(Integer type,String bizId) {
        UserThumbPo po = new UserThumbPo();
        po.setIsDeleted(true);

        Example example = new Example(UserThumbPo.class);
        final Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("bizType",type);
        criteria.andEqualTo("bizId",bizId);
        criteria.andEqualTo("isDeleted",0);

        userThumbUpMapper.updateByExampleSelective(po,example);
    }

    public int count(String bizId, Integer bizType,Integer actionType) {
        Example example = new Example(UserThumbPo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("bizId",bizId);
        criteria.andEqualTo("bizType",bizType);
        criteria.andEqualTo("actionType",actionType);

        criteria.andEqualTo("isDeleted",0);
        return userThumbUpMapper.selectCountByExample(example);
    }

    public List<BizCountPo> getBizAllGroupCount(Integer bizType,Integer pageNum,Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        return userThumbUpMapper.groupBizCount(bizType);
    }



    public int countUnRead(Integer userId, Date preTime) {
        if (userId == null){
            return 0;
        }
        Example example = new Example(UserThumbPo.class);
        final Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("notifyUserId",userId);

        if (preTime!=null){
            criteria.andGreaterThan("createTime",preTime);
        }
        criteria.andEqualTo("isDeleted",0);
        return userThumbUpMapper.selectCountByExample(example);
    }
}
