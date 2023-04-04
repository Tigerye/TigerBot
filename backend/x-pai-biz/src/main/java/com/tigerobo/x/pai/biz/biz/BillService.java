package com.tigerobo.x.pai.biz.biz;

import com.google.common.base.Preconditions;
import com.tigerobo.x.pai.api.vo.biz.bill.DayCallVo;
import com.tigerobo.x.pai.api.vo.biz.bill.UserBillReq;
import com.tigerobo.x.pai.api.vo.biz.bill.UserCallBillVo;
import com.tigerobo.x.pai.api.enums.ModelCallTypeEnum;
import com.tigerobo.x.pai.biz.auth.RoleService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.biz.utils.TimeUtil;
import com.tigerobo.x.pai.dal.aml.dao.AmlModelDao;
import com.tigerobo.x.pai.dal.aml.entity.AmlModelDo;
import com.tigerobo.x.pai.dal.biz.dao.ApiDao;
import com.tigerobo.x.pai.dal.biz.dao.TaskDao;
import com.tigerobo.x.pai.dal.biz.dao.call.ModelSourceCallDao;
import com.tigerobo.x.pai.dal.biz.entity.ApiDo;
import com.tigerobo.x.pai.dal.biz.entity.TaskDo;
import com.tigerobo.x.pai.dal.biz.entity.call.ModelSourceCallPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BillService {
    @Autowired
    private ModelSourceCallDao modelSourceCallDao;


    @Autowired
    private ApiDao apiDao;
    @Autowired
    private RoleService roleService;

    @Autowired
    private TaskDao taskDao;

    @Autowired
    private AmlModelDao amlModelDao;

    public UserCallBillVo getUserBill(UserBillReq req) {
        Preconditions.checkArgument(req.getUserId() != null, "未指定用户");

        Integer userId = req.getUserId();

        Integer loginUserId = ThreadLocalHolder.getUserId();
        if (!Objects.equals(userId, loginUserId)) {
            roleService.checkAdmin();
        }

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

        Integer callSourceType = req.getCallSource();

        List<ModelSourceCallPo> userDetailList = modelSourceCallDao.getUserDetailList(startDay, endDay, userId,callSourceType);
        long totalCall = userDetailList.stream().map(k -> (long) (k.getNum())).reduce(0L, Long::sum);


        Map<String, List<ModelSourceCallPo>> modelDetailMap = getModelDetailMap(userDetailList);

        List<UserCallBillVo.UserModelBillVo> modelBillList =
                modelDetailMap.values().stream().map(m -> buildModelBill(m, startDay, endDay)).filter(Objects::nonNull).collect(Collectors.toList());

        initTaskId(modelBillList);
        initAmlId(modelBillList);

        if (modelBillList.size()>1){
            modelBillList.sort(Comparator.comparing(UserCallBillVo.UserModelBillVo::getNum).reversed());
        }

        for (UserCallBillVo.UserModelBillVo userModelBillVo : modelBillList) {
            userModelBillVo.setCallSource(callSourceType);
        }
        UserCallBillVo billVo = new UserCallBillVo();
        billVo.setUserId(userId);
        billVo.setCallModelTotalNum(totalCall);
        billVo.setModelCallList(modelBillList);
        return billVo;
    }

    private void initTaskId(List<UserCallBillVo.UserModelBillVo> modelBillList){
        if (CollectionUtils.isEmpty(modelBillList)){
            return;
        }
        List<String> modelIds = modelBillList.stream().filter(m -> m.getType() == 1).map(m -> m.getModelId()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(modelIds)){
            return;
        }
        Map<String, TaskDo> modelTaskIdMap = getModelTaskIdMap(modelIds);
        if (CollectionUtils.isEmpty(modelTaskIdMap)){
            return;
        }

        for (UserCallBillVo.UserModelBillVo billVo : modelBillList) {
            Integer type = billVo.getType();
            if (ModelCallTypeEnum.APP.getType().equals(type)){
                String modelId = billVo.getModelId();
                TaskDo task = modelTaskIdMap.get(modelId);
                if (task == null){
                    task = taskDao.getByModelUuid(modelId);
                }
                if (task!=null){
                    billVo.setTaskId(task.getUuid());
                    billVo.setAppShortName(task.getAppShortName());
                }else {
                    billVo.setTaskId("");
                    billVo.setAppShortName("");
                }

            }
        }
    }


    private void initAmlId(List<UserCallBillVo.UserModelBillVo> modelBillList){
        if (CollectionUtils.isEmpty(modelBillList)){
            return;
        }
        List<Integer> modelIds = modelBillList.stream().filter(m -> ModelCallTypeEnum.AML.getType().equals(m.getType())&&m.getModelId().matches("\\d+")).map(m -> Integer.parseInt(m.getModelId())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(modelIds)){
            return;
        }
        Map<String, Integer> modelTaskIdMap = getModelAmlIdMap(modelIds);
        if (CollectionUtils.isEmpty(modelTaskIdMap)){
            return;
        }

        for (UserCallBillVo.UserModelBillVo billVo : modelBillList) {
            Integer type = billVo.getType();
            if (ModelCallTypeEnum.AML.getType().equals(type)){
                String modelId = billVo.getModelId();
                Integer amlId = modelTaskIdMap.get(modelId);
                billVo.setAmlId(amlId);
            }
        }
    }

    private Map<String, TaskDo> getModelTaskIdMap(List<String> modelIds) {
        if (CollectionUtils.isEmpty(modelIds)) {
            return new HashMap<>();
        }
        List<TaskDo> tasks = taskDao.getModelRelTasks(modelIds);

        Map<String, TaskDo> map = new HashMap<>();
        if (!CollectionUtils.isEmpty(tasks)) {
            for (TaskDo task : tasks) {

                map.put(task.getModelUuid(), task);
            }
        }
        return map;
    }

    private Map<String, Integer> getModelAmlIdMap(List<Integer> modelIds) {
        if (CollectionUtils.isEmpty(modelIds)) {
            return new HashMap<>();
        }
        List<AmlModelDo> modelList = amlModelDao.getByModelIds(modelIds);


        Map<String, Integer> map = new HashMap<>();
        if (!CollectionUtils.isEmpty(modelList)) {
            for (AmlModelDo modelDo : modelList) {
                map.put(String.valueOf(modelDo.getId()),modelDo.getAmlId());
            }

        }
        return map;
    }

    private Map<String, List<ModelSourceCallPo>> getModelDetailMap(List<ModelSourceCallPo> userDetailList) {

        Map<String, List<ModelSourceCallPo>> modelDetailMap = new HashMap<>();
        if (CollectionUtils.isEmpty(userDetailList)) {
            return modelDetailMap;
        }
        for (int i = 0; i < userDetailList.size(); i++) {
            ModelSourceCallPo callPo = userDetailList.get(i);
            String modelId = callPo.getModelId();
            List<ModelSourceCallPo> detailList = modelDetailMap.computeIfAbsent(modelId, k -> new ArrayList<>());
            detailList.add(callPo);
        }
        return modelDetailMap;
    }

    private UserCallBillVo.UserModelBillVo buildModelBill(List<ModelSourceCallPo> sameModelList, Integer startDay, Integer endDay) {
        if (CollectionUtils.isEmpty(sameModelList)) {
            return null;
        }

        ModelSourceCallPo first = sameModelList.get(0);
        String modelId = first.getModelId();

        UserCallBillVo.UserModelBillVo billVo = new UserCallBillVo.UserModelBillVo();

        Long modelTotal = sameModelList.stream().map(k -> k.getNum().longValue()).reduce(0L, Long::sum);

        Map<Integer,DayCallVo> dayMap = new HashMap<>();
        for (ModelSourceCallPo callPo : sameModelList) {

            DayCallVo callVo = dayMap.get(callPo.getDay());
            if (callVo == null){
                String dayFormat = TimeUtil.dayFormat(callPo.getDay());
                DayCallVo dayCallVo = DayCallVo.builder().date(dayFormat).day(callPo.getDay()).num(callPo.getNum()).build();
                dayMap.put(callPo.getDay(),dayCallVo);
            }else {
                callVo.setNum(callVo.getNum()+callPo.getNum());
            }
        }
        List<DayCallVo> dayCallVoList = new ArrayList<>(dayMap.values());

        if (!CollectionUtils.isEmpty(dayCallVoList)) {
            if (dayCallVoList.size()>1){
                dayCallVoList.sort(Comparator.comparing(DayCallVo::getDay));
            }
            Integer day = dayCallVoList.get(0).getDay();
            if (!day.equals(startDay)) {
                String startDayFormat = TimeUtil.dayFormat(startDay);
                DayCallVo dayCallVo = DayCallVo.builder().date(startDayFormat).day(startDay).num(0).build();
                dayCallVoList.add(0,dayCallVo);
            }
            if (!endDay.equals(startDay)) {
                DayCallVo dayCallVo = dayCallVoList.get(dayCallVoList.size() - 1);

                if (!endDay.equals(dayCallVo.getDay())) {

                    DayCallVo endDayCallVo = DayCallVo.builder().date(TimeUtil.dayFormat(endDay)).day(endDay).num(0).build();
                    dayCallVoList.add(endDayCallVo);
                }
            }
        }


        billVo.setDayDetailList(dayCallVoList);

        billVo.setType(first.getType());
        billVo.setModelId(first.getModelId());


        billVo.setNum(modelTotal);
        billVo.setModelName(modelId);
        if (ModelCallTypeEnum.AML.getType().equals(first.getType())) {
            if (!modelId.matches("\\d+")) {
                log.error("aml-id:{},-not num", modelId);
                return null;
            }

            AmlModelDo modelDo = amlModelDao.loadIgnore(Integer.parseInt(modelId));
            if (modelDo != null) {
                billVo.setModelName(modelDo.getName());
            }
        } else if (ModelCallTypeEnum.APP.getType().equals(first.getType())) {
            ApiDo apiDo = apiDao.getByModelUuid(modelId, true);
            if (apiDo != null) {
                billVo.setModelName(apiDo.getName());
            }
        }

        return billVo;
    }
}
