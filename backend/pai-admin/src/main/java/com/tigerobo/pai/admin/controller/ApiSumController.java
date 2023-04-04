package com.tigerobo.pai.admin.controller;

import com.tigerobo.x.pai.biz.serving.ApiSumService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@Api(value = "api调用统计",  tags = "api调用统计")
@RequestMapping("/api/")
public class ApiSumController {
    @Autowired
    private ApiSumService apiSumService;

    @ApiOperation(value = "总量")
    @RequestMapping(path = "getTotal",method = {RequestMethod.GET,RequestMethod.POST})
    public Map<String,Object> getTotal(){

        Integer apiTotal = apiSumService.getApiTotal();
        Map<String,Object> map = new HashMap<>();
        map.put("total",apiTotal);
        return map;
    }

}
