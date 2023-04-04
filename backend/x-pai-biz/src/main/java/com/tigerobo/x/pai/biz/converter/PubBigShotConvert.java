package com.tigerobo.x.pai.biz.converter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tigerobo.x.pai.api.dto.admin.BigShotDto;
import com.tigerobo.x.pai.api.dto.admin.PubSiteDto;
import com.tigerobo.x.pai.api.enums.FollowTypeEnum;
import com.tigerobo.x.pai.api.vo.biz.pub.PubBigShotVo;
import com.tigerobo.x.pai.api.vo.user.follow.FollowVo;
import com.tigerobo.x.pai.dal.biz.entity.blog.BlogBigshotReplyPo;
import com.tigerobo.x.pai.dal.biz.entity.pub.PubBigShotPo;
import com.tigerobo.x.pai.dal.biz.entity.pub.PubSitePo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
@Slf4j
public class PubBigShotConvert {

    public static PubBigShotVo convert(PubBigShotPo po){

        if (po ==null){
            return null;
        }
        PubBigShotVo vo = new PubBigShotVo();

        vo.setId(po.getId());
        vo.setLogo(po.getLogo());
        vo.setName(po.getName());
        vo.setAlias(po.getAlias());
        vo.setIntro(po.getIntro());
        vo.setVip(po.getVip());
        return vo;
    }
    public static FollowVo convert2follow(PubBigShotPo po){

        if (po ==null){
            return null;
        }
        PubBigShotVo bigShotVo = convert(po);
        return convert2FollowVo(bigShotVo);
    }


    public static FollowVo convert2FollowVo(PubBigShotVo bigShotVo) {
        FollowVo followVo = new FollowVo();


        BeanUtils.copyProperties(bigShotVo,followVo);

        followVo.setLogoOss(bigShotVo.getLogo());

        followVo.setBizType(FollowTypeEnum.BIG_SHOT.getType());

        followVo.setPlatformName("twitter");

        return followVo;
    }

    public static List<PubBigShotVo> convert(List<PubBigShotPo> pos){
        if (CollectionUtils.isEmpty(pos)){
            return null;
        }
        return pos.stream().map(po->convert(po)).collect(Collectors.toList());
    }

    public static List<BlogBigshotReplyPo> convertReply(String json, String currentBlogSpecId, String chatId){
        if (StringUtils.isEmpty(json)||StringUtils.isEmpty(currentBlogSpecId)){
            return null;
        }

        List<BlogBigshotReplyPo> replyPos = new ArrayList<>();
        try {
            JSONArray list = JSON.parseArray(json);

            for (int i = 0; i < list.size(); i++) {

                JSONObject jsonObject = list.getJSONObject(i);
                String spec_id = jsonObject.getString("spec_id");
                String author = jsonObject.getString("author");
                String publish_time = jsonObject.getString("publish_time");
                String content = jsonObject.getString("content");
                if (org.apache.commons.lang3.StringUtils.isAnyEmpty(spec_id, author, publish_time, content)) {
                    continue;
                }

                Date date = DateUtils.parseDate(publish_time, "yyyy-MM-dd HH:mm:ss");

                BlogBigshotReplyPo po = new BlogBigshotReplyPo();
                po.setPublishTime(date);
                po.setAuthor(author);
                po.setSpecId(spec_id);
                po.setContent(content);
                po.setSeq(i);
                boolean cur = currentBlogSpecId.equals(spec_id);
                po.setCurrentBlog(cur);

                po.setChatId(chatId);
                replyPos.add(po);
            }
        }catch (Exception ex){
            log.error("speId:{}",currentBlogSpecId,ex);
            return null;
        }
        return replyPos;
    }

    public static BigShotDto po2dto(PubBigShotPo po){
        BigShotDto dto=new BigShotDto();
        BeanUtils.copyProperties(po,dto);
        return dto;
    }
}
