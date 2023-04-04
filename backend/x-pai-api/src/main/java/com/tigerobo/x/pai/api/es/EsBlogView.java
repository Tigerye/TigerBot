package com.tigerobo.x.pai.api.es;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;


@Data
public class EsBlogView {
    String id;
    Integer day;
    Integer blogId;
    Integer userId;
    String ip;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;
}
