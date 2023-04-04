package com.tigerobo.x.pai.dal.biz.dao.blog;

import com.github.pagehelper.PageHelper;
import com.tigerobo.x.pai.dal.biz.entity.blog.BlogTwitterCrawlerPo;
import com.tigerobo.x.pai.dal.biz.mapper.blog.BlogTwitterCrawlerMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Component
public class BlogTwitterCrawlerDao {

    @Autowired
    private BlogTwitterCrawlerMapper blogTwitterCrawlerMapper;


    public List<BlogTwitterCrawlerPo> getWaitList(){
        Example example = new Example(BlogTwitterCrawlerPo.class);

        Example.Criteria criteria = example.createCriteria();
//        criteria.andEqualTo("id",1353);
        criteria.andEqualTo("processStatus",0);
//        criteria.andEqualTo("thirdId",999031);
//        criteria.andEqualTo("isDeleted",0);

        PageHelper.startPage(1,300);

        return blogTwitterCrawlerMapper.selectByExample(example);
    }

    public List<BlogTwitterCrawlerPo> getTestWaitList(){
        Example example = new Example(BlogTwitterCrawlerPo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",82757);
//        criteria.andEqualTo("processStatus",0);
//        criteria.andGreaterThanOrEqualTo("createTime", DateUtils.addDays(new Date(),-8));
//        criteria.andNotEqualTo("replyChain","");
//        criteria.andEqualTo("thirdId",999031);
//        criteria.andEqualTo("isDeleted",0);

        PageHelper.startPage(1,300);

        return blogTwitterCrawlerMapper.selectByExample(example);
    }
    public void add(BlogTwitterCrawlerPo po){
//        BlogTwitterCrawlerPo db = getByThirdId(po.getThirdId());
//        if (db!=null){
//            return;
//        }
        if (StringUtils.isBlank(po.getContent())){
            po.setProcessStatus(2);
        }
        blogTwitterCrawlerMapper.insertSelective(po);
    }

    public int update(BlogTwitterCrawlerPo po){

        return blogTwitterCrawlerMapper.updateByPrimaryKeySelective(po);
    }

    public BlogTwitterCrawlerPo getByThirdId(Integer thirdId){

        Example example = new Example(BlogTwitterCrawlerPo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("thirdId",thirdId);

        List<BlogTwitterCrawlerPo> blogCrawlerPos = blogTwitterCrawlerMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(blogCrawlerPos)){
            return null;
        }
        return blogCrawlerPos.get(0);
    }
    public BlogTwitterCrawlerPo getBySpecId(String specId){

        Example example = new Example(BlogTwitterCrawlerPo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("specId",specId);

        List<BlogTwitterCrawlerPo> blogCrawlerPos = blogTwitterCrawlerMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(blogCrawlerPos)){
            return null;
        }
        return blogCrawlerPos.get(0);
    }
    public List<BlogTwitterCrawlerPo> getByThirdIds(List<Integer> thirdIds){
        Example example = new Example(BlogTwitterCrawlerPo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("thirdId",thirdIds);
        return blogTwitterCrawlerMapper.selectByExample(example);
    }


}
