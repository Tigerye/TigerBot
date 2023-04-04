package com.tigerobo.x.pai.dal.biz.mapper;

import com.tigerobo.x.pai.api.eye.req.ModelBoardReq;
import com.tigerobo.x.pai.dal.biz.entity.eye.CallModelDayPo;
import com.tigerobo.x.pai.dal.biz.entity.eye.CallModelGuestPo;
import com.tigerobo.x.pai.dal.biz.entity.eye.CallModelNamePo;
import com.tigerobo.x.pai.dal.biz.entity.eye.CallModelUserPo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@org.apache.ibatis.annotations.Mapper
public interface ModelQueryMapper {
    List<CallModelDayPo> getSourceCallByDay(@Param("req") ModelBoardReq req);
    List<CallModelNamePo> getSourceCallByModelName(@Param("req") ModelBoardReq req);
    List<CallModelUserPo> getSourceModelCallByUser(@Param("req") ModelBoardReq req);

}
