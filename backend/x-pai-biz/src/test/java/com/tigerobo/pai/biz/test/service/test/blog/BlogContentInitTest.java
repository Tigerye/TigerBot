package com.tigerobo.pai.biz.test.service.test.blog;

import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.constants.OssConstant;
import com.tigerobo.x.pai.biz.utils.RestUtil;
import com.tigerobo.x.pai.dal.biz.entity.blog.BlogInfoPo;
import com.tigerobo.x.pai.dal.biz.mapper.blog.BlogInfoMapper;
import org.junit.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

public class BlogContentInitTest extends BaseTest {

    @Autowired
    private BlogInfoMapper blogInfoMapper;


    @Test
    public void initTest(){

        Example example = new Example(BlogInfoPo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("sourceFrom",0);

        criteria.andIsNull("content");

        List<BlogInfoPo> list = blogInfoMapper.selectByExample(example);

        if (list==null||list.size()==0){
            return;
        }
        for (BlogInfoPo blogInfoPo : list) {
            System.out.println(blogInfoPo.getTitle());

            String url = OssConstant.domainUrl + blogInfoPo.getOssUrl();
            String s = RestUtil.get(url, null);
            if (StringUtils.isNotBlank(s)){
                updateContent(blogInfoPo.getId(),s);
            }
        }
    }

    private void updateContent(int id,String content){

        BlogInfoPo po = new BlogInfoPo();
        po.setId(id);
        po.setContent(content);
        blogInfoMapper.updateByPrimaryKeySelective(po);

    }
}
