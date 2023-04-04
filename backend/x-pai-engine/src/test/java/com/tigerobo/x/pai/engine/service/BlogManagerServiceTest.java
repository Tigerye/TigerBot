package com.tigerobo.x.pai.engine.service;

import com.tigerobo.x.pai.biz.biz.blog.BlogCrawlerService;
import com.tigerobo.x.pai.biz.lake.LakeTranslateService;
import com.tigerobo.x.pai.dal.biz.dao.blog.BlogCrawlerDao;
import com.tigerobo.x.pai.dal.biz.dao.blog.BlogSearchDao;
import com.tigerobo.x.pai.dal.biz.entity.blog.BlogCrawlerPo;
import com.tigerobo.x.pai.dal.biz.entity.blog.BlogInfoPo;
import com.tigerobo.x.pai.dal.biz.mapper.blog.BlogInfoMapper;
import com.tigerobo.x.pai.engine.EngineBaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

public class BlogManagerServiceTest extends EngineBaseTest {

    @Autowired
    private BlogCrawlerService blogCrawlerService;
    @Autowired
    private BlogCrawlerDao blogCrawlerDao;

//    @Autowired
//    private BlogInfoDao blogInfoDao;

    @Autowired
    private BlogSearchDao blogSearchDao;
    @Autowired
    private BlogInfoMapper blogInfoMapper;
    @Autowired
    private LakeTranslateService lakeTranslateService;
    @Test
    public void test(){
        BlogCrawlerService.BlogCrawlerConfig config = new BlogCrawlerService.BlogCrawlerConfig();
//        config.setIgnoreTranslate(true);

        int i = 27791;
        config.setIgnoreSummary(false);
        config.setCanUseDbTranslate(false);
        BlogCrawlerPo crawlerPo = blogCrawlerDao.getById(i);
        blogCrawlerService.save2blog(crawlerPo,config);
    }


    @Test
    public void updateTitleTransTest(){
        int start = 5000;
        int end = start+200;
        int max = 6000;
        while (end<max){
            List<BlogInfoPo> list = get(start, end);
            long startTime = System.currentTimeMillis();
            deal(list);
            System.out.println(start+",delta:"+(System.currentTimeMillis()-startTime));
            start = end;
            end = end+200;
        }
    }

    private void deal(List<BlogInfoPo> list){
        if (CollectionUtils.isEmpty(list)){
            return;
        }
        list.parallelStream().forEach(blogInfoPo->{

            String title = blogInfoPo.getTitle();
            String trans = lakeTranslateService.en2cn(title);
            if (!StringUtils.isEmpty(trans)){
                BlogInfoPo update = new BlogInfoPo();
                update.setId(blogInfoPo.getId());
                update.setTranslateTitle(trans);
                blogSearchDao.update(update);
            }

        });


    }

    private List<BlogInfoPo> get(int startId, Integer endId){

        Example example = new Example(BlogInfoPo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andGreaterThan("id",startId);
        criteria.andLessThanOrEqualTo("id",endId);

//        criteria.andEqualTo("id",4619);

        criteria.andEqualTo("sourceFrom",1);
        return blogInfoMapper.selectByExample(example);
    }
}
