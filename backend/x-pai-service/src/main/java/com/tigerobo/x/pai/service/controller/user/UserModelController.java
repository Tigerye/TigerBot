package com.tigerobo.x.pai.service.controller.user;

import com.tigerobo.x.pai.api.auth.aspect.Authorize;
import com.tigerobo.x.pai.api.dto.ModelCommitDto;
import com.tigerobo.x.pai.api.vo.IdReqVo;
import com.tigerobo.x.pai.api.vo.ResultVO;
import com.tigerobo.x.pai.api.vo.UuidVo;
import com.tigerobo.x.pai.api.vo.user.UserModelVo;
import com.tigerobo.x.pai.biz.user.UserModelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user/model/")
@Api(value = "用户模型", tags = "用户模型")
public class UserModelController {

    @Autowired
    private UserModelService userModelService;


    @ApiOperation(value = "模型详情")
    @PostMapping(path = {"/getModelDetail"}, consumes = "application/json", produces = "application/json")
    @Authorize
    public UserModelVo getDetail(@RequestBody UuidVo idVo) {
        String uuid = idVo.getUuid();
        return userModelService.getModelDetail(uuid);
    }


    @Authorize
    @ApiOperation(value = "获取模型commit-列表")
    @PostMapping(path = {"/getCommitList"}, consumes = "application/json", produces = "application/json")
    public List<ModelCommitDto> getCommitList(@RequestBody UuidVo idVo) {
        String uuid = idVo.getUuid();
        return userModelService.getCommitList(uuid);
    }

    @Authorize
    @ApiOperation(value = "提交模型版本")
    @PostMapping(path = {"/commit"}, consumes = "application/json", produces = "application/json")
    public ResultVO commitModel(@RequestBody ModelCommitDto commitDto) {

        userModelService.addCommit(commitDto);
        return ResultVO.success();
    }

    @Authorize
    @ApiOperation(value = "删除模型提交")
    @PostMapping(path = {"/delCommit"}, consumes = "application/json", produces = "application/json")
    public ResultVO commitModel(@RequestBody IdReqVo idReqVo) {
        userModelService.deleteCommit(idReqVo.getId());
        return ResultVO.success();
    }


}
