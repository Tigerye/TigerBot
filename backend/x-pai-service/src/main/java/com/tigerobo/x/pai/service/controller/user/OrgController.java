package com.tigerobo.x.pai.service.controller.user;

import com.tigerobo.x.pai.api.auth.aspect.Authorize;
import com.tigerobo.x.pai.api.auth.entity.OrgInfoDto;
import com.tigerobo.x.pai.api.vo.ResultVO;
import com.tigerobo.x.pai.biz.auth.OrgInfoService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/org/")
@Api(value = "企业信息", position = 1100, tags = "企业信息")
public class OrgController {

    @Autowired
    private OrgInfoService orgInfoService;
    @ApiOperation(value = "获取用户认证企业信息")
    @PostMapping(path = "/getUserOrg", consumes = "application/json", produces = "application/json")
    @Authorize
    public OrgInfoDto getUserOrg() {
        Integer userId = ThreadLocalHolder.getUserId();
        return orgInfoService.getOrgInfo(userId);
    }

    @ApiOperation(value = "新增-修改用户认证企业")
    @PostMapping(path = "/createOrUpdate", consumes = "application/json", produces = "application/json")
    @Authorize
    public ResultVO createOrUpdate(@RequestBody OrgInfoDto orgInfoDto) {
        Integer userId = ThreadLocalHolder.getUserId();

        orgInfoService.addOrUpdate(orgInfoDto);
        return ResultVO.success();
    }

    @ApiOperation(value = "认证")
    @PostMapping(path = "/verify", consumes = "application/json", produces = "application/json")
    @Authorize
    public OrgInfoDto verify(@RequestBody OrgInfoDto orgInfoDto) {
        return orgInfoService.verify(orgInfoDto);
    }
}
