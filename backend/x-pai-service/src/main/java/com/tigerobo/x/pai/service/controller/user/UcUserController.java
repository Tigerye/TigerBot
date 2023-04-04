package com.tigerobo.x.pai.service.controller.user;

import com.tigerobo.x.pai.api.auth.aspect.Authorize;
import com.tigerobo.x.pai.api.auth.entity.Authorization;
import com.tigerobo.x.pai.api.auth.entity.User;
import com.tigerobo.x.pai.api.auth.service.UserService;
import com.tigerobo.x.pai.api.vo.IdReqVo;
import com.tigerobo.x.pai.biz.biz.OssService;
import com.tigerobo.x.pai.biz.user.UcUserServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/")
@Api(value = "用户模块", position = 2900, tags = "用户模块")
public class UcUserController {

    @Autowired
    private UcUserServiceImpl ucUserService;

    @Autowired
    private UserService userService;
//    @Autowired
//    private OSSApi ossApi;

    @Autowired
    private OssService ossService;
//拿取用户信息
    @ApiOperation(value = "用户-信息")
    @PostMapping(path = {"/web/uc/get_user_info"}, produces = "application/json")
//    @Authorize
    public Authorization getUserInfo() {
        Authorization authorization = ucUserService.getUserLoginInfo();
        return authorization;
    }

    @ApiOperation(value = "用户-基础-信息")
    @PostMapping(path = {"/web/uc/get_user_com_info"}, produces = "application/json")
//    @Authorize
    public User getUserComInfo(IdReqVo reqVo) {

        return userService.getCacheUserByOther(reqVo.getId());
    }


    @ApiOperation(value = "上传头像", position = 100)
    @PostMapping(path = "/user/upload-avatar")
    @Authorize
    String uploadAvatar(@NotNull @RequestParam("file") MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        byte[] data = file.getBytes();
        long timestamp = System.currentTimeMillis();
        String objectName = "biz/user/avatar/tmp/" + timestamp + "-" + fileName;
        return ossService.uploadImg(data,objectName);
    }


}
