package com.tigerobo.x.pai.service.controller.basket;

import com.alibaba.fastjson.JSON;
import com.tigerobo.x.pai.api.basket.CorrectWordBasketReq;
import com.tigerobo.x.pai.api.vo.ResultVO;
import com.tigerobo.x.pai.biz.basket.CorrectWordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/basket/")
@Api(value = "搜集", tags = "数据搜集")
public class BasketController {

    @Autowired
    CorrectWordService correctWordService;

    @ApiOperation(value = "上传矫正错别字")
    @PostMapping(path = "uploadCorrectWord", produces = "application/json")
    public ResultVO correctWord(HttpServletRequest request, @Valid @RequestBody CorrectWordBasketReq req) {

        String key = request.getParameter("key");

        if (StringUtils.isBlank(key)) {
            log.warn("key:{},req:{}", key, JSON.toJSONString(req));
        }

        correctWordService.add(req, key);

        return ResultVO.success();
    }

}
