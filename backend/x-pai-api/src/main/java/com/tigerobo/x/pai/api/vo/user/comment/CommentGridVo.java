package com.tigerobo.x.pai.api.vo.user.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tigerobo.x.pai.api.enums.BusinessEnum;
import com.tigerobo.x.pai.api.vo.IBusinessDetailVo;
import com.tigerobo.x.pai.api.vo.user.UserBriefVo;
import lombok.Data;

import java.util.Date;

@Data
public class CommentGridVo{


    Integer id;
    String content;
    UserBriefVo user;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;
    public Integer getUserId(){
        if (user == null){
            return null;
        }
        return user.getId();
    };
}