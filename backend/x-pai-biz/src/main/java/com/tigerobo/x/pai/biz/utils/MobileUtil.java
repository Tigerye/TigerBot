package com.tigerobo.x.pai.biz.utils;

import com.tigerobo.x.pai.api.uc.dto.BaseMobileDto;
import org.apache.commons.lang3.StringUtils;

/**
 * @author:Wsen
 * @time: 2020/4/23
 **/
public class MobileUtil {
    public static BaseMobileDto getBaseMobile(String area,String oriMobile) {

        if (StringUtils.isNotBlank(area)){
            return getBaseMobile(area+"|"+oriMobile);
        }
        return getBaseMobile(oriMobile);
    }
    public static BaseMobileDto getBaseMobile(String oriMobile) {
        int index = oriMobile.indexOf("|");
        String mobile, areaCode;
        if (index > 0 && index < oriMobile.length()) {
            areaCode = oriMobile.substring(0, index);
            mobile = oriMobile.substring(index + 1);
        } else {
            areaCode = "+86";
            mobile = oriMobile;
        }

        while (mobile.startsWith("|")){
            mobile = mobile.substring(1);
        }
        return BaseMobileDto.builder().areaCode(areaCode).mobile(mobile).build();
    }
}
