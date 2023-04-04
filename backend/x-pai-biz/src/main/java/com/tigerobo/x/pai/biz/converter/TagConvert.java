package com.tigerobo.x.pai.biz.converter;

import com.tigerobo.x.pai.api.biz.entity.Tag;
import com.tigerobo.x.pai.dal.biz.entity.TagPo;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

public class TagConvert {

    public static List<TagPo> convertList(List<Tag> dtos){
        if (CollectionUtils.isEmpty(dtos)){
            return null;
        }
        return dtos.stream().map(TagConvert::convert).collect(Collectors.toList());
    }
    public static TagPo convert(Tag dto){
        if (dto == null){
            return null;
        }
        TagPo po = new TagPo();
        po.setUid(dto.getUid());
        po.setText(dto.getText());
        po.setTextEn(dto.getTextEn());
        po.setDesc(dto.getDesc());
        po.setIcon(dto.getIcon());
        Tag.Type type = dto.getType();
        if (type!=null){
            po.setType(type.getVal());
        }
        return po;
    }
    public static List<Tag> convert2dtoList(List<TagPo> pos){
        if (CollectionUtils.isEmpty(pos)){
            return null;
        }
        return pos.stream().map(TagConvert::convert).collect(Collectors.toList());
    }
    public static Tag convert(TagPo po){
        if (po == null){
            return null;
        }
        Tag dto = new Tag();
        dto.setUid(po.getUid());
        dto.setText(po.getText());
        dto.setTextEn(po.getTextEn());
        dto.setDesc(po.getDesc());
        dto.setIcon(po.getIcon());
        Tag.Type type = Tag.Type.valueOf(po.getType());
        dto.setType(type);
        return dto;
    }
}
