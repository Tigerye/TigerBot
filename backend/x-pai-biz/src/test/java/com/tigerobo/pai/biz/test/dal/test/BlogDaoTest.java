package com.tigerobo.pai.biz.test.dal.test;

import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.dal.biz.dao.blog.BlogSearchDao;
import com.tigerobo.x.pai.dal.biz.entity.blog.BlogInfoPo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BlogDaoTest extends BaseTest {
//    @Autowired
//    private BlogInfoDao blogInfoDao;
    @Autowired
    private BlogSearchDao blogSearchDao;

    @Autowired
    private DataSource dataSource;

    @Test
    public void selectTest()throws SQLException {

        try(final Connection connection = dataSource.getConnection()){

            String sql = "select count(1) from aml_info";
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);

            final ResultSet resultSet = preparedStatement.executeQuery();

            final boolean next = resultSet.next();
            final int anInt = resultSet.getInt(1);
            System.out.println(anInt);
        }


    }

    @Test
    public void dealTest(){

        BlogInfoPo po = new BlogInfoPo();
        po.setId(1);
        po.setProcessStatus(5);

        blogSearchDao.update(po);
    }

    @Test
    public void addTEST(){


        BlogInfoPo po = new BlogInfoPo();
        po.setProcessStatus(5);

        blogSearchDao.add(po);
    }

}
