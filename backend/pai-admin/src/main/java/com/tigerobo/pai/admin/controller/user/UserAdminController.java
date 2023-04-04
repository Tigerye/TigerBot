package com.tigerobo.pai.admin.controller.user;

import com.tigerobo.x.pai.api.admin.auth.AdminAuthorize;
import com.tigerobo.x.pai.api.admin.req.user.UserSearchReq;
import com.tigerobo.x.pai.api.dto.user.UserDto;
import com.tigerobo.x.pai.api.vo.IdReqVo;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.biz.admin.user.UserAdminService;
import com.tigerobo.x.pai.biz.user.BlackUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@Api(value = "user")
@RequestMapping("/user/")
public class UserAdminController {
    @Autowired
    private UserAdminService userAdminService;

    @Autowired
    private BlackUserService blackUserService;
    @AdminAuthorize
    @ApiOperation(value = "列表")
    @PostMapping(path = {"list"}, consumes = "application/json", produces = "application/json")
    public PageVo<UserDto> bigShotList(@RequestBody UserSearchReq req){
        return userAdminService.query(req);
    }

    @AdminAuthorize
    @ApiOperation(value = "设置黑名单")
    @PostMapping(path = "setBlack", consumes = "application/json", produces = "application/json")
    public Map<String,Object> setBlackUser(@RequestBody IdReqVo req){

        final Integer id = req.getId();
        blackUserService.addBlackUser(id);

        return new HashMap<>();
    }


    @AdminAuthorize
    @ApiOperation(value = "删除黑名单")
    @PostMapping(path = "removeBlack", consumes = "application/json", produces = "application/json")
    public Map<String,Object> removeBlack(@RequestBody IdReqVo req){

        final Integer id = req.getId();
        blackUserService.removeBlackUser(id);
        return new HashMap<>();
    }
}
