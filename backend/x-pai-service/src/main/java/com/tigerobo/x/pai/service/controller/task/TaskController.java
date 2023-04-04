package com.tigerobo.x.pai.service.controller.task;

import com.alibaba.fastjson.JSON;
import com.tigerobo.x.pai.api.vo.TaskQueryVo;
import com.tigerobo.x.pai.api.vo.biz.TaskVo;
import com.tigerobo.x.pai.api.config.ConditionConfig;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.QueryVo;
import com.tigerobo.x.pai.biz.biz.service.WebTaskService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description: 业务模块-需求任务接口
 * @modified By:
 * @version: $
 * todo 确认前端调用模型
 */
@Slf4j
@RestController
@RequestMapping("/task")
@Api(value = "需求任务接口",tags = "需求任务接口")
//@Conditional(ConditionConfig.XPaiBizCondition.class)
public class TaskController{
    @Autowired
    private WebTaskService webTaskService;


    @ApiOperation(value = "查询")
    @PostMapping(path = "/query", consumes = "application/json", produces = "application/json")
    public PageVo<TaskVo> query(@Valid @RequestBody TaskQueryVo queryVo) {
        Integer userId = ThreadLocalHolder.getUserId();
        log.info("userId:{},task/query:{}",userId,JSON.toJSONString(queryVo));

        return this.webTaskService.query(queryVo);
    }
//
//    @ApiOperation(value = "列表")
//    @PostMapping(path = "/list", consumes = "application/json", produces = "application/json")
//    public List<TaskVo> list(@Valid @RequestBody QueryVo queryVo) {
//        return this.webTaskService.query(queryVo).getList();
//    }

}
