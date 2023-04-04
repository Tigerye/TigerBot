package com.tigerobo.x.pai.service.controller.eye;

import com.tigerobo.x.pai.api.auth.aspect.Authorize;
import com.tigerobo.x.pai.api.enums.MetricCountEnum;
import com.tigerobo.x.pai.api.eye.dto.EyeDayMetricDto;
import com.tigerobo.x.pai.api.eye.dto.EyeMetricCountDto;
import com.tigerobo.x.pai.api.eye.req.DailyMetricReq;
import com.tigerobo.x.pai.api.eye.req.MetricCountReq;
import com.tigerobo.x.pai.api.eye.req.ModelBoardReq;
import com.tigerobo.x.pai.biz.auth.RoleService;
import com.tigerobo.x.pai.biz.eye.BoardCallService;
import com.tigerobo.x.pai.biz.eye.EyeMetricService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/eye/board/")
@Api(value = "统计看板", tags = "统计看板")
@Authorize
public class BoardController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private EyeMetricService eyeMetricService;

    @Autowired
    private BoardCallService boardCallService;

    @ApiOperation(value = "看板信息")
    @PostMapping(path = "/viewBoard", consumes = "application/json", produces = "application/json")
    @Authorize
    public BoardCallService.BoardStatistic getModelCallByDay(@RequestBody ModelBoardReq req) {
        roleService.checkAdmin();

        return boardCallService.statisticOnModelCall(req);
//        return boardService.statisticOnModelCall(req);
    }

    @ApiOperation(value = "每日指标")
    @PostMapping(path = "/daily/metric", consumes = "application/json", produces = "application/json")
    @Authorize
    public List<EyeDayMetricDto> getDailyMetricList(@RequestBody DailyMetricReq req) {
        roleService.checkAdmin();
        return eyeMetricService.getDailyMetricList(req);
    }


    @ApiOperation(value = "指标总数统计")
    @PostMapping(path = "/getMetricCount", consumes = "application/json", produces = "application/json")
    @Authorize
    public List<EyeMetricCountDto> getMetricCount(@RequestBody MetricCountReq req) {
        roleService.checkAdmin();

        List<Integer> typeList = req.getTypeList();
        if (CollectionUtils.isEmpty(typeList)){
            typeList = new ArrayList<>();
            for (MetricCountEnum value : MetricCountEnum.values()) {
                typeList.add(value.getType());
            }

        }

        return typeList.stream().map(t->eyeMetricService.getMetricCount(t)).collect(Collectors.toList());
    }


}
