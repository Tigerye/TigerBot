package com.tigerobo.x.pai.biz.pay;

import com.github.pagehelper.Page;
import com.tigerobo.x.pai.api.pay.dto.OrderDto;
import com.tigerobo.x.pai.api.pay.req.UserPayOrderQueryReq;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.biz.pay.convert.OrderConvert;
import com.tigerobo.x.pai.dal.pay.dao.OrderDao;
import com.tigerobo.x.pai.dal.pay.entity.OrderPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class OrderQueryService {

    @Autowired
    private OrderDao orderDao;

    public PageVo<OrderDto> getUserPayOrderList(Integer userId, UserPayOrderQueryReq req){

        Validate.isTrue(userId!=null,"用户未登录");
        Page<OrderPo> userPayUserList = orderDao.getUserPayUserList(userId, req);

        List<OrderDto> orderDtos = OrderConvert.po2dtos(userPayUserList);
        long total = userPayUserList.getTotal();
        PageVo<OrderDto> pageVo = new PageVo<>();
        pageVo.setPageNum(req.getPageNum());
        pageVo.setPageSize(req.getPageSize());
        pageVo.setList(orderDtos);
        pageVo.setTotal(total);
        return pageVo;

    }

}
