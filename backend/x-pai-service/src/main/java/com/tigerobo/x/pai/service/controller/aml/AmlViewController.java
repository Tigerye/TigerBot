package com.tigerobo.x.pai.service.controller.aml;

import com.github.pagehelper.PageInfo;
import com.tigerobo.x.pai.api.aml.dto.AmlBaseModelDto;
import com.tigerobo.x.pai.api.aml.dto.AmlInfoDto;
import com.tigerobo.x.pai.api.aml.dto.AmlStatisticDto;
import com.tigerobo.x.pai.api.aml.dto.ModelDataItem;
import com.tigerobo.x.pai.api.aml.dto.dataset.AmlDatasetItemReq;
import com.tigerobo.x.pai.api.aml.vo.AmlTrainViewVo;
import com.tigerobo.x.pai.api.aml.vo.MyAmlQueryVo;
import com.tigerobo.x.pai.api.auth.aspect.Authorize;
import com.tigerobo.x.pai.api.enums.AmlStatusEnum;
import com.tigerobo.x.pai.api.exception.AuthorizeException;
import com.tigerobo.x.pai.api.exception.ResultCode;
import com.tigerobo.x.pai.api.vo.IdVo;
import com.tigerobo.x.pai.biz.aml.service.AmlBaseModelServiceImpl;
import com.tigerobo.x.pai.biz.aml.service.AmlViewServiceImpl;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/aml/view")
@Api(value = "自主训练查看接口", position = 5200, tags = "自主训练详情")
public class AmlViewController {


    @Autowired
    private AmlViewServiceImpl amlViewService;



    @Autowired
    private AmlBaseModelServiceImpl amlBaseModelService;
    @ApiOperation(value = "查看我的自主训练列表")
    @PostMapping(path = "/get_my_aml_list", consumes = "application/json", produces = "application/json")
    @Authorize
    public PageInfo<AmlInfoDto> getMyAmlList(@RequestBody MyAmlQueryVo req) {
        Integer userId = ThreadLocalHolder.getUserId();
        if (userId == null){
            throw new AuthorizeException(ResultCode.USER_NOT_LOGIN, "not Login!", null);
        }
        return amlViewService.getMyAmlList(req,String.valueOf(userId));
    }




    @ApiOperation(value = "查看自主训练基础信息")
    @PostMapping(path = "/get_aml_base_info", consumes = "application/json", produces = "application/json")
    @Authorize
    public AmlInfoDto get_aml_base_info(@RequestBody IdVo req) {
        Integer id = req.getId();
        Integer userId = ThreadLocalHolder.getUserId();
        if (userId == null){
            throw new AuthorizeException(ResultCode.USER_NOT_LOGIN, "not Login!", null);
        }
        return amlViewService.getAmlInfo(id,userId);
    }

    @ApiOperation(value = "查看基础模型列表")
    @PostMapping(path = "/get_base_model_list", consumes = "application/json", produces = "application/json")
    public List<AmlBaseModelDto> get_base_model_list() {
        return amlBaseModelService.getBaseModelList();
    }

    @ApiOperation(value = "查看数据导入信息")
    @PostMapping(path = "/view_dataset_import", consumes = "application/json", produces = "application/json")
    @Authorize
    public AmlInfoDto view_dataset_import(@RequestBody IdVo req) {

        Integer id = req.getId();

        Integer userId = ThreadLocalHolder.getUserId();
        if (userId == null){
            throw new AuthorizeException(ResultCode.USER_NOT_LOGIN, "not Login!", null);
        }
        return  amlViewService.getAmlImportView(req.getId(),userId);
    }

    @ApiOperation(value = "查看数据集详情-穿透信息统计")
    @PostMapping(path = "/view_dataset_statistic", consumes = "application/json", produces = "application/json")
    @Authorize
    public AmlStatisticDto view_dataset_statistic(@RequestBody IdVo req) {

        Integer userId = ThreadLocalHolder.getUserId();
        if (userId == null){
            throw new AuthorizeException(ResultCode.USER_NOT_LOGIN, "not Login!", null);
        }

        return amlViewService.getDatasetStatistic(req.getId(), userId);
    }


    @ApiOperation(value = "查看数据集详情-穿透信息列表")
    @PostMapping(path = "/view_dataset_item_page", consumes = "application/json", produces = "application/json")
    @Authorize
    public PageInfo<ModelDataItem> view_dataset_detail(@RequestBody AmlDatasetItemReq req) {

        Integer userId = ThreadLocalHolder.getUserId();
        if (userId == null){
            throw new AuthorizeException(ResultCode.USER_NOT_LOGIN, "not Login!", null);
        }

        return amlViewService.getDatasetItemViewPage(req, userId);
    }


    @ApiOperation(value = "查看训练详情")
    @PostMapping(path = "/view_train", consumes = "application/json", produces = "application/json")
    @Authorize
    public AmlTrainViewVo view_train(@RequestBody IdVo req) {

        Integer userId = ThreadLocalHolder.getUserId();
        if (userId == null){
            throw new AuthorizeException(ResultCode.USER_NOT_LOGIN, "not Login!", null);
        }

        return amlViewService.getTrainView(req.getId(),userId);
    }


    @ApiOperation(value = "查看aml状态枚举")
    @PostMapping(path = "/get_aml_status_list", consumes = "application/json", produces = "application/json")
    public Map<Byte,String> get_aml_status_list() {
        Map<Byte,String> map = new LinkedHashMap<>();
        for (AmlStatusEnum value : AmlStatusEnum.values()) {
            map.put(value.getStatus(),value.getName());
        }
        return map;
    }

}
