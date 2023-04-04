package com.tigerobo.x.pai.biz.ai.art.image;

import com.tigerobo.x.pai.api.ai.enums.AiArtImageProcessEnum;
import com.tigerobo.x.pai.biz.cache.RedisCacheService;
import com.tigerobo.x.pai.biz.utils.TimeUtil;
import com.tigerobo.x.pai.dal.ai.dao.AiArtImageDao;
import com.tigerobo.x.pai.dal.ai.entity.AiArtImagePo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;
@Slf4j
@Service
public class ArtImageAssignService {

    @Autowired
    private AiArtImageDao aiArtImageDao;

    @Autowired
    private RedisCacheService redisCacheService;

    @Value("${pai.ai.art.capacity:9}")
    Integer capacity;
    @Value("${pai.ai.art.userLimit:3}")
    final int userLimit = 3;

    @Value("${pai.gpt.image.userIds:1856}")
    private String chatUserIds;

    /**
     * 1,今日用户第一张图优先
     * 2,限制用户处理中的个数,userLimit,3
     * 3,限制处理中的总数capacity
     * @param test
     * @return
     */
    public List<AiArtImagePo> getWaitTaskList(boolean test,boolean fromChat){
        if (test){
            return aiArtImageDao.getDealListTest(1);
        }

        final Integer status = AiArtImageProcessEnum.ON_PROCESS.getStatus();
        final List<AiArtImagePo> dealList = aiArtImageDao.getDealList(status, capacity);
        int dealingCount = dealList==null?0:dealList.size();
        if (dealingCount>=capacity){
            return null;
        }

        if (fromChat){
            return getChatAiArtImagePos();
        }


        Map<Integer, Integer> userCountMap = getUserCountMap(dealList);
        int remainCount = (capacity>2?capacity-2:capacity) - dealingCount;
        if (remainCount<=0){
            return null;
        }
        int pageSize = 100;
        List<AiArtImagePo> waitDealList = aiArtImageDao.getDealList(AiArtImageProcessEnum.PREPARE.getStatus(),pageSize);
        if (CollectionUtils.isEmpty(waitDealList)) {
            return null;
        }
        List<AiArtImagePo> targetList = new ArrayList<>();

        Set<Integer> firstUserIds = new HashSet<>();

        final int dayValue = TimeUtil.getDayValue(new Date());
        for (AiArtImagePo aiArtImagePo : waitDealList) {
            final Integer userId = aiArtImagePo.getUserId();
            if (firstUserIds.contains(userId)){
                continue;
            }
            final String key = getKey(dayValue,userId);

            final Integer count = redisCacheService.getInteger(key);
            if (count==null||count==0){
                firstUserIds.add(userId);
                redisCacheService.set(key,"1",24*3600);
                targetList.add(aiArtImagePo);
                if (targetList.size()>=remainCount){
                    break;
                }
            }
        }


        for (AiArtImagePo aiArtImagePo : waitDealList) {
            if (targetList.contains(aiArtImagePo)){
                continue;
            }

            final Integer userId = aiArtImagePo.getUserId();
            Integer existCount = userCountMap.get(userId);
            if (existCount!=null&&existCount>0&&existCount>=userLimit){
                continue;
            }
            if (existCount == null){
                existCount = 1;
            }else {
                existCount++;
            }
            userCountMap.put(userId,existCount);
            targetList.add(aiArtImagePo);
            if (targetList.size()>=remainCount){
                break;
            }
        }


        return targetList;
    }

    private List<AiArtImagePo> getChatAiArtImagePos() {
        final Integer status = AiArtImageProcessEnum.ON_PROCESS.getStatus();
        String chatUserIds = this.chatUserIds;
        if (StringUtils.isEmpty(chatUserIds)){
            return null;
        }
        try {
            final List<Integer> checkUserIds = Arrays.stream(chatUserIds.split(","))
                    .filter(org.apache.commons.lang3.StringUtils::isNotBlank)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(checkUserIds)) {
                return null;
            }
            return aiArtImageDao.getUserDealList(checkUserIds, status, capacity);
        }catch (Exception ex){
            log.error("fromChat",ex);
            return null;
        }
    }

    private Map<Integer, Integer> getUserCountMap(List<AiArtImagePo> dealList) {
        Map<Integer,Integer> userCountMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(dealList)){
            for (AiArtImagePo aiArtImagePo : dealList) {
                final Integer userId = aiArtImagePo.getUserId();
                Integer count = userCountMap.get(userId);
                if (count == null){
                    count=0;
                }
                userCountMap.put(userId,++count);
            }
        }
        return userCountMap;
    }

    private String getKey(int day,Integer userId){

        if (userId == null){
            return null;
        }
        return "pai:art:user:dc:"+day+":"+userId;
    }
}
