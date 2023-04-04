package com.tigerobo.x.pai.biz.biz;

import com.tigerobo.x.pai.api.vo.IBusinessDetailVo;

public interface IBusinessDetailFetchService {

    IBusinessDetailVo getBusinessDetail(String id);

    default Integer getIntId(String id){
        if (id == null){
            return null;
        }
        if (!id.matches("\\d+")){
            return null;
        }
        return Integer.parseInt(id);
    }
}
