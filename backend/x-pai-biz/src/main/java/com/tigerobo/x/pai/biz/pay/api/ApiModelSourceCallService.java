package com.tigerobo.x.pai.biz.pay.api;

import com.tigerobo.x.pai.api.enums.ModelCallSourceEnum;
import com.tigerobo.x.pai.api.enums.ModelCallTypeEnum;
import com.tigerobo.x.pai.dal.biz.dao.ApiDao;
import com.tigerobo.x.pai.dal.biz.dao.call.ModelSourceCallDao;
import com.tigerobo.x.pai.dal.biz.entity.call.ModelSourceCallPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class ApiModelSourceCallService {

    @Autowired
    private ModelSourceCallDao modelSourceCallDao;

    @Autowired
    private ApiDao apiDao;



    public long getUserApiCallNum(int userId, int day, String modelId){

        if (StringUtils.isEmpty(modelId)){
            return 0L;
        }

//        modelSourceCallDao.getByDay()

        final List<Integer> sourceList = Arrays.asList(ModelCallSourceEnum.API_BATCH_EVALUATE.getType(),
                ModelCallSourceEnum.INVOKE.getType());
        final List<ModelSourceCallPo> userDayCallList = modelSourceCallDao.
                getUserDayCallList(day, userId, modelId, sourceList, ModelCallTypeEnum.APP.getType());

        long total = 0;
        if (!CollectionUtils.isEmpty(userDayCallList)){
            for (ModelSourceCallPo callPo : userDayCallList) {
                final Integer num = callPo.getNum();
                if (num!=null){
                    total += num;
                }
            }

        }
        return total;
    }


}
