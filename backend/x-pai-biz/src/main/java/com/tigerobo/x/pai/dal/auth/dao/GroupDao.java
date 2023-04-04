package com.tigerobo.x.pai.dal.auth.dao;

import com.github.pagehelper.PageHelper;
import com.tigerobo.x.pai.api.auth.entity.Group;
import com.tigerobo.x.pai.dal.auth.entity.GroupDo;
import com.tigerobo.x.pai.dal.auth.mapper.GroupMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Component
public class GroupDao {

    @Autowired
    private GroupMapper groupMapper;

    public void insert(GroupDo groupDo) {
        groupMapper.insertSelective(groupDo);
    }

    public List<GroupDo> getByUuids(List<String> uuids) {
        if (CollectionUtils.isEmpty(uuids)) {
            return null;
        }
        Example example = new Example(GroupDo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("uuid", uuids);
        criteria.andEqualTo("isDeleted", 0);

        return groupMapper.selectByExample(example);
    }

    public List<GroupDo> getByName(String name) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        Example example = new Example(GroupDo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("name", name);
        criteria.andEqualTo("isDeleted", 0);

        return groupMapper.selectByExample(example);

    }


    public List<GroupDo> likeNameList(String name) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }

        Example example = new Example(GroupDo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andLike("name", "%" + name + "%");
        criteria.andEqualTo("scope", Group.Scope.PUBLIC.getVal());
        criteria.andEqualTo("isDeleted", 0);

        PageHelper.startPage(1, 5);

        return groupMapper.selectByExample(example);

    }


    public GroupDo getByUuid(String uuid) {

        Example example = new Example(GroupDo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("uuid", uuid);
        criteria.andEqualTo("isDeleted", 0);
        return groupMapper.selectOneByExample(example);
    }

    public void add(GroupDo groupDo) {
        groupMapper.insertSelective(groupDo);
    }


    public GroupDo getByAccount(String account) {

        Example example = new Example(GroupDo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("account", account);
        criteria.andEqualTo("isDeleted", 0);
        List<GroupDo> groupDos = groupMapper.selectByExample(example);
        return CollectionUtils.isEmpty(groupDos) ? null : groupDos.get(0);
    }


    public List<GroupDo> getPublicList() {
        Example example = new Example(GroupDo.class);

        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("scope", Group.Scope.PUBLIC.getVal());
        criteria.andEqualTo("isDeleted", 0);
        PageHelper.startPage(1, 100);
        return groupMapper.selectByExample(example);
    }
}
