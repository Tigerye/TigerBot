package com.tigerobo.x.pai.service.controller.demand;

import com.tigerobo.x.pai.api.auth.aspect.Authorize;
import com.tigerobo.x.pai.api.config.ConditionConfig;
import com.tigerobo.x.pai.api.vo.FileReqVo;
import com.tigerobo.x.pai.biz.biz.OssService;
import com.tigerobo.x.pai.biz.oss.OSSHome;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description: 业务模块-数据服务接口
 * @modified By:
 * @version: $
 * todo
 */
@RestController
@RequestMapping("/dataset")
@Api(value = "业务模块-数据服务接口", position = 2400, tags = "业务模块-数据服务接口")
public class DatasetController {//extends GeneralController<DatasetService, DatasetReqVo, DatasetVo> {

    @Autowired
    private OssService ossService;

    @Autowired
    private OSSHome ossHome;

//    @Authorize
    @ApiOperation(value = "上传", position = 100)
    @PostMapping(path = "/upload")
    String upload(@NotNull @RequestParam("file") MultipartFile file) throws Exception {
        byte[] data = file.getBytes();
        String uuid = DigestUtils.md5Hex(data);
        String key = "biz/dataset/tmp/" + uuid;
        String url = ossService.uploadFile(data, key);

//        this.ossApi.upload("biz/dataset/tmp/" + uuid, data, null);
//        return this.ossApi.getUrl2("biz/dataset/tmp/" + uuid);
        return url;
    }

    private final static String ROLE_SESSION_NAME = "dataset-upload";

    @ApiOperation(value = "获取上传OSS-STS", position = 100)
    @PostMapping(path = "/oss-token")
//    @Authorize
    OSSHome.OssToken ossToken() throws Exception {
//        Authorization authorization = this.authorizeService.authorize(fileReqVo);
//        String gid = authorization.getGid();
        OSSHome.OssToken ossToken = this.ossHome.ossToken(ROLE_SESSION_NAME);
        ossToken.setObjectDir("biz/dataset/tmp/");
        return ossToken;
    }
}
