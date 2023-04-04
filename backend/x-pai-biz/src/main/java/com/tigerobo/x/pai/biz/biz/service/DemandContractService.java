package com.tigerobo.x.pai.biz.biz.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Preconditions;
import com.qiyuesuo.sdk.v2.bean.Contract;
import com.qiyuesuo.sdk.v2.json.JSONUtils;
import com.qiyuesuo.sdk.v2.response.ContractPageResult;
import com.qiyuesuo.sdk.v2.response.SdkResponse;
import com.tigerobo.x.pai.api.auth.entity.OrgInfoDto;
import com.tigerobo.x.pai.api.biz.entity.Demand;
import com.tigerobo.x.pai.api.vo.biz.contract.ContractDraftVo;
import com.tigerobo.x.pai.api.vo.biz.req.DemandContractSampleReq;
import com.tigerobo.x.pai.api.vo.biz.req.DemandSignContractReq;
import com.tigerobo.x.pai.api.exception.AuthorizeException;
import com.tigerobo.x.pai.api.exception.ResultCode;
import com.tigerobo.x.pai.api.enums.ContractChannelEnum;
import com.tigerobo.x.pai.api.enums.ContractStatusEnum;
import com.tigerobo.x.pai.api.enums.DemandContractStatusEnum;
import com.tigerobo.x.pai.api.enums.OrgVerifyStatusEnum;
import com.tigerobo.x.pai.biz.auth.OrgInfoService;
import com.tigerobo.x.pai.biz.third.qiyuesuo.QysService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.dal.biz.dao.ContractInfoDao;
import com.tigerobo.x.pai.dal.biz.dao.DemandDao;
import com.tigerobo.x.pai.dal.biz.dao.ModelDao;
import com.tigerobo.x.pai.dal.biz.dao.TaskDao;
import com.tigerobo.x.pai.dal.biz.entity.ContractInfoPo;
import com.tigerobo.x.pai.dal.biz.entity.DemandDo;
import com.tigerobo.x.pai.dal.biz.entity.ModelDo;
import com.tigerobo.x.pai.dal.biz.entity.TaskDo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;

@Slf4j
@Service
public class DemandContractService {


    @Value("${pai.qys.key:}")
    private String key = "PhbPPimEUj";
    @Value("${pai.qys.secret:}")
    private String secret = "oiFaixSB3h05AsCplLCyGRh33IpV9O";
    @Value("${pai.qys.url:}")
    private String url = "https://openapi.qiyuesuo.cn";

    @Value("${pai.qys.sponsorName:}")
    private String sponsorName;

    @Value("${pai.qys.sponsorContactName:}")
    private String sponsorContactName;

    @Value("${pai.qys.sponsorMobile:}")
    private String sponsorMobile;


    @Autowired
    private DemandDao demandDao;
    @Autowired
    private TaskDao taskDao;
    @Autowired
    private ModelDao modelDao;
    @Autowired
    private ContractInfoDao contractInfoDao;

    @Autowired
    private OrgInfoService orgInfoService;

    @Autowired
    private QysService qysService;


    public void uploadDemandContractSample(DemandContractSampleReq sampleReq){

        Integer userId = ThreadLocalHolder.getUserId();
        if (userId==null){
            throw new AuthorizeException(ResultCode.USER_NOT_LOGIN);
        }
        String uuid = sampleReq.getUuid();
        Preconditions.checkArgument(!StringUtils.isEmpty(uuid), "参数为空");

        String sampleUrl = sampleReq.getSampleUrl();
        Preconditions.checkArgument(!StringUtils.isEmpty(sampleUrl),"样例地址为空");
        DemandDo demand = demandDao.getByUuid(uuid);
        Preconditions.checkArgument(demand != null, "需求不存在");


        DemandDo update = new DemandDo();

        update.setId(demand.getId());
        update.setContractSampleUrl(sampleUrl);
        update.setUpdateBy(String.valueOf(userId));
        demandDao.update(update);
    }
    public ContractDraftVo view(DemandSignContractReq contractReq) {

        String uuid = contractReq.getUuid();
        Preconditions.checkArgument(!StringUtils.isEmpty(uuid), "参数为空");

        DemandDo demand = demandDao.getByUuid(uuid);
        Preconditions.checkArgument(demand != null, "需求不存在");

        Integer contractId = demand.getContractId();
        Preconditions.checkArgument(contractId != null, "合同不存在");

        ContractInfoPo contract = contractInfoDao.getById(contractId);

        return getContractDraftVo(uuid,contract);
    }

