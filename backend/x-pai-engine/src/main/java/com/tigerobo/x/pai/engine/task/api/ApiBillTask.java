package com.tigerobo.x.pai.engine.task.api;

import com.tigerobo.x.pai.biz.pay.api.manage.ApiBillManage;
import com.tigerobo.x.pai.biz.pay.bill.ApiBillService;
import com.tigerobo.x.pai.biz.pay.payment.PaymentBillService;
import com.tigerobo.x.pai.biz.utils.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class ApiBillTask {


    @Autowired
    private PaymentBillService paymentBillService;

    @Autowired
    private ApiBillService apiBillService;
    @Value("${task.support.win:false}")
    boolean supportWin;

    @Value("${engine.task.switch:true}")
    boolean taskSwitch;

    @Value("${pay.bill.task.switch:true}")
    boolean paySwitch;

    @Autowired
    private ApiBillManage apiBillManage;

    @Scheduled(cron = "0 5/15 1-23 * * ?")
    public void refreshApiBillToday() {

        if (!taskSwitch) {
            return;
        }
        if (!paySwitch){
            return;
        }
        if (!supportWin) {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("windows")) {
                return;
            }
        }
        try {
            final int day = TimeUtil.getDayValue(new Date());
            apiBillManage.billDetailTask(day);
        } catch (Exception ex) {
            log.error("refreshApiBillToday", ex);
        }
    }

    @Scheduled(cron = "0 35 0 * * ?")
    public void refreshYesterdayApiBill() {

        if (!taskSwitch) {
            return;
        }
        if (!paySwitch){
            return;
        }
        if (!supportWin) {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("windows")) {
                return;
            }
        }

        try {
            final int preDay = TimeUtil.getDayValue(DateUtils.addDays(new Date(), -1));
            apiBillManage.billDetailTask(preDay);

            //todo 如果刷新最后一天，重新生成订单

            apiBillManage.paymentDetailBillSettlement(preDay);
        } catch (Exception ex) {
            log.error("refreshApiBillToday", ex);
        }
    }
}
