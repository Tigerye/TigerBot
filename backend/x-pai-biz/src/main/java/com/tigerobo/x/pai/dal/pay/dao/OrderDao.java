package com.tigerobo.x.pai.dal.pay.dao;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.tigerobo.x.pai.api.pay.enums.OrderStatusEnum;
import com.tigerobo.x.pai.api.pay.req.UserPayOrderQueryReq;
import com.tigerobo.x.pai.dal.pay.entity.OrderPo;
import com.tigerobo.x.pai.dal.pay.mapper.OrderMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Component
public class OrderDao {

    @Autowired
    private OrderMapper orderMapper;



    public Page<OrderPo> getUserPayUserList(Integer userId, UserPayOrderQueryReq req){
        Example example = new Example(OrderPo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("status", OrderStatusEnum.HAS_PAY.getStatus());

        if (req.getStartPayTime()!=null){
            criteria.andGreaterThanOrEqualTo("payTime",req.getStartPayTime());
        }

        if (req.getEndPayTime()!=null){
            criteria.andLessThan("payTime",DateUtils.addDays(req.getEndPayTime(),1));
        }

        if (StringUtils.isNotBlank(req.getKeyword())){
            criteria.andLike("name","%"+req.getKeyword()+"%");
        }

        PageHelper.startPage(req.getPageNum(),req.getPageSize());

        example.setOrderByClause("id desc");
        Page<OrderPo> orderPos = (Page<OrderPo>)orderMapper.selectByExample(example);
        return orderPos;
    }

    public int updateByStatusCondition(OrderPo updateInfo,Long orderNo,Integer preStatus){

        Example example = new Example(OrderPo.class);

        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("orderNo",orderNo);
        criteria.andEqualTo("status",preStatus);
        return orderMapper.updateByExampleSelective(updateInfo,example);
    }

    public OrderPo load(Long orderNo){
        if (orderNo == null){
            return null;
        }

        Example example = new Example(OrderPo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("orderNo",orderNo);

        List<OrderPo> orderPos = orderMapper.selectByExample(example);

        if (CollectionUtils.isEmpty(orderPos)){
            return null;
        }
        return orderPos.get(0);
    }

    public int add(OrderPo orderPo){
        return orderMapper.insertSelective(orderPo);
    }

}