    /**
     {"contractStatus":"SIGNING","contractId":"2880346780938076574","bizId":"12333","callbackType":"SEND_SIGNING"}
     {"tenantType":"COMPANY","contractStatus":"SIGNING","tenantName":"上海虎烨信息科技有限公司","contact":"18301966691","contractId":"2880352189157540117","tenantId":"2879583570958090963","bizId":"12335","callbackType":"SEAL"}
     {"contractStatus":"COMPLETE","contractId":"2880356389312529066","bizId":"12338","callbackType":"COMPLETE"}
     * @param content
     */
    public void dealContractCallBack(String content){

        if (StringUtils.isEmpty(content)){
            return;
        }
        JSONObject jsonObject = JSON.parseObject(content);
        String contractStatus = jsonObject.getString("contractStatus");
        if ("COMPLETE".equalsIgnoreCase(contractStatus)){
            Integer bizId = jsonObject.getInteger("bizId");
            if (bizId==null){
                log.error("callback:bizId为空,:{}",content);
                return;
            }
            completeContract(bizId);
        }
    }

    public void completeContract(Integer id){
        if (id == null){
            return;
        }
        ContractInfoPo contract = contractInfoDao.getById(id);
        Preconditions.checkArgument(contract!=null,"合同不存在");
        Integer status = contract.getStatus();

        String bizId = contract.getBizId();

        demandContractComplete(id, bizId);

        ContractInfoPo contractUpdate = new ContractInfoPo();
        contractUpdate.setId(contract.getId());
        contract.setStatus(ContractStatusEnum.SIGNED.getStatus());
        contract.setCompleteTime(new Date());
        contractInfoDao.update(contract);
    }

    private void demandContractComplete(Integer id, String bizId) {
        DemandDo demandDo = demandDao.getByUuid(bizId);
        if (demandDo!=null){
            Integer contractId = demandDo.getContractId();
            if (contractId!=null&&contractId.equals(id)){
                DemandDo demandUpdate = new DemandDo();
                demandUpdate.setId(demandDo.getId());
                demandUpdate.setPhase(Demand.Phase.HAS_SIGN_CONTRACT.getVal());
                demandUpdate.setContractCompleteTime(new Date());
                demandDao.update(demandUpdate);
            }
        }
    }

    public ContractDraftVo generateDemandContractOffline(DemandSignContractReq contractReq) {
        Integer userId = ThreadLocalHolder.getUserId();
        Preconditions.checkArgument(userId != null, "用户未登录");
        String uuid = contractReq.getUuid();
        Preconditions.checkArgument(!StringUtils.isEmpty(uuid), "参数为空");

        long beginPoint = System.currentTimeMillis();
        DemandDo demand = demandDao.getByUuid(uuid);
        Preconditions.checkArgument(demand != null, "需求不存在");
        TaskDo task = taskDao.getByUuid(uuid);

        Preconditions.checkArgument(task != null, "需求任务不存在");

        Preconditions.checkArgument(!StringUtils.isEmpty(contractReq.getContractUrl()), "合同文件为空");
        String modelUuid = task.getModelUuid();
        ModelDo model = modelDao.getByUuid(modelUuid);


        ContractInfoPo contractInfoPo = uploadOfflineContract(demand, model, contractReq.getContractUrl());
        return getContractDraftVo(demand.getUuid(), contractInfoPo);
    }
    public ContractDraftVo generateDemandContractDraft(DemandSignContractReq contractReq) {
        Integer userId = ThreadLocalHolder.getUserId();
        Preconditions.checkArgument(userId != null, "用户未登录");
        String uuid = contractReq.getUuid();
        Preconditions.checkArgument(!StringUtils.isEmpty(uuid), "参数为空");

        long beginPoint = System.currentTimeMillis();
        DemandDo demand = demandDao.getByUuid(uuid);
        Preconditions.checkArgument(demand != null, "需求不存在");
        TaskDo task = taskDao.getByUuid(uuid);

        Preconditions.checkArgument(task != null, "需求任务不存在");

        String modelUuid = task.getModelUuid();
//        Preconditions.checkArgument(!StringUtils.isEmpty(modelUuid), "需求模型不存在");

        ModelDo model = modelDao.getByUuid(modelUuid);
//        Preconditions.checkArgument(model != null, "需求模型不存在");

        Integer contractId = demand.getContractId();
        ContractInfoPo contractInfoPo = contractInfoDao.getById(contractId);


        boolean needCreate = true;
        if (contractInfoPo!=null&& !ContractStatusEnum.CANCEL.getStatus().equals(contractInfoPo.getStatus())){
            needCreate = false;
        }
        if (needCreate){
            Integer phase = demand.getPhase();
            Demand.Phase phaseEnum = Demand.Phase.valueOf(phase);
            if (Demand.Phase.WAIT_TEST == phaseEnum || Demand.Phase.TEST_PASS == phaseEnum) {
            } else {
                throw new AuthorizeException("当前状态不能签署合同");
            }
            long beforeContractPoint = System.currentTimeMillis();
            contractInfoPo = createQysContract(demand,model);
            log.info("demand-uuid:{},createContractDelta:{}",uuid,beforeContractPoint-beginPoint);
        }
        long beforeSponsorDraftPoint = System.currentTimeMillis();
        sponsorDraft(contractInfoPo,demand);
        log.info("sponsorTime:uuid{},delta:{}",uuid,System.currentTimeMillis()-beforeSponsorDraftPoint);

        long beforeGetContractPagePoint = System.currentTimeMillis();
        ContractDraftVo contractDraftVo = getContractDraftVo(uuid, contractInfoPo);
        log.info("getContractPagePoint:uuid{},delta:{}",uuid,System.currentTimeMillis()-beforeGetContractPagePoint);

        log.info("totalTime:uuid{},delta:{}",uuid,System.currentTimeMillis()-beginPoint);
        return contractDraftVo;
    }

