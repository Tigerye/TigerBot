package com.tigerobo.x.pai.dal.biz.entity.eye;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "`eye_daily_count`")
@Data
public class EyeDailyCountPo {
    @Id
    @KeySql(useGeneratedKeys = true)
    Integer id;
    Integer day;
    /**
     *     @see com.tigerobo.x.pai.api.enums.EyeDailyTypeEnum
     */
    Integer type;
    Integer num;
}
