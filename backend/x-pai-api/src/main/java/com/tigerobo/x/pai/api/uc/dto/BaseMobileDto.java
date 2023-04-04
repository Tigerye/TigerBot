package com.tigerobo.x.pai.api.uc.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @author:Wsen
 * @time: 2020/4/23
 **/
@Data
@Builder
public class BaseMobileDto {
    private String mobile;
    private String areaCode;


    public String getFullMobile(){
        String full = "";
        if (areaCode != null){
            full+= areaCode+"|";
        }
        if (mobile !=null){
            full += mobile;
        }
        return full;
    }
}
