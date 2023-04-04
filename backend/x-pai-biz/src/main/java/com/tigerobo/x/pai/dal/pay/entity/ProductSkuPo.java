package com.tigerobo.x.pai.dal.pay.entity;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Table(name = "product_sku")
@Data
public class ProductSkuPo {

    @Id
    @KeySql(useGeneratedKeys = true)
    private Integer id;

    Integer productId;
    String name;
    String showName;
    @Column(name = "`desc`")
    String desc;
    BigDecimal price;
    BigDecimal originPrice;
    Integer days;
    Integer level;
    Integer status;
    Boolean isDeleted;

    String properties;
    String itemUnit;
    Integer priceType;
}
