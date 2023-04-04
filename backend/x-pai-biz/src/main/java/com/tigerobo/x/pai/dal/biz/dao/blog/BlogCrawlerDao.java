package com.tigerobo.x.pai.dal.biz.dao.blog;

import com.github.pagehelper.PageHelper;
import com.tigerobo.x.pai.dal.biz.entity.blog.BlogCrawlerPo;
import com.tigerobo.x.pai.dal.biz.mapper.blog.BlogCrawlerMapper;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Component
public class BlogCrawlerDao {

    @Autowired
    private BlogCrawlerMapper blogCrawlerMapper;

    public void add(BlogCrawlerPo po){
        BlogCrawlerPo db = getByThirdId(po.getThirdId());
        if (db!=null){
            return;
        }
        blogCrawlerMapper.insertSelective(po);
    }


    public void update(BlogCrawlerPo po){
        blogCrawlerMapper.updateByPrimaryKeySelective(po);
    }
    public BlogCrawlerPo getByThirdId(Integer thirdId){

        Example example = new Example(BlogCrawlerPo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("thirdId",thirdId);

        List<BlogCrawlerPo> blogCrawlerPos = blogCrawlerMapper.selectByExample(example);


        if (CollectionUtils.isEmpty(blogCrawlerPos)){
            return null;
        }
        return blogCrawlerPos.get(0);
    }
    public BlogCrawlerPo getById(Integer id){


        return blogCrawlerMapper.selectByPrimaryKey(id);
    }

    public List<BlogCrawlerPo> getByThirdIds(List<Integer> thirdIds){
        Example example = new Example(BlogCrawlerPo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("thirdId",thirdIds);
        return blogCrawlerMapper.selectByExample(example);

    }

    public List<BlogCrawlerPo> getWaitDealList(){
        Example example = new Example(BlogCrawlerPo.class);

        Example.Criteria criteria = example.createCriteria();
//        criteria.andEqualTo("id",1353);
        criteria.andEqualTo("processStatus",1);
//        criteria.andEqualTo("thirdId",206);
        Date startTime = DateUtils.addDays(new Date(), -5);
        criteria.andGreaterThan("createTime",startTime);

        PageHelper.startPage(1,300);

        return blogCrawlerMapper.selectByExample(example);
    }

    public List<BlogCrawlerPo> getTestWaitDealList(){
        Example example = new Example(BlogCrawlerPo.class);

        Example.Criteria criteria = example.createCriteria();
//        criteria.andEqualTo("id",1353);
        criteria.andEqualTo("processStatus",1);
//        criteria.andEqualTo("thirdId",2319598);
//        Date startTime = DateUtils.addDays(new Date(), -5);
//        criteria.andGreaterThan("createTime",startTime);

        PageHelper.startPage(1,300);

        return blogCrawlerMapper.selectByExample(example);
    }

    public List<BlogCrawlerPo> getWaitDealCategoryList(){
        Example example = new Example(BlogCrawlerPo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("processStatus",5);
        criteria.andEqualTo("categoryStatus",0);

        //        Date startTime = DateUtils.addDays(new Date(), -5);
//        criteria.andGreaterThan("createTime",startTime);

        example.setOrderByClause("id desc");
        PageHelper.startPage(1,100);

        return blogCrawlerMapper.selectByExample(example);
    }


}
