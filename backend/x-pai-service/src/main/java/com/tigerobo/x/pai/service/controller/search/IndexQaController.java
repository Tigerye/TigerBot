package com.tigerobo.x.pai.service.controller.search;


import com.tigerobo.pai.search.api.dto.qa.QaQuestionDto;
import com.tigerobo.pai.search.api.req.base.IdUserReq;
import com.tigerobo.x.pai.biz.micro.search.QaQuestionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/index/qa")
@Api(value = "搜索问答", position = 1)
public class IndexQaController {

    @Autowired
    private QaQuestionService qaQuestionService;

    @ApiOperation(value = "索引问答问题列表", position = 4)
    @PostMapping(path = "/getQuestionList", consumes = "application/json", produces = "application/json")
    public List<QaQuestionDto> getAppSceneList(@RequestBody IdUserReq req) {
        return qaQuestionService.getQuestionList(req);
    }


    //todo userIndexListPage;

    //deleteIndex
}
