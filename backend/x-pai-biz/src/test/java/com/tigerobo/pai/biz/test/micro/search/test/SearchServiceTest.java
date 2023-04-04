package com.tigerobo.pai.biz.test.micro.search.test;

import com.alibaba.fastjson.JSON;
import com.tigerobo.pai.search.api.base.UserBase;
import com.tigerobo.pai.search.api.dto.PaiIndexPropertyDto;
import com.tigerobo.pai.search.api.dto.UserIndexDto;
import com.tigerobo.pai.search.api.dto.qa.QaQuestionDto;
import com.tigerobo.pai.search.api.req.UserAddIndexReq;
import com.tigerobo.pai.search.api.req.application.ApplicationPageReq;
import com.tigerobo.pai.search.api.req.base.IdUserReq;
import com.tigerobo.pai.search.api.req.base.PageVo;
import com.tigerobo.pai.search.api.req.base.UserReqPage;
import com.tigerobo.pai.search.api.req.index.IndexPageReq;
import com.tigerobo.pai.search.api.req.search.ImgSearchResponse;
import com.tigerobo.pai.search.api.req.search.PaiSearchRequest;
import com.tigerobo.pai.search.api.req.search.PaiSearchResponse;
import com.tigerobo.pai.search.api.req.search.img.IQReq;
import com.tigerobo.pai.search.api.req.search.qa.QaRequest;
import com.tigerobo.pai.search.api.req.search.qa.QaResp;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.biz.micro.search.*;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class SearchServiceTest extends BaseTest {


    @Autowired
    private SearchIndexService searchIndexService;

    @Autowired
    private PaiSearchService paiSearchService;
    @Autowired
    private ImgSearchService imgSearchService;

    @Autowired
    private SearchIndexPropertyService searchIndexPropertyService;

    @Autowired
    private PaiQaSearchService paiQaSearchService;

    @Autowired
    private QaQuestionService qaQuestionService;

    @Test
    public void getTest(){

        IdUserReq req = new IdUserReq();

        req.setId(35507);
        final List<QaQuestionDto> questionList = qaQuestionService.getQuestionList(req);
        System.out.println(JSON.toJSONString(questionList));
    }

    @Test
    public void getShowPropertyLisTest(){

        IdUserReq idUserReq = new IdUserReq();
        idUserReq.setUser(new UserBase(3));

        idUserReq.setId(35481);
        ThreadLocalHolder.setUserId(3);

        final List<PaiIndexPropertyDto> showProperties = searchIndexPropertyService.getShowProperties(idUserReq);

        System.out.println(JSON.toJSONString(showProperties));
    }

    @Test
    public void createTest() {

        UserAddIndexReq req = new UserAddIndexReq();

        req.setUserIndexName("wsen0228");

        ThreadLocalHolder.setUserId(3);
        final UserIndexDto index = searchIndexService.createIndex(req);


        System.out.println(JSON.toJSONString(index));
    }


    @Test
    public void getIndexTest() {


        IdUserReq req = new IdUserReq();
        req.setId(3333);
        ThreadLocalHolder.setUserId(3);
        final UserIndexDto userIndex = searchIndexService.getUserIndex(req);

        System.out.println(JSON.toJSONString(userIndex));
    }

    @Test
    public void getUserIndexPageTest() {

        IndexPageReq req = new IndexPageReq();

        req.setPageNum(2);
        ThreadLocalHolder.setUserId(3);

        req.setApplicationId(0);

        final PageVo<UserIndexDto> userIndexPage = searchIndexService.getUserIndexPage(req);

        System.out.println(JSON.toJSONString(userIndexPage));
    }

    @Test
    public void searchTest() {


        PaiSearchRequest request = new PaiSearchRequest();

        request.setIndexId(35609);
//        request.setQuery("枪毙");
        ThreadLocalHolder.setUserId(3);
        String json = "{\"indexId\":58,\"pageNum\":1,\"pageSize\":5,\"query\":\"\"}";
        final PaiSearchRequest paiSearchRequest = JSON.parseObject(json, PaiSearchRequest.class);
        final PaiSearchResponse search = paiSearchService.search(paiSearchRequest);

        System.out.println(JSON.toJSONString(search));
    }

    @Test
    public void imgSearchTest() {

//        request.setQuery("枪毙");
        ThreadLocalHolder.setUserId(3);



        IQReq req = new IQReq();
        ThreadLocalHolder.setUserId(3);
        req.setIndexId(35609);
//        req.setImgUrl("https://ximei-oss.ximeiapp.com/d8d4f1e0299e51748742580345ff0f2c.jpeg");
        final ImgSearchResponse search = imgSearchService.search(req);

        System.out.println(JSON.toJSONString(search));
    }

    @Test
    public void qaTest() {

//        request.setQuery("枪毙");
//        ThreadLocalHolder.setUserId(3);
        QaRequest request = new QaRequest();
        request.setIndexId(35588);
        request.setQuery("孙中山认为应该使用什么来对译");
        final QaResp search = paiQaSearchService.search(request);

        System.out.println(JSON.toJSONString(search));
    }

}
