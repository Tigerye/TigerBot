package com.tigerobo.x.pai.api.dto.admin.github.req;

import com.tigerobo.x.pai.api.vo.biz.PageReqVo;
import lombok.Data;

@Data
public class GithubUserAdminReq extends PageReqVo {
    String keyword;
    Integer userId;
    Boolean isDeleted;
}
