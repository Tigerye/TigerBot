package com.tigerobo.x.pai.biz.converter;

import com.tigerobo.x.pai.api.vo.biz.blog.BlogChatVo;
import com.tigerobo.x.pai.dal.biz.entity.blog.BlogBigshotReplyPo;
import com.tigerobo.x.pai.dal.biz.entity.blog.BlogChatPo;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

public class BlogChatConvert {

    public static List<BlogChatVo> reply2chatVo(List<BlogBigshotReplyPo> pos){
        if (CollectionUtils.isEmpty(pos)){
            return null;
        }
        return pos.stream().map(p->reply2chatVo(p)).collect(Collectors.toList());
    }

    public static BlogChatVo reply2chatVo(BlogBigshotReplyPo po){
        if (po == null){
            return null;
        }

        BlogChatVo vo = new BlogChatVo();
        BeanUtils.copyProperties(po,vo);
        return vo;
    }
    public static List<BlogChatPo> reply2chatPo(List<BlogBigshotReplyPo> pos){
        if (CollectionUtils.isEmpty(pos)){
            return null;
        }
        return pos.stream().map(p->reply2chatPo(p)).collect(Collectors.toList());
    }

    public static BlogChatPo reply2chatPo(BlogBigshotReplyPo po){
        if (po == null){
            return null;
        }

        BlogChatPo vo = new BlogChatPo();
        BeanUtils.copyProperties(po,vo);
        return vo;
    }

    public static List<BlogChatVo> po2vo(List<BlogChatPo> pos){
        if (CollectionUtils.isEmpty(pos)){
            return null;
        }
        return pos.stream().map(p->po2vo(p)).collect(Collectors.toList());
    }

    public static BlogChatVo po2vo(BlogChatPo po){
        if (po == null){
            return null;
        }

        BlogChatVo vo = new BlogChatVo();
        BeanUtils.copyProperties(po,vo);
        return vo;
    }


}
