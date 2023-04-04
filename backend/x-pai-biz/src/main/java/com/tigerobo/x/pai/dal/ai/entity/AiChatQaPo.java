package com.tigerobo.x.pai.dal.ai.entity;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "ai_chat_qa")
@Data
public class AiChatQaPo {

    @Id
    @KeySql(useGeneratedKeys = true)
    private Integer id;


    String question;
    Integer userId;

    String clientId;

}
