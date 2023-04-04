package com.tigerobo.x.pai.biz.converter;

import java.util.List;
import java.util.stream.Collectors;

public abstract class DtoConvert<D, P> {

    public List<P> dto2pos(List<D> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            return null;
        }
        return dtos.stream().map(d -> dto2po(d)).collect(Collectors.toList());
    }
    public abstract P dto2po(D d) ;
}
