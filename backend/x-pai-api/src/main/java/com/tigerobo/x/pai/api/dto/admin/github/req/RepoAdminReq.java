package com.tigerobo.x.pai.api.dto.admin.github.req;

import com.tigerobo.x.pai.api.vo.biz.PageReqVo;
import lombok.Data;

@Data
public class RepoAdminReq extends PageReqVo {
    String keyword;
    String aiTag;
    Integer userId;
    Boolean isDeleted;
}
