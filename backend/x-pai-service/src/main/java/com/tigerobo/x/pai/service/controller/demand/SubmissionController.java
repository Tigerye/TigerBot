package com.tigerobo.x.pai.service.controller.demand;

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
 * @description: 业务模块-提交服务接口
 * @modified By:
 * @version: $
 */
@RestController
@RequestMapping("/submission")
@Api(value = "业务模块-提交服务接口", position = 2500, tags = "业务模块-提交服务接口")
//@Conditional(ConditionConfig.XPaiBizCondition.class)
public class SubmissionController {
    @Autowired
    private OSSHome ossHome;
    @Autowired
    private OssService ossService;
    @ApiOperation(value = "上传", position = 100)
    @PostMapping(path = "/upload")
    String upload(@NotNull @RequestParam("file") MultipartFile file) throws Exception {
        byte[] data = file.getBytes();
        String uuid = DigestUtils.md5Hex(data);
        String objectName = "biz/submission/tmp/" + uuid;

        return ossService.uploadFile(data,objectName);
    }

    private final static String ROLE_SESSION_NAME = "submission-upload";

    @ApiOperation(value = "获取上传OSS-STS", position = 100)
    @PostMapping(path = "/oss-token")
    OSSHome.OssToken ossToken(@NotNull @RequestBody FileReqVo fileReqVo) throws Exception {
//        Authorization authorization = this.authorizeService.authorize(fileReqVo);
//        String gid = authorization.getGid();
        OSSHome.OssToken ossToken = this.ossHome.ossToken(ROLE_SESSION_NAME);
        ossToken.setObjectDir("biz/submission/tmp/");
        return ossToken;
    }
}
