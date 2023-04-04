package com.tigerobo.x.pai.biz.eye;

import com.google.common.base.Preconditions;
import com.tigerobo.x.pai.api.enums.BlogSourceFromEnum;
import com.tigerobo.x.pai.api.enums.EyeDailyTypeEnum;
import com.tigerobo.x.pai.api.enums.MetricCountEnum;
import com.tigerobo.x.pai.api.eye.dto.EyeDayMetricDto;
import com.tigerobo.x.pai.api.eye.dto.EyeMetricCountDto;
import com.tigerobo.x.pai.api.eye.req.DailyMetricReq;
import com.tigerobo.x.pai.biz.utils.TimeUtil;
import com.tigerobo.x.pai.dal.auth.dao.UserDao;
import com.tigerobo.x.pai.dal.biz.dao.blog.BlogSearchDao;
import com.tigerobo.x.pai.dal.biz.dao.eye.EyeDailyCountDao;
import com.tigerobo.x.pai.dal.biz.entity.eye.EyeDailyCountPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Slf4j
@Service
public class EyeMetricService {

    @Autowired
    private EyeDailyCountDao eyeDailyCountDao;

//    @Autowired
//    private BlogInfoDao blogInfoDao;

    @Autowired
    private BlogSearchDao blogSearchDao;
    @Autowired
    private UserDao userDao;

    public EyeMetricCountDto getMetricCount(Integer type){
        EyeMetricCountDto countResultDto = new EyeMetricCountDto();
        countResultDto.setType(type);
        countResultDto.setTypeName("");

        MetricCountEnum typeEnum = MetricCountEnum.getByType(type);
        if (typeEnum == null){
            return countResultDto;
        }
        countResultDto.setTypeName(typeEnum.getText());
        int count = 0;

        switch (typeEnum){
            case BLOG:count = blogSearchDao.countByType(null);break;
            case BLOG_ALGOLET:count = blogSearchDao.countByType(BlogSourceFromEnum.ALGOLET.getType());break;
            case BLOG_SITE:count = blogSearchDao.countByType(BlogSourceFromEnum.SITE.getType());break;
            case BLOG_BIGSHOT:count = blogSearchDao.countByType(BlogSourceFromEnum.BIG_SHOT.getType());break;
            case USER:count = userDao.count();
        }

        countResultDto.setNum(count);

        return countResultDto;
    }



    public List<EyeDayMetricDto> getDailyMetricList(DailyMetricReq req){

        Integer metricType = req.getType();

        EyeDailyTypeEnum typeEnum = EyeDailyTypeEnum.getByType(metricType);

        Preconditions.checkArgument(typeEnum!=null);


        Date startDate = req.getStartDate();
        Date today = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
        if (startDate == null) {
            startDate = DateUtils.addDays(today, -5);
        }

        Date endDate = req.getEndDate();
        if (endDate == null || endDate.after(today)) {
            endDate = today;
        }
        int startDay = Integer.parseInt(DateFormatUtils.format(startDate, "yyyyMMdd"));

        int endDay = Integer.parseInt(DateFormatUtils.format(endDate, "yyyyMMdd"));
        req.setStartDay(startDay);
        req.setEndDay(endDay);

        List<EyeDailyCountPo> list = eyeDailyCountDao.getList(startDay, endDay, typeEnum.getType());

        Map<Integer,EyeDailyCountPo> map = new HashMap<>();

        if (!CollectionUtils.isEmpty(list)){
            for (EyeDailyCountPo eyeDailyCountPo : list) {
                map.put(eyeDailyCountPo.getDay(),eyeDailyCountPo);
            }
        }

        List<EyeDayMetricDto> targetList = new ArrayList<>();
        Date cursor = startDate;

        while (!cursor.after(endDate)){
            int dayValue = TimeUtil.getDayValue(cursor);

            EyeDailyCountPo po = map.get(dayValue);
            int num = po == null?0:po.getNum();

            String dayFormat = TimeUtil.dayFormat(dayValue);

            EyeDayMetricDto metricDto = new EyeDayMetricDto();
            metricDto.setDay(dayValue);
            metricDto.setDate(dayFormat);

            metricDto.setTypeName(typeEnum.getText());
            metricDto.setType(typeEnum.getType());
            metricDto.setNum(num);

            targetList.add(metricDto);

            cursor = DateUtils.addDays(cursor,1);
        }
        return targetList;
    }

}
