package com.tigerobo.x.pai.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class MemberDto {
    Boolean isMember;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    Date expireDate;

    Integer level;
    String levelName;
}