    private ContractDraftVo getContractDraftVo(String uuid,ContractInfoPo contractInfoPo) {
        Integer channel = contractInfoPo.getChannel();
        if (channel == null){
            return null;
        }

        if (channel.equals(ContractChannelEnum.QIYUESUO.getType())){
            String thirdContractId = contractInfoPo.getThirdContractId();
            long qysId = Long.parseLong(thirdContractId);
            SdkResponse<ContractPageResult> pageUrlResp = qysService.pageUrl(qysId, contractInfoPo.getOrgContactMobile());
            String pageUrl;
            if(pageUrlResp.getCode() == 0) {
                ContractPageResult result = pageUrlResp.getResult();
                pageUrl = result.getPageUrl();
                log.info("合同:{},页面地址为:{}", contractInfoPo.getId(), pageUrl);
            } else {
                log.info("请求失败，错误码:{}，错误信息:{}", pageUrlResp.getCode(), pageUrlResp.getMessage());
                log.error("demandId:{},info:{}", uuid, JSON.toJSON(pageUrlResp));
                throw new AuthorizeException("生成合同页面失败");
            }
            ContractDraftVo contractDraftVo = new ContractDraftVo();
            contractDraftVo.setContractPageUrl(pageUrl);
            contractDraftVo.setContractId(contractInfoPo.getId());
            contractDraftVo.setDemandUuid(uuid);
            contractDraftVo.setContractChannel(channel);
            return contractDraftVo;
        }else if (channel.equals(ContractChannelEnum.OFFLINE.getType())){
            ContractDraftVo contractDraftVo = new ContractDraftVo();
//            contractDraftVo.setContractPageUrl(pageUrl);
            contractDraftVo.setContractId(contractInfoPo.getId());
            contractDraftVo.setDemandUuid(uuid);
            contractDraftVo.setContractChannel(channel);
            contractDraftVo.setContractUrl(contractInfoPo.getContractUrl());
            return contractDraftVo;
        }

        return null;

    }

    private void sponsorDraft(ContractInfoPo contractInfoPo, DemandDo demand) {

        if (!StringUtils.isEmpty(contractInfoPo.getThirdContractId())){
            return;
        }
        SdkResponse<Contract> draft = null;
        String bizId = contractInfoPo.getBizId();
        try {
            draft = qysService.draft(contractInfoPo);

        }catch (Exception ex){

            log.error("生成合同草稿失败,demandId:{}", bizId,ex);
            throw new AuthorizeException("生成合同失败");
        }
        Long thirdId = null;
        try {
            if (draft != null) {
                if (draft.getCode() == 0) {
                    Contract result = draft.getResult();
                    log.info("创建合同成功，demandId:{},合同ID:{}", bizId, result.getId());
                    thirdId = result.getId();
                } else {
                    log.error("请求失败，错误码:{}，错误信息:{},:{}", draft.getCode(), draft.getMessage(), JSONUtils.toJson(draft));
                    throw new AuthorizeException(draft.getMessage());
                }
            }
        }catch (Exception ex){
            log.error("demandId:{}",bizId,ex);
        }
        if (thirdId == null){
            throw new AuthorizeException("生成合同失败");
        }

        ContractInfoPo update = new ContractInfoPo();
        update.setId(contractInfoPo.getId());
        String thirdContractId = String.valueOf(thirdId);
        update.setThirdContractId(thirdContractId);
        update.setSponsorTime(new Date());
        update.setStatus(ContractStatusEnum.ON_SIGN.getStatus());
        contractInfoDao.update(update);

        contractInfoPo.setThirdContractId(thirdContractId);

        contractInfoPo.setStatus(ContractStatusEnum.ON_SIGN.getStatus());



        DemandDo demandUpdate = new DemandDo();
        demandUpdate.setId(demand.getId());
        demandUpdate.setPhase(Demand.Phase.TEST_PASS.getVal());
        demandUpdate.setTestPassTime(new Date());
        demandDao.update(demandUpdate);
    }

