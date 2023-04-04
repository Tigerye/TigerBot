package com.tigerobo.x.pai.service.controller.search;

import com.tigerobo.pai.search.api.req.search.ImgSearchResponse;
import com.tigerobo.pai.search.api.req.search.PaiSearchRequest;
import com.tigerobo.pai.search.api.req.search.PaiSearchResponse;
import com.tigerobo.pai.search.api.req.search.img.IQReq;
import com.tigerobo.pai.search.api.req.search.qa.QaRequest;
import com.tigerobo.pai.search.api.req.search.qa.QaResp;
import com.tigerobo.x.pai.api.auth.aspect.Authorize;
import com.tigerobo.x.pai.biz.micro.search.ImgSearchService;
import com.tigerobo.x.pai.biz.micro.search.PaiQaSearchService;
import com.tigerobo.x.pai.biz.micro.search.PaiSearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/indexSearch")
@Api(value = "索引-搜索", tags = "索引-搜索")
public class SearchController {

    @Autowired
    private PaiSearchService paiSearchService;


    @Autowired
    private PaiQaSearchService paiQaSearchService;

    @Autowired
    private ImgSearchService imgSearchService;

    @ApiOperation(value = "索引-搜索入口", tags = "索引-搜索入口")
//    @Authorize
    @PostMapping(path = "", consumes = "application/json", produces = "application/json")
    public PaiSearchResponse search(@RequestBody PaiSearchRequest request) {
        return paiSearchService.search(request);
    }

    @ApiOperation(value = "索引-搜索", tags = "索引-搜索")
//    @Authorize
    @PostMapping(path = "search", consumes = "application/json", produces = "application/json")
    public PaiSearchResponse dosearch(@RequestBody PaiSearchRequest request) {
        return paiSearchService.search(request);
    }

    @ApiOperation(value = "索引-问答", tags = "索引-问答")
//    @Authorize
    @PostMapping(path = "qa", consumes = "application/json", produces = "application/json")
    public QaResp qa(@RequestBody QaRequest request) {
        return paiQaSearchService.search(request);
    }


    @ApiOperation(value = "索引-图片搜索")
//    @Authorize
    @PostMapping(path = "imgSearch", consumes = "application/json", produces = "application/json")
    public ImgSearchResponse imgSearch(@RequestBody IQReq request) {
        return imgSearchService.search(request);
    }



}
