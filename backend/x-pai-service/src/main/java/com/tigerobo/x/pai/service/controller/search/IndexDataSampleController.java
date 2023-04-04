package com.tigerobo.x.pai.service.controller.search;


import com.tigerobo.pai.search.api.dto.sample.DataSampleDto;
import com.tigerobo.pai.search.api.req.base.TypeReq;
import com.tigerobo.x.pai.biz.micro.search.IndexDataSampleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/indexDataSample/")
@Api(value = "搜索数据样例")
public class IndexDataSampleController {

    @Autowired
    private IndexDataSampleService indexDataSampleService;


    @ApiOperation(value = "样例列表", position = 4)
    @PostMapping(path = "/getSampleList", produces = "application/json")
    public List<DataSampleDto> getAppSceneList() {
        return indexDataSampleService.getDataSample();
    }

    @ApiOperation(value = "样例列表by类型", position = 4)
    @PostMapping(path = "/getSampleListByType", produces = "application/json")
    public List<DataSampleDto> getAppSceneList(@RequestBody TypeReq req) {
        return indexDataSampleService.getDataSample(req);
    }
}
