package com.tigerobo.x.pai.service.controller.aml;

import com.tigerobo.x.pai.api.aml.dto.AmlInfoDto;
import com.tigerobo.x.pai.api.aml.dto.scene.AmlCreateDto;
import com.tigerobo.x.pai.api.aml.dto.scene.AmlStartTrainDto;
import com.tigerobo.x.pai.api.aml.dto.scene.AmlUploadDataDto;
import com.tigerobo.x.pai.api.aml.service.AmlInfoService;
import com.tigerobo.x.pai.api.auth.aspect.Authorize;
import com.tigerobo.x.pai.api.auth.service.UserService;
import com.tigerobo.x.pai.api.exception.AuthorizeException;
import com.tigerobo.x.pai.api.exception.ResultCode;
import com.tigerobo.x.pai.api.vo.IdVo;
import com.tigerobo.x.pai.api.vo.ResultVO;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/aml/base")
@Api(value = "自主训练流程操作", position = 5100, tags = "自主训练流-自主训练流程操作")
public class AmlBaseController {

    @Autowired
    private AmlInfoService amlInfoService;

    @Autowired
    private UserService userService;


    @ApiOperation(value = "删除")
    @PostMapping(path = "/delete", consumes = "application/json", produces = "application/json")
    @Authorize
    public ResultVO create(@RequestBody IdVo req) {
        Integer userId = ThreadLocalHolder.getUserId();
        if (userId == null){
            throw new AuthorizeException(ResultCode.USER_NOT_LOGIN, "not Login!", null);
        }

        amlInfoService.deleteAml(req.getId(),userId);
        return ResultVO.success();
    }



    @ApiOperation(value = "创建")
    @PostMapping(path = "/create", consumes = "application/json", produces = "application/json")
    @Authorize
    public AmlInfoDto create(@RequestBody AmlCreateDto req) {
        Integer userId = ThreadLocalHolder.getUserId();
        if (userId == null){
            throw new AuthorizeException(ResultCode.USER_NOT_LOGIN, "not Login!", null);
        }
        return  amlInfoService.addAml(req,userId);
    }

    @ApiOperation(value = "导入训练数据")
    @PostMapping(path = "/import_dataset", consumes = "application/json", produces = "application/json")
    @Authorize
    public ResultVO import_dataset(@RequestBody AmlUploadDataDto req) {
        Integer userId = ThreadLocalHolder.getUserId();
        if (userId == null){
            throw new AuthorizeException(ResultCode.USER_NOT_LOGIN, "not Login!", null);
        }
        amlInfoService.importDataset(req,userId);
        return ResultVO.success();
    }

    @ApiOperation(value = "开始训练")
    @PostMapping(path = "/start_train", consumes = "application/json", produces = "application/json")
    @Authorize
    public ResultVO start_train(@RequestBody AmlStartTrainDto req) {

        Integer userId = ThreadLocalHolder.getUserId();
        if (userId == null){
            throw new AuthorizeException(ResultCode.USER_NOT_LOGIN, "not Login!", null);
        }

        amlInfoService.startTrain(req,userId);
        return  ResultVO.success();
    }

}
