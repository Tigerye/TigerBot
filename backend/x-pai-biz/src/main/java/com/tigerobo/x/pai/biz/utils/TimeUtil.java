package com.tigerobo.x.pai.biz.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTime;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
@Slf4j
public class TimeUtil {

    public static int getDayValue(Date time){
        return Integer.parseInt(DateFormatUtils.format(time,"yyyyMMdd"));
    }

    public static int getMonthValue(Date time){
        return Integer.parseInt(DateFormatUtils.format(time,"yyyyMM"));
    }

    public static int getMonthValue(int day){
        return day/100;
    }

    public static int getStartDay(int day){
        final int monthValue = getMonthValue(day);
        return monthValue*100+1;
    }

    public static String dayFormat(int day) {

        int year = day / 10000;

        int month = day % 10000 / 100;

        int dayVal = day % 10000 % 100;

        return year + "-" + month + "-" + dayVal;
    }


    public static Date convertDay(int day){
        try {
            return DateUtils.parseDate(String.valueOf(day), "yyyyMMdd");
        } catch (ParseException e) {
            log.error("day:{}",day,e);
        }
        return null;
    }


    public static int getMonthFirstDay(int month){
        return month*100+1;
    }

    public static int getMonthLastDay(int month){


        Date date = null;
        try {
            date = DateUtils.parseDate(String.valueOf(month), "yyyyMM");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        final Date endDay = DateUtils.addDays(DateUtils.addMonths(date, 1), -1);

        return getDayValue(endDay);
    }
    public static int getTodayRemainSecond(){
        long secondsLeftToday = 86400 - DateUtils.getFragmentInSeconds(Calendar.getInstance(), Calendar.DATE);

        return (int)secondsLeftToday;
    }




}
