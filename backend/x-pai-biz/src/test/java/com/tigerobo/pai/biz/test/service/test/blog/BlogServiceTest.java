package com.tigerobo.pai.biz.test.service.test.blog;

import com.alibaba.fastjson.JSON;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.auth.entity.User;
import com.tigerobo.x.pai.api.enums.BlogTabEnum;
import com.tigerobo.x.pai.api.vo.IdReqVo;
import com.tigerobo.x.pai.api.vo.biz.blog.BlogCategoryQueryVo;
import com.tigerobo.x.pai.api.vo.biz.blog.BlogQueryVo;
import com.tigerobo.x.pai.api.vo.biz.blog.BlogVo;
import com.tigerobo.x.pai.api.vo.biz.blog.req.BlogAddReq;
import com.tigerobo.x.pai.api.vo.biz.blog.req.BlogDetailReq;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.biz.blog.req.UserPageReq;
import com.tigerobo.x.pai.api.vo.user.follow.FollowVo;
import com.tigerobo.x.pai.biz.biz.blog.BlogOperateService;
import com.tigerobo.x.pai.biz.biz.blog.BlogSearchService;
import com.tigerobo.x.pai.biz.biz.blog.BlogService;
import com.tigerobo.x.pai.biz.cache.RedisCacheService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

public class BlogServiceTest extends BaseTest {

    @Autowired
    private BlogService blogService;

    @Autowired
    private BlogSearchService blogSearchService;

    @Autowired
    private BlogOperateService blogOperateService;


    @Autowired
    private RedisCacheService redisCacheService;
    @Test
    public void tranTest(){


        int id = 1;
        blogOperateService.offline(id);

    }

    @Test
    public void searchTest(){

        BlogDetailReq req = new BlogDetailReq();
        req.setId(148817);
        BlogVo pageDetail = blogService.getPageDetail(req);

        System.out.println(JSON.toJSONString(pageDetail));
    }
    @Test
    public void getRecommendTest(){

//        ThreadLocalHolder.setUserId(18);
        UserPageReq req = new UserPageReq();
        req.setKeyword("蔡");

        PageVo<FollowVo> bloggerPageList = blogService.getBloggerPageList(req);

        System.out.println(JSON.toJSONString(bloggerPageList));

    }

    @Test
    public void countTest(){

        ThreadLocalHolder.setUserId(18);
        int i = blogSearchService.countNew();

        System.out.println(i);


    }


    @Test
    public void addBlogTest(){

        BlogAddReq req = new BlogAddReq();
        ThreadLocalHolder.setUserId(3);
//        req.setId(2486);
        req.setTitle("wsenTitle");
        req.setContent("<p>test3</p>");
        req.setOnlineStatus(1);
        System.out.println(JSON.toJSONString(req));
        BlogVo blogVo = blogOperateService.addOrUpdateBlog(req);

        System.out.println(JSON.toJSONString(blogVo));
    }

    @Test
    public void viewTest(){
        int id = 58;
        blogService.incrView(id,null);

        int view = blogService.getView(id);
        System.out.println(view);
    }
    @Test
    public void testBloggers(){

        List<User> topUsers = blogService.getTopUsers();
        System.out.println(JSON.toJSONString(topUsers));
    }

    @Test
    public void blogTest(){
        int blogCount = blogService.getBlogCount();
        long start = System.currentTimeMillis();
        int blogCount2 = blogService.getBlogCount();
        System.out.println("delta:"+(System.currentTimeMillis()-start));
        int blogCount3 = blogService.getBlogCount();
        System.out.println("delta:"+(System.currentTimeMillis()-start));
        int blogCount4 = blogService.getBlogCount();
        System.out.println("delta:"+(System.currentTimeMillis()-start));

        System.out.println(blogCount);
        System.out.println(blogCount2);
        System.out.println(blogCount3);
        System.out.println(blogCount4);
    }