    private ContractInfoPo createQysContract(DemandDo demand, ModelDo model) {

        Integer userId = ThreadLocalHolder.getUserId();

        String createBy = demand.getCreateBy();
        if (createBy == null || !createBy.matches("\\d+")) {
            throw new AuthorizeException("需求没有归属人");
        }
        int demandUserId = Integer.parseInt(createBy);

        OrgInfoDto orgInfo = orgInfoService.getOrgInfo(demandUserId);
        Preconditions.checkArgument(orgInfo != null && OrgVerifyStatusEnum.VERIFIED.getStatus().equals(orgInfo.getVerifyStatus())
                , "用户企业未认证,请先认证");

        String contractCategoryId = demand.getContractCategoryId();
        Preconditions.checkArgument(!StringUtils.isEmpty(contractCategoryId),"合同分类id为空");

        ContractInfoPo infoPo = new ContractInfoPo();

        infoPo.setCreateBy(userId == null ? null : String.valueOf(userId));
        infoPo.setUserId(demandUserId);

        infoPo.setContractName(demand.getName());
        infoPo.setOrgId(orgInfo.getId());
        infoPo.setOrgFullName(orgInfo.getFullName());
        infoPo.setOrgContactMobile(orgInfo.getContactMobile());
        infoPo.setOrgContactName(orgInfo.getContactName());

        infoPo.setBizId(demand.getUuid());
        infoPo.setBizName(demand.getName());
        infoPo.setSubject(demand.getName());
        infoPo.setStatus(DemandContractStatusEnum.NOT_START.getStatus());

        if (model!=null){
            infoPo.setProductId(model.getUuid());
            infoPo.setProductName(model.getName());
        }


        infoPo.setCategoryId(contractCategoryId);
        infoPo.setSponsorName(sponsorName);
        infoPo.setSponsorContactName(sponsorContactName);
        infoPo.setSponsorMobile(sponsorMobile);
        infoPo.setChannel(ContractChannelEnum.QIYUESUO.getType());
        contractInfoDao.add(infoPo);

        Preconditions.checkArgument(infoPo.getId()!=null);
        DemandDo demandUpdate = new DemandDo();
        demandUpdate.setId(demand.getId());
        demandUpdate.setContractId(infoPo.getId());
        demandDao.update(demandUpdate);
        return infoPo;
    }

    private ContractInfoPo uploadOfflineContract(DemandDo demand, ModelDo model,String contractUrl) {

        Integer userId = ThreadLocalHolder.getUserId();

        Integer contractId = demand.getContractId();
        ContractInfoPo contractDb = null;
        if (contractId!=null&&contractId>0){
            contractDb = contractInfoDao.getById(contractId);
        }

        if (contractDb!=null){
            ContractInfoPo update = new ContractInfoPo();
            update.setChannel(ContractChannelEnum.OFFLINE.getType());
            update.setContractUrl(contractUrl);
            update.setCreateBy(String.valueOf(userId));
            update.setStatus(DemandContractStatusEnum.SIGNED.getStatus());
            contractInfoDao.update(update);
        }else {
            String createBy = demand.getCreateBy();
            if (createBy == null || !createBy.matches("\\d+")) {
                throw new AuthorizeException("需求没有归属人");
            }
            int demandUserId = Integer.parseInt(createBy);

            ContractInfoPo infoPo = new ContractInfoPo();

            infoPo.setCreateBy(userId == null ? null : String.valueOf(userId));
            infoPo.setUserId(demandUserId);

            infoPo.setContractName(demand.getName());


            infoPo.setBizId(demand.getUuid());
            infoPo.setBizName(demand.getName());
            infoPo.setSubject(demand.getName());
            infoPo.setStatus(DemandContractStatusEnum.SIGNED.getStatus());

            if (model!=null){
                infoPo.setProductId(model.getUuid());
                infoPo.setProductName(model.getName());
            }

            infoPo.setSponsorName(sponsorName);
            infoPo.setSponsorContactName(sponsorContactName);
            infoPo.setSponsorMobile(sponsorMobile);
            infoPo.setChannel(ContractChannelEnum.OFFLINE.getType());

            infoPo.setContractUrl(contractUrl);
            contractInfoDao.add(infoPo);
            Preconditions.checkArgument(infoPo.getId()!=null);

            contractId = infoPo.getId();
        }


        DemandDo demandUpdate = new DemandDo();
        demandUpdate.setId(demand.getId());
        demandUpdate.setContractId(contractId);
        demandUpdate.setPhase(Demand.Phase.HAS_SIGN_CONTRACT.getVal());
        demandUpdate.setContractCompleteTime(new Date());
        demandDao.update(demandUpdate);

        return contractInfoDao.getById(contractId);

    }
}
