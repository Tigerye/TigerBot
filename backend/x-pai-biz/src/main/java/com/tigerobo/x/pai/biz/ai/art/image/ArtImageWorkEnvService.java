package com.tigerobo.x.pai.biz.ai.art.image;

import com.tigerobo.x.pai.biz.utils.TimeUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class ArtImageWorkEnvService {
    @Value("${pai.env.aml.prefix}")
    String prefix;

    @Value("${pai.sensitive.switch:true}")
    boolean checkSensitive;
    @Value("${pai.ai.artImage.progress:100}")
    Integer totalProgress;

    private static String workPath = "/mnt/xpai/application/";
    String getWorkPath(Long reqId) {
        int day = TimeUtil.getDayValue(new Date());

        String itemWorkPath = workPath;
        if (!StringUtils.isBlank(prefix)) {
            itemWorkPath += prefix + "/";
        }
        itemWorkPath += day + "/" + reqId + "/";
        return itemWorkPath;
    }
}
