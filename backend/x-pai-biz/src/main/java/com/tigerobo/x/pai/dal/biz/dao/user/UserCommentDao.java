package com.tigerobo.x.pai.dal.biz.dao.user;

import com.github.pagehelper.PageHelper;
import com.tigerobo.x.pai.api.vo.biz.blog.req.UserPageReq;
import com.tigerobo.x.pai.dal.biz.entity.user.UserCommentPo;
import com.tigerobo.x.pai.dal.biz.mapper.user.UserCommentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class UserCommentDao {

    @Autowired
    private UserCommentMapper userCommentMapper;

    public int count(String bizId,Integer bizType){

        Example example = new Example(UserCommentPo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("bizId",bizId);

        criteria.andEqualTo("bizType",bizType);
        criteria.andEqualTo("isDeleted",0);
        criteria.andEqualTo("onlineStatus",1);
        return userCommentMapper.selectCountByExample(example);
    }


    public int countUserCommentNum(Integer userId,String bizId,Integer bizType){

        Example example = new Example(UserCommentPo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("bizId",bizId);
        criteria.andEqualTo("bizType",bizType);
        criteria.andEqualTo("isDeleted",0);
        return userCommentMapper.selectCountByExample(example);
    }

    public UserCommentPo load(Integer id){

        if (id == null){
            return null;
        }
        UserCommentPo userCommentPo = userCommentMapper.selectByPrimaryKey(id);

        if (userCommentPo==null){
            return null;
        }
        Boolean isDeleted = userCommentPo.getIsDeleted();
        if (isDeleted==null||isDeleted){
            return null;
        }
        return userCommentPo;
    }

    public void add(UserCommentPo po){
        userCommentMapper.insertSelective(po);
    }


    public void updateSubComment(String bizId,Integer bizType,Integer id){

        Example example = new Example(UserCommentPo.class);

        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("bizId",bizId);

        criteria.andEqualTo("bizType",bizType);
        criteria.andEqualTo("replyRootId",id);

        UserCommentPo update = new UserCommentPo();
        update.setReplyRootId(0);

        userCommentMapper.updateByExampleSelective(update,example);
    }
    public void delete(Integer id){

        UserCommentPo po = new UserCommentPo();
        po.setId(id);
        po.setIsDeleted(true);
        update(po);
    }

    public void update(UserCommentPo po){
        userCommentMapper.updateByPrimaryKeySelective(po);
    }

    public List<UserCommentPo> getBizCommentListById(String bizId, Integer bizType,Integer pageNo,Integer pageSize){

        Example example = new Example(UserCommentPo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("bizId",bizId);
        criteria.andEqualTo("bizType",bizType);

        criteria.andEqualTo("replyRootId",0);
        criteria.andEqualTo("isDeleted",0);
        criteria.andEqualTo("onlineStatus",1);

        PageHelper.startPage(pageNo,pageSize);
        example.setOrderByClause("id desc");
        return userCommentMapper.selectByExample(example);
    }

    public List<UserCommentPo> getByIds(List<Integer> ids){

        Example example = new Example(UserCommentPo.class);

        final Example.Criteria criteria = example.createCriteria();

        criteria.andIn("id",ids);
        criteria.andEqualTo("isDeleted",0);
        return userCommentMapper.selectByExample(example);
    }

    public List<UserCommentPo> getNotifyUserCommentList(Integer userId,UserPageReq req){

        Example example = new Example(UserCommentPo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("notifyUserId",userId);

        criteria.andEqualTo("isDeleted",0);
//        criteria.andEqualTo("onlineStatus",1);

        PageHelper.startPage(req.getPageNum(),req.getPageSize());
        example.setOrderByClause("id desc");
        return userCommentMapper.selectByExample(example);
    }

    public List<UserCommentPo> getUserCommentList(Integer userId,UserPageReq req){

        if (userId == null||userId == 0){
            return null;
        }
        Example example = new Example(UserCommentPo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);

        criteria.andEqualTo("isDeleted",0);
//        criteria.andEqualTo("onlineStatus",1);
        example.setOrderByClause("id desc");

        PageHelper.startPage(req.getPageNum(),req.getPageSize());

        return userCommentMapper.selectByExample(example);
    }

    public List<UserCommentPo> getSubCommentList(String bizId, Integer bizType,List<Integer> rootIds){

        if (CollectionUtils.isEmpty(rootIds)){
            return new ArrayList<>();
        }
        Example example = new Example(UserCommentPo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("bizId",bizId);
        criteria.andEqualTo("bizType",bizType);
        criteria.andIn("replyRootId",rootIds);
        criteria.andEqualTo("isDeleted",0);
        criteria.andEqualTo("onlineStatus",1);
        example.setOrderByClause("id asc");
        return userCommentMapper.selectByExample(example);
    }


    public int countUnRead(Integer userId, Date preTime) {
        if (userId == null){
            return 0;
        }
        Example example = new Example(UserCommentPo.class);
        final Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("notifyUserId",userId);

        if (preTime!=null){
            criteria.andGreaterThan("createTime",preTime);
        }
        return userCommentMapper.selectCountByExample(example);

    }
}
