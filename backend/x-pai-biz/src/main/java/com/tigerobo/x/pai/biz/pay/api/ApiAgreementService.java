package com.tigerobo.x.pai.biz.pay.api;

import com.tigerobo.x.pai.api.pay.req.ApiAgreementReq;
import com.tigerobo.x.pai.api.vo.api.AgreementVo;
import com.tigerobo.x.pai.biz.pay.convert.AgreementConvert;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.dal.biz.dao.ApiDao;
import com.tigerobo.x.pai.dal.biz.dao.TaskDao;
import com.tigerobo.x.pai.dal.biz.entity.ApiDo;
import com.tigerobo.x.pai.dal.biz.entity.TaskDo;
import com.tigerobo.x.pai.dal.pay.dao.ApiAgreementDao;
import com.tigerobo.x.pai.dal.pay.dao.SkuDao;
import com.tigerobo.x.pai.dal.pay.entity.ApiAgreementPo;
import com.tigerobo.x.pai.dal.pay.entity.ProductSkuPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class ApiAgreementService {

    @Autowired
    private ApiAgreementDao apiAgreementDao;

    @Autowired
    private ApiUserConfigService apiUserConfigService;

    @Autowired
    private ApiDao apiDao;

    @Autowired
    private SkuDao skuDao;

    @Autowired
    TaskDao taskDao;
    @Transactional
    public AgreementVo addAgreement(ApiAgreementReq req) {
        Validate.isTrue(req != null, "参数不正确");
        Validate.isTrue(req.getSkuId()!=null,"未选商品");
        final Integer userId = ThreadLocalHolder.getUserId();

        Validate.isTrue(userId != null && userId > 0, "用户未登录");

        final String modelId = req.getModelId();
        final ApiDo apiDo = apiDao.getByModelUuid(modelId);
        Validate.isTrue(apiDo != null, "模型不存在");

        final Integer skuId = req.getSkuId();
        Validate.isTrue(skuId != null && skuId > 0, "模型不存在");


        final ProductSkuPo sku = skuDao.load(skuId);
        Validate.isTrue(sku != null, "模型不存在");

        ApiAgreementPo po = apiAgreementDao.getUserModelAgreement(userId, modelId);

        Validate.isTrue(po == null, "当前模型已绑定协议");

        final ApiAgreementPo agreement = AgreementConvert.build(userId, apiDo, sku);

        final int add = apiAgreementDao.add(agreement);
        Validate.isTrue(add == 1, "添加失败");
        apiUserConfigService.cancelTrayOverWarn(userId);


        final AgreementVo agreementVo = AgreementConvert.convert2vo(agreement);

        initTask(modelId, agreementVo);
        return agreementVo;

    }

    public List<AgreementVo> viewAgreements(Integer userId) {

        if (userId == null){
            return null;
        }
        final List<ApiAgreementPo> pos = apiAgreementDao.getUserAgreements(userId);

        return AgreementConvert.convert2vo(pos);
    }

    public AgreementVo getUserAgreement(Integer userId,String modelId) {

        if (userId == null|| StringUtils.isEmpty(modelId)){
            return null;
        }
        final ApiAgreementPo ag = apiAgreementDao.getUserModelAgreement(userId,modelId);

        final AgreementVo agreementVo = AgreementConvert.convert2vo(ag);

        initTask(modelId, agreementVo);
        return agreementVo;
    }

    private void initTask(String modelId, AgreementVo agreementVo) {
        if (agreementVo != null && agreementVo.getModelId() != null) {
            final List<TaskDo> modelRelTasks = taskDao.getModelRelTasks(Arrays.asList(modelId));
            if (!CollectionUtils.isEmpty(modelRelTasks)) {
                final String uuid = modelRelTasks.get(0).getUuid();
                agreementVo.setTaskId(uuid);

                final String appShortName = modelRelTasks.get(0).getAppShortName();
                agreementVo.setAppShortName(appShortName);
            }else{
                if (agreementVo.getAppShortName()==null){
                    agreementVo.setAppShortName("");
                }
            }

        }

    }

    public boolean hasSignAgreement(Integer userId) {
        if (userId == null) {
            return false;
        }
        final List<ApiAgreementPo> userAgreements = apiAgreementDao.getUserAgreements(userId);
        return !CollectionUtils.isEmpty(userAgreements);
    }
}
