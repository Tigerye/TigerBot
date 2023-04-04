package com.tigerobo.x.pai.dal.admin.dao;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.tigerobo.x.pai.api.admin.req.BlogAdminQueryReq;
import com.tigerobo.x.pai.dal.admin.mapper.BlogAdminQueryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BlogAdminDao {

    @Autowired
    private BlogAdminQueryMapper blogAdminQueryMapper;

    public Page<Integer> searchBlogPage(BlogAdminQueryReq req) {

        PageHelper.startPage(req.getPageNum(), req.getPageSize());

        return (Page<Integer>)blogAdminQueryMapper.query(req);
    }

}
