package com.tigerobo.x.pai.biz.ai.gpt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.tigerobo.x.pai.api.ai.req.AiArtImageGenerateReq;
import com.tigerobo.x.pai.api.enums.ProcessStatusEnum;
import com.tigerobo.x.pai.biz.ai.art.image.ArtImageUserReqService;
import com.tigerobo.x.pai.dal.ai.dao.AiArtImageDao;
import com.tigerobo.x.pai.dal.ai.entity.AiArtImagePo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class ChatImageService {


    @Value("${pai.gpt.image.reqUserId:1856}")
    private Integer reqUserId;
    @Value("${pai.gpt.image.waitSecond:100}")
    private Integer waitSecond;

    @Value("${pai.gpt.image.regex:[\"画.{0,20}\"]}")
    private String matchImageText;
    @Autowired
    private ArtImageUserReqService artImageUserReqService;

    @Autowired
    private AiArtImageDao aiArtImageDao;

    public List<String> produceImages(String text){
        if (!isMatchImage(text)){
            return null;
        }
        final Integer id = add2aiImageTask(text);

        if (id == null){
            return null;
        }

        return waitImages(id);
    }

    public boolean isMatchImage(String text){
        if (StringUtils.isBlank(text)){
            return false;
        }
        if (StringUtils.isBlank(matchImageText)){
            return false;
        }
        final JSONArray array = JSON.parseArray(matchImageText);

        for (int i = 0; i < array.size(); i++) {
            final String img =  array.getString(i);
            if (text.matches(img)){
                return true;
            }

        }
        return false;
    }

    private List<String> waitImages(Integer id){
        final long start = System.currentTimeMillis();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            log.error("",e);
        }
        while (true){
            final AiArtImagePo po = aiArtImageDao.load(id);

            if (po == null){
                break;
            }
            final Integer status = po.getProcessStatus();
            if (ProcessStatusEnum.FAIL.getStatus().equals(status)){
                return null;
            }
            if (ProcessStatusEnum.SUCCESS.getStatus().equals(status)){

                final String iterImages = po.getIterImages();
                final JSONArray array = JSON.parseArray(iterImages);

                List<String> list = new ArrayList<>();
                for (int i = 0; i < array.size()&&i<3; i++) {
                    final String img = array.getString(i);
                    if (StringUtils.isNotBlank(img)){
                        list.add(img);
                    }
                }
                return list;
            }

            if (System.currentTimeMillis()-start>waitSecond*1000){
                break;
            }
            try {
                log.warn("wait id:{}，delta:{}s",id,(System.currentTimeMillis()-start)/1000);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error("",e);
            }
        }
        return null;
    }

    private Integer add2aiImageTask(String text){

        AiArtImageGenerateReq req = new AiArtImageGenerateReq();
        AiArtImageGenerateReq.ArtImageParams params = new AiArtImageGenerateReq.ArtImageParams();
        params.setText(text);
        req.setInputParam(Arrays.asList(params));
        req.setUserId(reqUserId);
        req.setPromptWeight(9f);
        req.setImageStrength(0.8f);
        req.setSteps(50);
        req.setStyleType("stable");
        req.setSizeId(168);
        req.setNIter(1);
        req.setCoinTotal(1);

        req.setSeed((int)(System.currentTimeMillis()/1000));
        req.setWidth(512);
        req.setHeight(512);

        req.setModelVersion("v2.1");
        Integer id = artImageUserReqService.innerReqProduceImage(req);


        final AiArtImagePo load = aiArtImageDao.load(id);

        return load.getId();
    }
}
