package com.tigerobo.x.pai.biz.basket;

import com.alibaba.fastjson.JSON;
import com.tigerobo.x.pai.api.basket.CorrectWordBasketReq;
import com.tigerobo.x.pai.api.basket.CorrectWordDto;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.dal.basket.dao.CorrectWordDao;
import com.tigerobo.x.pai.dal.basket.entity.CorrectWordPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CorrectWordService {

    @Autowired
    private CorrectWordDao correctWordDao;


    public void add(CorrectWordBasketReq req,String key){

        List<CorrectWordDto> list = req.getList();
        String ip = ThreadLocalHolder.getIp();

        for (CorrectWordDto wordDto : list) {
            CorrectWordPo correctWordPo = dto2po(wordDto, key, ip);
            if (correctWordPo == null){
                log.error("参数不正确 add:{}", JSON.toJSONString(wordDto));
            }
            correctWordDao.add(correctWordPo);
        }
    }



    private CorrectWordPo dto2po(CorrectWordDto dto,String key,String ip){

        if (dto == null){
            return null;
        }
        if (StringUtils.isBlank(dto.getOriginTitle())&&StringUtils.isBlank(dto.getCorrectTitle())){
            return null;
        }

        CorrectWordPo po = new CorrectWordPo();

        po.setCorrectTitle(dto.getCorrectTitle());
        po.setOriginTitle(dto.getOriginTitle());
        po.setKey(key);
        po.setIp(ip);

        return po;
    }
}
