package com.tigerobo.x.pai.service.controller.search;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Preconditions;
import com.tigerobo.pai.search.api.dto.doc.IndexDocObjectsDto;
import com.tigerobo.pai.search.api.req.search.PaiSearchRequest;
import com.tigerobo.pai.search.api.req.search.PaiSearchResponse;
import com.tigerobo.x.pai.api.auth.service.UserService;
import com.tigerobo.x.pai.api.vo.ResultVO;
import com.tigerobo.x.pai.biz.micro.search.PaiSearchService;
import com.tigerobo.x.pai.biz.micro.search.SearchIndexDocService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description: 服务模块-API请求接口
 * @modified By:
 * @version: $
 */
@RestController
@RequestMapping("/indexApi/")
@Api(value = "API搜索", position = 1, tags = "API搜索")
public class SearchApiController {


    @Autowired
    private UserService userService;

    @Autowired
    private PaiSearchService paiSearchService;

    @Autowired
    private SearchIndexDocService searchIndexDocService;

    @ApiOperation(value = "搜索接口调用", position = 50)
    @PostMapping(path = "search", consumes = "application/json", produces = "application/json")
//    @Authorize(isAccessToken = true)
    public PaiSearchResponse search(HttpServletRequest request, @Valid @RequestBody JSONObject params)  {
        String apiKey = request.getParameter("apiKey");
        String appId = request.getParameter("appId");
        String accessToken = request.getParameter("accessToken");

        String indexIdStr = request.getParameter("indexId");

        Integer userId = userService.getIdByUuidCache(appId);
        Validate.isTrue(accessToken!=null&&accessToken.length()>10,"用户校验失败");
        Preconditions.checkArgument(userId != null && userId > 0, "用户不存在");
        Validate.isTrue(indexIdStr!=null&&indexIdStr.matches("\\d+"),"索引参数错误");

        final int indexId = Integer.parseInt(indexIdStr);
        //todo 校验token

        PaiSearchRequest searchRequest = new PaiSearchRequest();
        Integer pageNum = params.getInteger("pageNo");
        if (pageNum == null||pageNum<=0){
            pageNum = params.getInteger("pageNo");
        }
        Integer pageSize = params.getInteger("pageSize");
        final String query = params.getString("query");
        searchRequest.setQuery(query);
        searchRequest.setPageNum(pageNum);
        searchRequest.setPageSize(pageSize);
        searchRequest.setIndexId(indexId);

        return paiSearchService.apiSearch(searchRequest,userId);
    }

    @ApiOperation(value = "添加文档", position = 50)
    @PostMapping(path = "addDoc", consumes = "application/json", produces = "application/json")
//    @Authorize(isAccessToken = true)
    public ResultVO addDoc(HttpServletRequest request, @Valid @RequestBody JSONObject params)  {
        String apiKey = request.getParameter("apiKey");
        String appId = request.getParameter("appId");
        String accessToken = request.getParameter("accessToken");

        String indexIdStr = request.getParameter("indexId");

        Integer userId = userService.getIdByUuidCache(appId);
        Validate.isTrue(accessToken!=null&&accessToken.length()>10,"用户校验失败");
        Preconditions.checkArgument(userId != null && userId > 0, "用户不存在");
        Validate.isTrue(indexIdStr!=null&&indexIdStr.matches("\\d+"),"索引参数错误");

        final int indexId = Integer.parseInt(indexIdStr);

        IndexDocObjectsDto objectDto = new IndexDocObjectsDto();
        objectDto.setIndexId(indexId);

        objectDto.setDataJsons(JSON.toJSONString(params));

        searchIndexDocService.addDocObjects(objectDto,userId);
        return ResultVO.success();
    }

}
