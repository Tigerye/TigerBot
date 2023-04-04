package com.tigerobo.x.pai.dal.auth.dao;

import com.github.pagehelper.PageHelper;
import com.tigerobo.x.pai.dal.auth.entity.UserDo;
import com.tigerobo.x.pai.dal.auth.mapper.UserMapper;
import com.tigerobo.x.pai.dal.biz.entity.blog.BlogInfoPo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Component
public class UserDao {

    @Autowired
    private UserMapper userMapper;

    public int count(Date start, Date end){
        Example example = new Example(UserDo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andGreaterThanOrEqualTo("createTime",start);
        criteria.andLessThanOrEqualTo("createTime",end);

        return userMapper.selectCountByExample(example);
    }

    public int count(){
        Example example = new Example(UserDo.class);
        return userMapper.selectCountByExample(example);
    }

    public UserDo getByWechatUnionId(String unionId){
        if (StringUtils.isBlank(unionId)){
            return null;
        }
        Example example = new Example(UserDo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("wechat",unionId);
        criteria.andEqualTo("isDeleted",0);
        List<UserDo> userDos = userMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(userDos)){
            return null;
        }
        return userDos.get(0);
    }

    public UserDo getByMobile(String mobile){
        if (StringUtils.isBlank(mobile)){
            return null;
        }
        Example example = new Example(UserDo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("mobile",mobile);
        criteria.andEqualTo("isDeleted",0);
        List<UserDo> userDos = userMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(userDos)){
            return null;
        }
        return userDos.get(0);

    }


    public UserDo getByAccount(String account){

        if (StringUtils.isBlank(account)){
            return null;
        }
        Example example = new Example(UserDo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("account",account);
        criteria.andEqualTo("isDeleted",0);
        List<UserDo> userDos = userMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(userDos)){
            return null;
        }
        return userDos.get(0);
    }

    public UserDo getById(Integer id){
        if (id == null){
            return null;
        }
        return userMapper.selectByPrimaryKey(id);
    }

    public UserDo load(Integer id){
        if (id == null){
            return null;
        }
        UserDo userDo = userMapper.selectByPrimaryKey(id);

        if (userDo == null||userDo.getIsDeleted()==null||userDo.getIsDeleted()){
            return null;
        }
        return userDo;
    }


    public List<UserDo> equalNameList(String name){
        if (org.springframework.util.StringUtils.isEmpty(name)){
            return null;
        }

        Example example = new Example(UserDo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("name",name);
//        criteria.andEqualTo("scope", Group.Scope.PUBLIC.getVal());
        criteria.andEqualTo("isDeleted",0);

        PageHelper.startPage(1,5);

        return userMapper.selectByExample(example);

    }


    public List<UserDo> getByIds(List<Integer> ids){
        if (ids == null||ids.isEmpty()){
            return null;
        }
        Example example = new Example(UserDo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id",ids);
        criteria.andEqualTo("isDeleted",0);

        return userMapper.selectByExample(example);
    }

    public UserDo get(String uuid){
        Example example = new Example(UserDo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("uuid",uuid);
        List<UserDo> userDos = userMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(userDos)){
            return null;
        }
        return userDos.get(0);
    }

    public UserDo getByUuid(String uuid){

        Example example = new Example(UserDo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("uuid",uuid);

        List<UserDo> userDos = userMapper.selectByExample(example);
        if (!CollectionUtils.isEmpty(userDos)){
            return userDos.get(0);
        }
        return null;
    }


    public int insert(UserDo userDo) {
        if (userDo == null){
            return 0 ;
        }
        return userMapper.insertSelective(userDo);
    }

    public List<UserDo> getGroupUsers(String gid) {

        Example example = new Example(UserDo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("currGroupUuid",gid);
        criteria.andEqualTo("isDeleted",0);

        PageHelper.startPage(1,10);
        return userMapper.selectByExample(example);
    }

    public int update(UserDo userDo){
        if (userDo==null){
            return 0;
        }
        return userMapper.updateByPrimaryKeySelective(userDo);
    }

    public int delete(Integer id){
        if (id == null){
            return 0;
        }
        UserDo userDo = new UserDo();
        userDo.setId(id);
        userDo.setIsDeleted(true);
        return update(userDo);
    }
}
