package com.tigerobo.x.pai.dal.pay.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Table(name = "order_info")
public class OrderPo {

    @Id
    @KeySql(useGeneratedKeys = true)
    Integer id;

    Long orderNo;
    Integer userId;
    Integer productId;
    Integer skuId;
    String name;

    Integer amount;
    BigDecimal totalPrice;
    Integer status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date payTime;
    Integer payChannel;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;

    Boolean isDeleted;

    String paymentNo;
}
