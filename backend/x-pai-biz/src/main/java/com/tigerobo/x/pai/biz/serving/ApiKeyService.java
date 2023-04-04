package com.tigerobo.x.pai.biz.serving;

import com.tigerobo.x.pai.dal.biz.dao.ApiDao;
import com.tigerobo.x.pai.dal.biz.dao.TaskDao;
import com.tigerobo.x.pai.dal.biz.entity.TaskDo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class ApiKeyService {
    @Autowired
    private TaskDao taskDao;
    Map<String, String> shortNameKeyMap = new ConcurrentHashMap<>();

    public static final String ART_IMAGE_APP_NAME = "art_image";
    public String getArtImageApiKey(){
        String shortName = ART_IMAGE_APP_NAME;
        final String apiKey = shortNameKeyMap.get(shortName);
        if (apiKey!=null){
            return apiKey;
        }
        return getApiKeyByShortName(shortName);
    }

    public synchronized String getApiKeyByShortName(String appShortName) {

        String apiKey = shortNameKeyMap.get(appShortName);
        if (!StringUtils.isEmpty(apiKey)){
            return apiKey;
        }
        final TaskDo task = taskDao.getByAppName(appShortName);
        if (task!=null){
            apiKey = task.getModelUuid();
            shortNameKeyMap.put(apiKey,apiKey);
            return apiKey;
        }
        return null;
    }
}
