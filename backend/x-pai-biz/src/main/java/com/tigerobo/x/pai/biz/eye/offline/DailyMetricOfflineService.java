package com.tigerobo.x.pai.biz.eye.offline;

import com.tigerobo.x.pai.api.enums.EyeDailyTypeEnum;
import com.tigerobo.x.pai.biz.data.es.EsUserAccessService;
import com.tigerobo.x.pai.biz.utils.TimeUtil;
import com.tigerobo.x.pai.dal.auth.dao.UserDao;
import com.tigerobo.x.pai.dal.biz.dao.blog.BlogSearchDao;
import com.tigerobo.x.pai.dal.biz.dao.eye.EyeDailyCountDao;
import com.tigerobo.x.pai.dal.biz.entity.eye.EyeDailyCountPo;
import com.tigerobo.x.pai.dal.biz.mapper.ModelQueryMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Slf4j
@Service
public class DailyMetricOfflineService {

//    @Autowired
//    private BlogInfoDao blogInfoDao;
    @Autowired
    private BlogSearchDao blogSearchDao;

    @Autowired
    private EyeDailyCountDao eyeDailyCountDao;

    @Autowired
    private UserDao userDao;
    @Autowired
    private ModelQueryMapper modelQueryMapper;

    @Autowired
    private EsUserAccessService esUserAccessService;

    public void addDayMetrics(Date day){

        Date start = DateUtils.truncate(day, Calendar.DAY_OF_MONTH);
        Date end = DateUtils.addDays(start, 1);
        int dayValue = TimeUtil.getDayValue(day);
        List<EyeDailyCountPo> dayList = eyeDailyCountDao.getDayList(dayValue, null);
        Map<Integer,EyeDailyCountPo> typeMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(dayList)){
            for (EyeDailyCountPo po : dayList) {
                typeMap.put(po.getType(),po);
            }
        }

        addTypeCount(start, end, dayValue, typeMap, EyeDailyTypeEnum.BLOG_INCR);
        addTypeCount(start, end, dayValue, typeMap, EyeDailyTypeEnum.BLOG_ALGOLET_INCR);
        addTypeCount(start, end, dayValue, typeMap, EyeDailyTypeEnum.BLOG_SITE_INCR);
        addTypeCount(start, end, dayValue, typeMap, EyeDailyTypeEnum.BLOG_BIGSHOT_INCR);
        addTypeCount(start, end, dayValue, typeMap, EyeDailyTypeEnum.USER_INCR);
        addTypeCount(start, end, dayValue, typeMap, EyeDailyTypeEnum.USER_ACTIVE);
    }

    private void addTypeCount(Date start, Date end, int dayValue,
                              Map<Integer, EyeDailyCountPo> typeMap,
                              EyeDailyTypeEnum dailyTypeEnum) {

        Integer type = dailyTypeEnum.getType();
        Integer count = 0;
        switch (dailyTypeEnum){
            case BLOG_INCR:count = blogSearchDao.count(start, end, null);break;
            case BLOG_ALGOLET_INCR: count = blogSearchDao.count(start, end, 0);break;
            case BLOG_SITE_INCR: count = blogSearchDao.count(start, end, 1);break;
            case BLOG_BIGSHOT_INCR: count = blogSearchDao.count(start, end, 2);break;
            case USER_INCR:count = userDao.count(start,end);break;
            case USER_ACTIVE:
            {
//                count=modelQueryMapper.countIpCall(dayValue)+modelQueryMapper.countUserCall(dayValue);
                count = esUserAccessService.countDay(dayValue);
            }break;
        }
        if (count == null){
            log.error("获取每日数据异常,day:{}",dayValue);
        }

        EyeDailyCountPo incrPo = typeMap.get(type);
        if (incrPo == null){
            incrPo = new EyeDailyCountPo();
            if (count==null){
                count=0;
            }
            incrPo.setNum(count);
            incrPo.setType(type);
            incrPo.setDay(dayValue);
            eyeDailyCountDao.add(incrPo);
        }else {
            if (count!=null&&!incrPo.getNum().equals(count)){
                incrPo.setNum(count);
                eyeDailyCountDao.update(incrPo);
            }
        }
    }
}
