package com.tigerobo.x.pai.api.vo;

import com.tigerobo.x.pai.api.vo.biz.PageReqVo;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PageVo<T> {


    public PageVo(){}
    public PageVo(PageReqVo req){
        if (req!=null){
            pageNum = req.getPageNum();
            pageSize = req.getPageSize();
        }
    }

    private List<T> list = new ArrayList<>();
    private int pageNum;
    private int pageSize;
    private long total;
    private boolean hasMore;

    public boolean isHasMore(){
        return total>pageNum*pageSize;
    }
}
