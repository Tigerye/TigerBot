package com.tigerobo.pai.biz.test.service.test.uc;

import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.uc.dto.ConfirmCodeDto;
import com.tigerobo.x.pai.biz.message.service.ConfirmCodeService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ConfirmCodeServiceTest extends BaseTest {

    @Autowired
    private ConfirmCodeService confirmCodeService;

    @Test
    public void sendTest(){

        ConfirmCodeDto codeDto = new ConfirmCodeDto();

        codeDto.setMobile("18301966691");

        String s = confirmCodeService.sendLoginConfirmCode(codeDto);
        System.out.println("code="+s);
    }

    @Test
    public void checkCodeTest(){
        ConfirmCodeDto confirmCodeDto  = new ConfirmCodeDto();
        confirmCodeDto.setMobile("18301966691");
        confirmCodeDto.setCode("5116");
        confirmCodeService.checkCode(confirmCodeDto);
    }
}
