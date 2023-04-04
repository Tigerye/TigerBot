package com.tigerobo.pai.biz.test.service.test.blog;

import com.alibaba.fastjson.JSON;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.dal.biz.dao.blog.BlogCategoryDao;
import com.tigerobo.x.pai.dal.biz.entity.blog.BlogCategoryPo;
import com.tigerobo.x.pai.dal.biz.mapper.blog.BlogCategoryMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

public class BlogCategoryServiceTest extends BaseTest {

    @Autowired
    private BlogCategoryDao blogCategoryDao;

    @Autowired
    private BlogCategoryMapper blogCategoryMapper;

    @Test
    public void initTest(){

        List<BlogCategoryPo> all = blogCategoryMapper.selectAll();

        Map<String,BlogCategoryPo> map = new HashMap<>();
        for (BlogCategoryPo po : all) {
            map.put(po.getName(),po);
        }

        List<String> addList = new ArrayList<>();




        List<String> strList = getStrList();
        for (String s : strList) {
            if(!map.containsKey(s)){
                addList.add(s);
            }
        }

        List<BlogCategoryPo> delList = map.entrySet().stream().filter(k -> !strList.contains(k.getKey())).map(e -> e.getValue()).collect(Collectors.toList());

        System.out.println("add-list:");
        for (String s : addList) {
            System.out.println(s);
        }

        System.out.println("del:-----------------");
        for (BlogCategoryPo po : delList) {
            System.out.println(po.getId()+"\t"+po.getName());
        }

        List<Integer> collect = delList.stream().map(d -> d.getId()).collect(Collectors.toList());

        System.out.println(JSON.toJSONString(collect));

    }

    private List<String> getStrList(){
        return Arrays.asList("Natural Language Processing",
                "Blockchain",
                "Reinforcement Learning",
                "AI Cloud Services",
                "Financial Services",
                "NFT",
                "Robotics",
                "AI in Government",
                "Workforce",
                "Machine Learning",
                "Self Driving Cars",
                "AI Software",
                "Computer Vision",
                "Mobile",
                "Quantum Computing",
                "Transportation",
                "Bitcoin",
                "Metaverse",
                "Retail",
                "Startups",
                "eCommerce",
                "IoT",
                "Speech Recognition",
                "Cyber Security",
                "Deep Learning",
                "Energy",
                "Health Care");
    }
}
