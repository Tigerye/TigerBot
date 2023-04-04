package com.tigerobo.x.pai.service.controller.search;


import com.tigerobo.pai.search.api.dto.IndexDocTaskDto;
import com.tigerobo.pai.search.api.dto.doc.IndexDocObjectsDto;
import com.tigerobo.pai.search.api.req.base.IdUserReq;
import com.tigerobo.pai.search.api.req.base.PageVo;
import com.tigerobo.pai.search.api.req.base.UserReqPage;
import com.tigerobo.pai.search.api.req.index.IndexDocFileListReq;
import com.tigerobo.pai.search.api.req.index.IndexDocFileReq;
import com.tigerobo.x.pai.api.auth.aspect.Authorize;
import com.tigerobo.x.pai.api.vo.ResultVO;
import com.tigerobo.x.pai.biz.micro.search.SearchIndexDocService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/indexData/")
@Api(value = "索引-数据", tags = "索引-数据")
public class IndexDocController {

    @Autowired
    private SearchIndexDocService searchIndexDocService;

    @ApiOperation(value = "索引-上传数据", tags = "索引-上传数据")
    @Authorize
    @PostMapping(path = "/addDocTask", consumes = "application/json", produces = "application/json")
    public Map<String, Object> addDocTask(@RequestBody IndexDocFileReq req) {

        final Map<String, Object> map = searchIndexDocService.addDocFile(req);

        return map;
    }


    @ApiOperation(value = "索引-批量上传数据", tags = "索引-批量上传数据")
    @Authorize
    @PostMapping(path = "/addDocListTask", consumes = "application/json", produces = "application/json")
    public Map<String, Object> addDocListTask(@RequestBody IndexDocFileListReq req) {

        final Map<String, Object> map = searchIndexDocService.addDocFileList(req);

        return map;
    }


    @ApiOperation(value = "查看上传文件任务详情", tags = "查看上传文件任务详情")
    @Authorize
    @PostMapping(path = "/getDocTaskDetail", consumes = "application/json", produces = "application/json")
    public IndexDocTaskDto getDocTaskDetail(@RequestBody IdUserReq req) {

        return searchIndexDocService.getDocTaskDetail(req);
    }


    @ApiOperation(value = "查看任务列表", tags = "查看任务列表")
    @Authorize
    @PostMapping(path = "/getUserDocList", consumes = "application/json", produces = "application/json")
    public PageVo getUserDocList(@RequestBody UserReqPage req) {

        return searchIndexDocService.getUserDocList(req);
    }


    @ApiOperation(value = "手动jsons添加文档", position = 50)
    @PostMapping(path = "addJsonDatas", consumes = "application/json", produces = "application/json")
    @Authorize
    public ResultVO addJsonDatas(HttpServletRequest request, @Valid @RequestBody IndexDocObjectsDto objectDto)  {
        final Integer userId = ThreadLocalHolder.getUserId();
        searchIndexDocService.addDocObjects(objectDto,userId);
        return ResultVO.success();
    }

    @Deprecated
    @ApiOperation(value = "手动json添加文档", position = 50)
    @PostMapping(path = "addJsonData", consumes = "application/json", produces = "application/json")
    @Authorize
    public ResultVO addJsonData(HttpServletRequest request, @Valid @RequestBody IndexDocObjectsDto objectDto)  {
        final Integer userId = ThreadLocalHolder.getUserId();
        searchIndexDocService.addDocObject(objectDto,userId);
        return ResultVO.success();
    }
}
