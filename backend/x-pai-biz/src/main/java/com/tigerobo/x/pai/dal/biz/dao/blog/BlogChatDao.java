package com.tigerobo.x.pai.dal.biz.dao.blog;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.tigerobo.x.pai.api.vo.biz.blog.req.ChatPageReq;
import com.tigerobo.x.pai.dal.biz.entity.blog.BlogChatPo;
import com.tigerobo.x.pai.dal.biz.mapper.blog.BlogChatMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Component
public class BlogChatDao {

    @Autowired
    private BlogChatMapper blogChatMapper;


    public Page<BlogChatPo> getPage(ChatPageReq req){

        PageHelper.startPage(req.getPageNum(),req.getPageSize());

        Example example = new Example(BlogChatPo.class);

        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("chatId",req.getChatId());
        criteria.andEqualTo("isDeleted",0);
        example.setOrderByClause("publish_time");
        Page<BlogChatPo> chatPos = (Page<BlogChatPo>)blogChatMapper.selectByExample(example);
        return chatPos;
    }
    public List<BlogChatPo> getDetailList(String chatId){
        return getDetailList(chatId,100);
    }
    public List<BlogChatPo> getDetailList(String chatId,int size){

        PageHelper.startPage(1,size);

        Example example = new Example(BlogChatPo.class);

        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("chatId",chatId);
        criteria.andEqualTo("isDeleted",0);

        example.setOrderByClause("publish_time");
        return blogChatMapper.selectByExample(example);
    }

    public void add(BlogChatPo po){

        blogChatMapper.insertSelective(po);
    }

    public List<BlogChatPo> getBySpecIds(List<String> specIds){
        if (CollectionUtils.isEmpty(specIds)){
            return null;
        }
        Example example = new Example(BlogChatPo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("specId",specIds);
        criteria.andEqualTo("isDeleted",0);

        example.setOrderByClause("publish_time");

        return blogChatMapper.selectByExample(example);
    }

    public int update(BlogChatPo po){
        return blogChatMapper.updateByPrimaryKeySelective(po);
    }
}