    @Test
    public void getMetricTest(){

        int blogCount = blogService.getBlogCount();
        int bloggerCount = blogService.getBloggerCount();
        List<User> topUsers = blogService.getTopUsers();

        System.out.println(blogCount);
        System.out.println(bloggerCount);
        System.out.println(JSON.toJSONString(topUsers));
    }
    @Test
    public void detailTest(){
        BlogDetailReq req = new BlogDetailReq();
        req.setId(41777);
        ThreadLocalHolder.setUserId(3);
//        req.setViewDetail(true);
//        ThreadLocalHolder.setUserId(3);
        BlogVo detail = blogService.getPageDetail(req);
        System.out.println(JSON.toJSONString(detail));
    }

    @Test
    public void preOrNextTest(){
        IdReqVo req = new IdReqVo();
        req.setId(40402);
//        ThreadLocalHolder.setUserId(3);
//        req.setViewDetail(true);
//        ThreadLocalHolder.setUserId(3);
        int id =41390;
        Object detail = blogService.getSiteRelBlog(id);
        System.out.println(JSON.toJSONString(detail));
    }
    @Test
    public void queryTest()throws Exception{



        BlogQueryVo queryVo = new BlogQueryVo();
        queryVo.setPageSize(20);


        queryVo.setTabType(BlogTabEnum.NEW.toString());
        queryVo.setViewTab(true);

//        queryVo.setBigShotIdList(Arrays.asList(31));


        ThreadLocalHolder.setUserId(3);
        System.out.println("count-"+blogSearchService.countNew());
        long start = System.currentTimeMillis();
        PageVo<BlogVo> pageList = blogSearchService.getPageList(queryVo);
        System.out.println("delta:"+(System.currentTimeMillis()-start));
        System.out.println(JSON.toJSONString(pageList));

        int i = blogSearchService.countNew();
        System.out.println("count-"+i);


    }

    @Test
    public void queryMineTest()throws Exception{

        BlogQueryVo queryVo = new BlogQueryVo();

//        queryVo.setTabType(BlogTabEnum.BIGSHOTS.toString());
//        ThreadLocalHolder.setUserId(3);
//        queryVo.setOnlineStatus(1);
//        queryVo.setUserId(3);
        queryVo.setBigShotIdList(Arrays.asList(47));
        queryVo.setOnlineStatus(0);
//        queryVo.setViewTab(true);
//        queryVo.setKeyword("Artificial");
//        queryVo.setSiteIdList(Arrays.asList(1));
//
//
//        Date date = DateUtils.parseDate("2021-03-01","yyyy-MM-dd");
//        queryVo.setStartPublishDate(date);
//        queryVo.setEndPublishDate( DateUtils.parseDate("2021-05-01","yyyy-MM-dd"));
        PageVo<BlogVo> pageList = blogSearchService.getUserBlogs(queryVo);

        System.out.println(JSON.toJSONString(pageList));
    }


    @Test
    public void queryCategoryTest()throws Exception{

        BlogCategoryQueryVo queryVo = new BlogCategoryQueryVo();

//        queryVo.setTabType(BlogTabEnum.BIGSHOTS.toString());
//        ThreadLocalHolder.setUserId(3);
//        queryVo.setOnlineStatus(1);
//        queryVo.setUserId(3);

        queryVo.setTabType("hot");
        queryVo.setCategoryId(1);
        queryVo.setKeyword("Hopcb");
//        queryVo.setViewTab(true);
//        queryVo.setKeyword("Artificial");
//        queryVo.setSiteIdList(Arrays.asList(1));
//
//
//        Date date = DateUtils.parseDate("2021-03-01","yyyy-MM-dd");
//        queryVo.setStartPublishDate(date);
//        queryVo.setEndPublishDate( DateUtils.parseDate("2021-05-01","yyyy-MM-dd"));
        PageVo<BlogVo> pageList = blogSearchService.getCategoryBlogs(queryVo);

        System.out.println(JSON.toJSONString(pageList));
    }

    @Test
    public void incrScoreTest(){

        String sortListKey =  "pai:blog:sort:list";
        Long count = 100L;
        int blogId = 4992;

        redisCacheService.zadd(sortListKey, count.doubleValue(), String.valueOf(blogId));
    }
}
