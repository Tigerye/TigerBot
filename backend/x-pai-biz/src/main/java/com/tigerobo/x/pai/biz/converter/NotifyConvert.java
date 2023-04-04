package com.tigerobo.x.pai.biz.converter;

import com.tigerobo.x.pai.api.vo.notify.NotifyVo;
import com.tigerobo.x.pai.dal.biz.entity.user.UserNotifyPo;
import org.springframework.beans.BeanUtils;

public class NotifyConvert {


    public static NotifyVo po2vo(UserNotifyPo po){
        NotifyVo vo = new NotifyVo();

        BeanUtils.copyProperties(po,vo);
        return vo;
    }
}
