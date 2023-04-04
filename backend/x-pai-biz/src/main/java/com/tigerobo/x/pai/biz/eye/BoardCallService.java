package com.tigerobo.x.pai.biz.eye;

import com.google.common.base.Preconditions;
import com.tigerobo.x.pai.api.auth.entity.Group;
import com.tigerobo.x.pai.api.auth.entity.User;
import com.tigerobo.x.pai.api.auth.service.UserService;
import com.tigerobo.x.pai.api.enums.ModelCallSourceEnum;
import com.tigerobo.x.pai.api.eye.req.ModelBoardReq;
import com.tigerobo.x.pai.biz.biz.service.WebGroupService;
import com.tigerobo.x.pai.biz.utils.TimeUtil;
import com.tigerobo.x.pai.dal.biz.dao.ApiDao;
import com.tigerobo.x.pai.dal.biz.dao.call.ModelCallDao;
import com.tigerobo.x.pai.dal.biz.entity.ApiDo;
import com.tigerobo.x.pai.dal.biz.entity.call.ModelCallPo;
import com.tigerobo.x.pai.dal.biz.entity.eye.CallModelDayPo;
import com.tigerobo.x.pai.dal.biz.entity.eye.CallModelGuestPo;
import com.tigerobo.x.pai.dal.biz.entity.eye.CallModelNamePo;
import com.tigerobo.x.pai.dal.biz.entity.eye.CallModelUserPo;
import com.tigerobo.x.pai.dal.biz.mapper.ModelQueryMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BoardCallService {

    @Autowired
    private ModelQueryMapper modelQueryMapper;

    @Autowired
    private ModelCallDao modelCallDao;


    @Autowired
    private ApiDao apiDao;

    @Autowired
    private UserService userService;
    @Autowired
    private WebGroupService webGroupService;


    public BoardStatistic statisticOnModelCall(ModelBoardReq boardReq) {

        Preconditions.checkArgument(boardReq != null, "参数为空");
        Date startDate = boardReq.getStartDate();
        Date today = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
        if (startDate == null) {
            startDate = DateUtils.addDays(today, -5);
        }
        Date endDate = boardReq.getEndDate();
        if (endDate == null || endDate.after(today)) {
            endDate = today;
        }
        int startDay = Integer.parseInt(DateFormatUtils.format(startDate, "yyyyMMdd"));

        int endDay = Integer.parseInt(DateFormatUtils.format(endDate, "yyyyMMdd"));
        boardReq.setStartDay(startDay);
        boardReq.setEndDay(endDay);
        List<ApiDo> bindAmlList = apiDao.getBindAmlList();
        List<ModelCallPo> callList = getAmlModelCalls(boardReq, bindAmlList);
        BoardStatistic statistic = new BoardStatistic();
        initByDay(boardReq, statistic);

        initByModel(boardReq, statistic, callList, bindAmlList);
        initByUser(boardReq, statistic);
        initGuest(boardReq, statistic);
        return statistic;
    }


    private List<ModelCallPo> getAmlModelCalls(ModelBoardReq boardReq, List<ApiDo> bindAmlList) {
        List<ModelCallPo> callList = null;

        Integer callSource = boardReq.getCallSource();
        boolean useAmlMatch = callSource == null || ModelCallSourceEnum.INVOKE.getType().equals(callSource);

        if (!useAmlMatch){
            return null;
        }
        List<Integer> amlModelIds = new ArrayList<>();
        if (!CollectionUtils.isEmpty(bindAmlList)) {

            for (ApiDo apiDo:bindAmlList){
                String amlRelIds = apiDo.getAmlRelIds();
                if (StringUtils.isNotBlank(amlRelIds)){
                    String[] modelIds = amlRelIds.split(",");
                    for (String modelId : modelIds) {
                        if (modelId.matches("\\d+")){
                            amlModelIds.add(Integer.parseInt(modelId));
                        }
                    }
                }
            }
            callList = modelCallDao.getCallByDay(boardReq, amlModelIds);
        }
        return callList;
    }

    private void initGuest(ModelBoardReq boardReq, BoardStatistic statistic) {
        statistic.setModelCallByGuestList(new ArrayList<>());
        statistic.setModelCallByGuestSum(0);
//
//        List<CallModelGuestPo> modelCallByGuest = modelQueryMapper.getModelCallByGuest(boardReq);
//        int groupByGuestSum = modelQueryMapper.countModelCallByGuest(boardReq);
//        if (modelCallByGuest == null) {
//            modelCallByGuest = new ArrayList<>();
//        }
//        statistic.setModelCallByGuestList(modelCallByGuest);
//        statistic.setModelCallByGuestSum(groupByGuestSum);
    }

    private void initByUser(ModelBoardReq boardReq, BoardStatistic statistic) {
        List<CallModelUserPo> groupByUserList = modelQueryMapper.getSourceModelCallByUser(boardReq);
        if (groupByUserList == null) {
            groupByUserList = new ArrayList<>();
        }else {
            for (CallModelUserPo callModelUserPo : groupByUserList) {
                Integer userId = callModelUserPo.getUserId();
                User userCache = userService.getFromCache(userId);
                if (userCache == null){
                    continue;
                }
                callModelUserPo.setUserName(userCache.getName());
                callModelUserPo.setAdmin(userCache.getRoleType());

                callModelUserPo.setMobile(userCache.getMobile());
                callModelUserPo.setWechatName(userCache.getWechatName());
                callModelUserPo.setUserAccount(userCache.getAccount());

                Group groupVo = webGroupService.getByUuidFromCache(userCache.getCurrentGroupUuid());
                if (groupVo!=null){
                    callModelUserPo.setGroupName(groupVo.getName());
                    callModelUserPo.setGroupUuid(groupVo.getUuid());
                }
            }
        }
        Map<Integer, CallModelUserPo> userMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(groupByUserList)) {
            for (CallModelUserPo callModelUserPo : groupByUserList) {
                userMap.put(callModelUserPo.getUserId(), callModelUserPo);
            }
        }

        long modelCallByUserSum = 0;
        if (!CollectionUtils.isEmpty(groupByUserList)) {
            groupByUserList.sort(Comparator.comparing(CallModelUserPo::getNum).reversed());
            modelCallByUserSum = groupByUserList.stream().mapToLong(CallModelUserPo::getNum).sum();
        }
        statistic.setModelCallByUserList(groupByUserList);
        statistic.setModelCallByUserSum(modelCallByUserSum);
    }


    private void initByModel(ModelBoardReq boardReq, BoardStatistic statistic, List<ModelCallPo> callList, List<ApiDo> bindAmlList) {
        List<CallModelNamePo> listByModel = modelQueryMapper.getSourceCallByModelName(boardReq);
        if (listByModel == null) {
            listByModel = new ArrayList<>();
        }else {
            for (CallModelNamePo namePo : listByModel) {
                String modelId = namePo.getModelId();
                Integer type = namePo.getType();
                ApiDo apiDo = apiDao.getByModelUuid(modelId, true);
                if (apiDo == null){
                    namePo.setModelName(modelId);
                }else {
                    namePo.setModelName(apiDo.getName());
                }
            }

        }

        Map<String, Integer> amlCountMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(callList)) {
            for (ModelCallPo modelCallPo : callList) {
                String modelId = String.valueOf(modelCallPo.getModelId());

                Integer count = amlCountMap.computeIfAbsent(modelId, k -> 0);
                count += modelCallPo.getNum();
                amlCountMap.put(modelId, count);
            }
        }




        if (amlCountMap.size() > 0&&!CollectionUtils.isEmpty(bindAmlList)) {

            for (ApiDo apiDo : bindAmlList) {

                int amlCount =0;
                String amlRelIds = apiDo.getAmlRelIds();
                if (StringUtils.isNotBlank(amlRelIds)){
                    String[] split = amlRelIds.split(",");
                    for (String relModelId : split) {
                        Integer count = amlCountMap.get(relModelId);
                        if (count!=null&&count>0){
                            amlCount+= count;
                        }
                    }
                }
                if (amlCount==0){
                    continue;
                }
                String uuid = apiDo.getUuid();
                CallModelNamePo namePo = getModelNamePoByUuid(uuid, listByModel);
                if (namePo == null) {
                    namePo = new CallModelNamePo();
                    namePo.setModelId(uuid);
                    namePo.setModelName(apiDo.getName());
                    namePo.setNum(amlCount);
                    listByModel.add(namePo);
                } else {
                    int existNum = namePo.getNum();
                    existNum += amlCount;
                    namePo.setNum(existNum);
                }
            }
        }

        listByModel.sort(Comparator.comparing(CallModelNamePo::getNum).reversed());
        statistic.setModelCallByNameList(listByModel);

        int modelGroupByNameSum = 0;
        for (CallModelNamePo namePo : listByModel) {
            modelGroupByNameSum += namePo.getNum();
        }
        statistic.setModelCallByNameSum(modelGroupByNameSum);
    }

    private CallModelNamePo getModelNamePoByUuid(String uuid, List<CallModelNamePo> list) {
        if (CollectionUtils.isEmpty(list) || StringUtils.isBlank(uuid)) {
            return null;
        }
        for (CallModelNamePo callModelNamePo : list) {
            if (uuid.equals(callModelNamePo.getModelId())) {
                return callModelNamePo;
            }
        }

        return null;
    }

    private ApiDo getByAmlId(Integer amlModelId, List<ApiDo> bindAmlList) {

        if (CollectionUtils.isEmpty(bindAmlList)) {
            return null;
        }

        for (ApiDo apiDo : bindAmlList) {
            if (amlModelId.equals(apiDo.getAmlModelId())) {
                return apiDo;
            }
        }
        return null;
    }

    private void initByDay(ModelBoardReq boardReq, BoardStatistic statistic) {
        List<CallModelDayPo> callByDayList = modelQueryMapper.getSourceCallByDay(boardReq);

        if (callByDayList == null) {
            callByDayList = new ArrayList<>();
        }
        for (CallModelDayPo po : callByDayList) {
            po.setDate(TimeUtil.dayFormat(po.getDay()));
        }

        Long modelCallByDaySum = callByDayList.stream().map(c -> (long) (c.getNum())).reduce(0L, Long::sum);

        statistic.setModelCallByDayList(callByDayList);
        statistic.setModelCallByDaySum(modelCallByDaySum);
    }


    @Data
    public static class BoardStatistic {

        List<CallModelDayPo> modelCallByDayList;
        long modelCallByDaySum;

        List<CallModelNamePo> modelCallByNameList;
        int modelCallByNameSum;

        List<CallModelUserPo> modelCallByUserList;
        long modelCallByUserSum;

        List<CallModelGuestPo> modelCallByGuestList;
        int modelCallByGuestSum;


    }
}
