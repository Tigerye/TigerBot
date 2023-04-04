package com.tigerobo.x.pai.api.dto.admin.github.req;

import com.tigerobo.x.pai.api.vo.biz.PageReqVo;
import lombok.Data;

@Data
public class RepoTaskAdminReq extends PageReqVo {
    String keyword;
    Integer status;
}
