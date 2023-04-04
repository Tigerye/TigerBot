package com.tigerobo.x.pai.dal.ai.entity;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "ai_style_transfer_dic")
public class AiStyleTransferDicPo {
    @Id
//    @KeySql(useGeneratedKeys = true)
    Integer id;
    String name;
    String styleImage;
    Boolean isDeleted;
}
