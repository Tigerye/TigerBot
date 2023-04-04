package com.tigerobo.x.pai.biz.notify;

import com.tigerobo.x.pai.api.ai.enums.AiArtImageProcessEnum;
import com.tigerobo.x.pai.biz.base.EnvService;
import com.tigerobo.x.pai.biz.cache.RedisCacheService;
import com.tigerobo.x.pai.dal.ai.dao.AiArtImageDao;
import com.tigerobo.x.pai.dal.ai.entity.AiArtImagePo;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class ArtImageNotifier {

    @Autowired
    private AiArtImageDao aiArtImageDao;

    @Autowired
    private RedisCacheService redisCacheService;

    @Autowired
    private EnvService envService;

    public synchronized void waitAuditNotifier() {
        if (!envService.isProd()) {
            return;
        }
        String key = "ding:artImage:audit";
        final String v = redisCacheService.get(key);

        if (v!=null){
            return;
        }

        DingTalkNotify.notice("艺术图待审核+1");
        redisCacheService.set(key,"1",30);
    }

    public synchronized void blockNotifier(){
        if (!envService.isProd()){
            return;
        }

        String key = "dingding:artImage:block";
        final Long preTime = redisCacheService.getLong(key);

        if (preTime!=null){
            final long current = System.currentTimeMillis();

            if ((current-preTime)>3600_1000){
                return;
            }
        }


        Date now= new Date();

        final Date point = DateUtils.addMinutes(now, -15);

        final List<AiArtImagePo> dealListTest = aiArtImageDao.getDealList(AiArtImageProcessEnum.ON_PROCESS.getStatus()
                ,20);

        Long maxMinute = 0L;
        String workPath = null;
        String req = null;
        for (AiArtImagePo aiArtImagePo : dealListTest) {
            final Integer progress = aiArtImagePo.getProgress();
            if (progress==80){
                continue;
            }

            final Date reqTime = aiArtImagePo.getReqTime();
            if (reqTime == null){
                continue;
            }
            if (reqTime.before(point)){

                final long delta = now.getTime() - reqTime.getTime();

                final long deltaMinute = delta / 1000 / 60;
                maxMinute = Math.max(deltaMinute,maxMinute);
                workPath = aiArtImagePo.getWorkPath();
                req = aiArtImagePo.getInputParam();
            }
        }


        if (maxMinute>0){
            String message ="algolet-艺术图存在阻塞超过";

            if (maxMinute>60){
                int hour =(int)(maxMinute/60);
                message+= hour+"小时";
            }else {
                message += maxMinute+"分钟";
            }
            message  += "workPath="+workPath;

            DingTalkNotify.noticeAlgolet(message);
            redisCacheService.set(key,System.currentTimeMillis()+"",3600);
        }
    }

}
