package com.tigerobo.x.pai.api.admin.req;

import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.biz.PageReqVo;
import lombok.Data;
import org.ehcache.shadow.org.terracotta.offheapstore.paging.Page;

import java.util.List;

@Data
public class IdListReq extends PageReqVo {
    List<Integer> idList;
    String keyword;
}
