package com.tigerobo.x.pai.service.controller.user;

import com.github.pagehelper.PageInfo;
import com.tigerobo.x.pai.api.auth.vo.GroupVo;
import com.tigerobo.x.pai.api.vo.QueryVo;
import com.tigerobo.x.pai.biz.biz.OssService;
import com.tigerobo.x.pai.biz.biz.service.WebGroupService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description: 授权模块-用户组服务接口
 * @modified By:
 * @version: $
 */
@RestController
@RequestMapping("/group")
@Api(value = "授权模块-用户组服务接口", position = 1200, tags = "授权模块-用户组服务接口")
//@Conditional(ConditionConfig.XPaiAuthCondition.class)
public class GroupController {

    @Autowired
    private OssService ossService;

    @Autowired
    private WebGroupService webGroupService;

    @ApiOperation(value = "上传Logo", position = 100)
    @PostMapping(path = "/upload-logo")
    String uploadLogo(@NotNull @RequestParam("file") MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        byte[] data = file.getBytes();
        long timestamp = System.currentTimeMillis();
        String objectName = "biz/group/logo/tmp/" + timestamp + "-" + fileName;

        return ossService.uploadFile(data, objectName);
    }


    //    @Override
    @ApiOperation(value = "查询", position = 53)
    @PostMapping(path = "/query", consumes = "application/json", produces = "application/json")
    public PageInfo<GroupVo> query(@Valid @RequestBody QueryVo queryVo) {

        return webGroupService.getPublic();
    }


}
