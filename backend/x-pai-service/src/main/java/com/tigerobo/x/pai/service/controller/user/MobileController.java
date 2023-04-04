package com.tigerobo.x.pai.service.controller.user;


import com.alibaba.fastjson.JSON;
import com.tigerobo.x.pai.api.vo.ResultVO;
import com.tigerobo.x.pai.api.uc.dto.ConfirmCodeDto;
import com.tigerobo.x.pai.biz.message.service.ConfirmCodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
@Slf4j
@RestController
@RequestMapping("/uc/mobile/")
@Api(value = "验证码模块", position = 2900, tags = "验证码模块")
public class MobileController {

    @Autowired
    private ConfirmCodeService confirmCodeService;
    //new
    @RequestMapping(value = "/send_mobile_code", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation("发送短信验证码")
    public ResultVO sendConfirmCode(
            HttpServletRequest request,
            @RequestBody ConfirmCodeDto confirmCodeDto) {

        log.info("send_mobile_code:{}", JSON.toJSONString(confirmCodeDto));
        confirmCodeService.sendLoginConfirmCode(confirmCodeDto);
        return ResultVO.success();
    }

}
