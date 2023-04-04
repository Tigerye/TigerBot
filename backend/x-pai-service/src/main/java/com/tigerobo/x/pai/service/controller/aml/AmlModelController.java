package com.tigerobo.x.pai.service.controller.aml;


import com.github.pagehelper.PageInfo;
import com.tigerobo.x.pai.api.aml.dto.AmlModelDto;
import com.tigerobo.x.pai.api.aml.vo.MyAmlQueryVo;
import com.tigerobo.x.pai.api.auth.aspect.Authorize;
import com.tigerobo.x.pai.api.exception.AuthorizeException;
import com.tigerobo.x.pai.api.exception.ResultCode;
import com.tigerobo.x.pai.api.vo.IdVo;
import com.tigerobo.x.pai.biz.aml.service.AmlModelServiceImpl;
import com.tigerobo.x.pai.biz.aml.service.AmlViewServiceImpl;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/aml/model")
@Api(value = "自主训练查看模型", position = 5300, tags = "自主训练查看模型")
public class AmlModelController {

    @Autowired
    private AmlViewServiceImpl amlViewService;
    @Autowired
    private AmlModelServiceImpl amlModelService;

    @ApiOperation(value = "查看我的模型列表")
    @PostMapping(path = "/get_my_model_list", consumes = "application/json", produces = "application/json")
    @Authorize
    public PageInfo<AmlModelDto> getMyModelList(@RequestBody MyAmlQueryVo req) {
        Integer userId = ThreadLocalHolder.getUserId();
        if (userId == null){
            throw new AuthorizeException(ResultCode.USER_NOT_LOGIN, "没有登录", null);
        }
        return amlViewService.getMyModelList(req,String.valueOf(userId));
    }



    @ApiOperation(value = "查看自主训练模型基础信息")
    @PostMapping(path = "/userGetModel", consumes = "application/json", produces = "application/json")
    @Authorize
    public AmlModelDto get_aml_model(@RequestBody IdVo req) {
        Integer id = req.getId();

        Integer userId = ThreadLocalHolder.getUserId();
        if (userId == null){
            throw new AuthorizeException(ResultCode.USER_NOT_LOGIN, "没有登录", null);
        }
        return amlViewService.userGetModelById(id,userId);
    }


    @ApiOperation(value = "模型下线")
    @PostMapping(path = "/offline", consumes = "application/json", produces = "application/json")
    @Authorize
    public void offline(@RequestBody IdVo req) {
        Integer id = req.getId();
        Integer userId = ThreadLocalHolder.getUserId();
        if (userId == null){
            throw new AuthorizeException(ResultCode.USER_NOT_LOGIN, "没有登录", null);
        }
        amlModelService.offline(id,userId);
    }


    @ApiOperation(value = "模型上线")
    @PostMapping(path = "/online", consumes = "application/json", produces = "application/json")
    @Authorize
    public void online(@RequestBody IdVo req) {
        Integer id = req.getId();
        Integer userId = ThreadLocalHolder.getUserId();
        if (userId == null){
            throw new AuthorizeException(ResultCode.USER_NOT_LOGIN, "没有登录", null);
        }
        amlModelService.online(id,userId);
    }

}
