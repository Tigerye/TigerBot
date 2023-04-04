package com.tigerobo.x.pai.engine.dao;

import com.github.pagehelper.PageHelper;
import com.tigerobo.x.pai.biz.utils.HtmlContentUtil;
import com.tigerobo.x.pai.dal.biz.entity.blog.BlogBigshotReplyPo;
import com.tigerobo.x.pai.dal.biz.mapper.blog.BlogBigshotReplyMapper;
import com.tigerobo.x.pai.engine.EngineBaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

public class BlogReplyDaoTest extends EngineBaseTest {

    @Autowired
    private BlogBigshotReplyMapper blogBigshotReplyMapper;


    @Test
    public void initSummaryTest(){

        int maxId = getMaxId();


        for (int i = 0; i < maxId; ) {


            System.out.println(i+"/"+maxId);
            int j = i+100;
            Example example = new Example(BlogBigshotReplyPo.class);

            Example.Criteria criteria = example.createCriteria();
            criteria.andGreaterThan("id",i);
            criteria.andLessThanOrEqualTo("id",j);

            List<BlogBigshotReplyPo> replyPos = blogBigshotReplyMapper.selectByExample(example);

            if (!CollectionUtils.isEmpty(replyPos)){
                replyPos.parallelStream().forEach(po->{
                    try {
                        String summary = getSummary(po.getContent());

                        String tranSummary = getSummary(po.getTranslateContent());
                        BlogBigshotReplyPo update = new BlogBigshotReplyPo();
                        update.setId(po.getId());
                        update.setSummary(summary);
                        update.setTranslateSummary(tranSummary);

                        update(update);

                    }catch (Exception ex){
                        System.out.println("id="+po.getId());
                        ex.printStackTrace();
                    }
                });
            }

            i=j;
        }





//        blogBigshotReplyMapper.selectByExample()

        System.out.println();
    }


    private String getSummary(String content){
        List<String> list = HtmlContentUtil.parseHtmlCleanContent(content);
        if (CollectionUtils.isEmpty(list)){
            return "";
        }
        return String.join(" ",list);
    }
    private void update(BlogBigshotReplyPo update){
        blogBigshotReplyMapper.updateByPrimaryKeySelective(update);
    }

    private int getMaxId(){

        Example example = new Example(BlogBigshotReplyPo.class);
        example.setOrderByClause("id desc");
        PageHelper.startPage(1,1);
        List<BlogBigshotReplyPo> replyPos = blogBigshotReplyMapper.selectByExample(example);

        return replyPos.get(0).getId();

    }
}
