package com.tigerobo.x.pai.dal.biz.mapper.bill;

import com.tigerobo.x.pai.api.vo.biz.bill.UserBillReq;
import com.tigerobo.x.pai.dal.biz.entity.bill.BillDayUserCallPo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@org.apache.ibatis.annotations.Mapper
public interface BillQueryMapper {
    List<BillDayUserCallPo> getUserSumList(@Param("req")UserBillReq req);
}
