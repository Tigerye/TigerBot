package com.tigerobo.x.pai.dal.biz.entity.bill;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "`bill_day_user_call`")
public class BillDayUserCallPo {
    @Id
    @KeySql(useGeneratedKeys = true)
    Integer id;

    Integer day;
    Integer userId;
    Integer num;
}
