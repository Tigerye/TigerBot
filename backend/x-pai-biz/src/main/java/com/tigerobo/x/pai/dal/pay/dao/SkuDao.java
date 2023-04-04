package com.tigerobo.x.pai.dal.pay.dao;

import com.tigerobo.x.pai.dal.pay.entity.ProductSkuPo;
import com.tigerobo.x.pai.dal.pay.mapper.ProductSkuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Component
public class SkuDao {

    @Autowired
    private ProductSkuMapper productSkuMapper;

    public List<ProductSkuPo> getMemberProducts(){

        return getProductsByType(1);
    }


    public List<ProductSkuPo> getProductsByType(Integer productType){

        Example example = new Example(ProductSkuPo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("isDeleted",0);
        criteria.andEqualTo("productId",productType);

        return productSkuMapper.selectByExample(example);
    }



    public ProductSkuPo load(Integer id){
        if (id== null){
            return null;
        }

        ProductSkuPo productSkuPo = productSkuMapper.selectByPrimaryKey(id);

        if (productSkuPo == null||productSkuPo.getIsDeleted()==null||productSkuPo.getIsDeleted()){
            return null;
        }
        return productSkuPo;
    }
}
