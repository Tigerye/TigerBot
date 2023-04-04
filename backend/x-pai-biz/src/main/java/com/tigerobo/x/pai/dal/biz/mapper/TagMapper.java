package com.tigerobo.x.pai.dal.biz.mapper;

import com.tigerobo.x.pai.dal.biz.entity.TagPo;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertListMapper;

@org.apache.ibatis.annotations.Mapper
public interface TagMapper extends Mapper<TagPo>, InsertListMapper<TagPo> {
}
