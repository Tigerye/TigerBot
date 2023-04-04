package com.tigerobo.pai.admin;

import com.tigerobo.x.pai.api.admin.BlogAdminVo;
import com.tigerobo.x.pai.api.admin.req.*;
import com.tigerobo.x.pai.api.dto.admin.BigShotDto;
import com.tigerobo.x.pai.api.dto.admin.PubSiteDto;
import com.tigerobo.x.pai.api.dto.blog.BlogCategoryDto;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.biz.pub.PubBigShotVo;
import com.tigerobo.x.pai.api.vo.biz.pub.PubSiteVo;
import com.tigerobo.x.pai.biz.admin.BigShotAdminService;
import com.tigerobo.x.pai.biz.admin.BlogAdminService;
import com.tigerobo.x.pai.biz.admin.PubSiteAdminService;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class BlogOpeTest extends AdminBaseTest{
    @Autowired
    private BlogAdminService blogAdminService;
    @Autowired
    private BigShotAdminService bigShotAdminService;
    @Autowired
    private PubSiteAdminService pubSiteAdminService;

    @Test
    public void blogOnlineTest(){
        AdminOnlineStatusReq req=new AdminOnlineStatusReq();
        req.setBlogId(24175);
        req.setOnlineStatus(0);
        blogAdminService.opeOnlineStatus(req);
    }

    @Test
    public void blogDelTest(){
        blogAdminService.blogDelete(1);
    }


    @Test
    public void categoryQuery(){
        final List<BlogCategoryDto> blogCategoryDtos = blogAdminService.categoryQuery();
        System.out.println(blogCategoryDtos.size());
    }


    @Test
    public void blogListTest(){
        BlogAdminQueryReq req=new BlogAdminQueryReq();
//        req.setKeyword("apple");
        req.setRecommend(0);
        req.setOnlineStatus(1);
        req.setIsDeleted(0);
        final PageVo<BlogAdminVo> pageList = blogAdminService.getPageList(req);
        pageList.getList();


    }


    @Test
    public  void bigShotQueryTest(){
        BigShotAdminReq req=new BigShotAdminReq();
        req.setKeyword("a");
        req.setEndCreateTime(new Date());
        final PageVo<BigShotDto> vos = bigShotAdminService.bigShotQuery(req);
        System.out.println(vos.getList().size());
        vos.getList().stream().map(vo-> {
            System.out.println(vo);
            return vo;
        }).collect(Collectors.toList());

    }

    @Test
    public void pubSiteQueryTest(){
        PubSiteAdminReq req=new PubSiteAdminReq();
        req.setKeyword("a");
        req.setEndCreateTime(new Date());
        final PageVo<PubSiteDto> vos = pubSiteAdminService.pubSiteQuery(req);
        vos.getList().stream().map(vo-> {
            System.out.println(vo);
            return vo;
        }).collect(Collectors.toList());
    }
@Autowired
private RestHighLevelClient restHighLevelClient;
    @Test
    public void esTest() throws Exception {
        CreateIndexRequest req = new CreateIndexRequest("");
        restHighLevelClient.indices().create(req, RequestOptions.DEFAULT);
        QueryBuilder queryBuilder = QueryBuilders.disMaxQuery().add(QueryBuilders.termQuery("", "")).
                add(QueryBuilders.prefixQuery("", ""));
        SearchRequest sreq;
    }
}
