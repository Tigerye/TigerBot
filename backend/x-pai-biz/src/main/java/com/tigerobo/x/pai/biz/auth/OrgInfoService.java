package com.tigerobo.x.pai.biz.auth;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Preconditions;
import com.qiyuesuo.sdk.v2.response.CompanyAuthPageResult;
import com.qiyuesuo.sdk.v2.response.CompanyAuthResult;
import com.qiyuesuo.sdk.v2.response.SdkResponse;
import com.tigerobo.x.pai.api.auth.entity.OrgInfoDto;
import com.tigerobo.x.pai.api.enums.OrgVerifyStatusEnum;
import com.tigerobo.x.pai.biz.converter.OrgInfoConvert;
import com.tigerobo.x.pai.biz.third.qiyuesuo.QysService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.dal.auth.dao.OrgInfoDao;
import com.tigerobo.x.pai.dal.auth.dao.UserDao;
import com.tigerobo.x.pai.dal.auth.entity.OrgInfoPo;
import com.tigerobo.x.pai.dal.auth.entity.UserDo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Objects;

@Slf4j
@Service
public class OrgInfoService {


    @Autowired
    private OrgInfoDao orgInfoDao;
    @Autowired
    private QysService qysService;

    @Autowired
    private UserDao userDao;

    public void callback(Integer id, Integer status, String authInfo) {
        if (StringUtils.isEmpty(authInfo)) {
            log.error("org-callback-id:{},status:{}", id, status);
            return;
        }

        if (status == null) {
            return;
        }
        OrgInfoPo load = orgInfoDao.load(id);
        if (load == null) {
            return;
        }
        JSONObject authJson = JSON.parseObject(authInfo);
        if (!notChange(load, authJson)) {
            return;
        }
        if (status.equals(0)) {
            OrgInfoPo update = new OrgInfoPo();
            update.setId(id);
            update.setVerifyStatus(OrgVerifyStatusEnum.ON_VERIFY.getStatus());
            orgInfoDao.update(update);
        } else if (status.equals(1)) {
            OrgInfoPo update = new OrgInfoPo();
            update.setId(id);
            update.setVerifyStatus(OrgVerifyStatusEnum.VERIFIED.getStatus());
            update.setVerifyTime(new Date());
            orgInfoDao.update(update);
        } else if (status.equals(2)) {
//            String basicReason = authJson.getString("basicReason");
            verifyFromViewQys(load);
        }
    }
    public void verifyFromViewQys(Integer orgId) {
        OrgInfoPo load = orgInfoDao.load(orgId);
        verifyFromViewQys(load);
    }
    public void verifyFromViewQys(OrgInfoPo load) {
        if (load == null) {
            return;
        }
        Integer verifyStatus = load.getVerifyStatus();
        if (verifyStatus != null ) {
            if (org.apache.commons.lang3.StringUtils.isAnyEmpty(load.getFullName(), load.getContactMobile(), load.getContactName())) {
                return;
            }
            SdkResponse<CompanyAuthResult> viewResp = qysService.vimEnterprise(load.getFullName());
            if (viewResp != null && viewResp.getCode() == 0 && viewResp.getResult() != null) {
                CompanyAuthResult result = viewResp.getResult();

                Integer status = result.getStatus();
                if (status == null) {
                    return;
                }

                if (status.equals(-1) || status.equals(1)) {
                    if (!OrgVerifyStatusEnum.NOT_START.getStatus().equals(load.getVerifyStatus())) {
                        OrgInfoPo update = new OrgInfoPo();
                        update.setId(load.getId());
                        update.setVerifyStatus(OrgVerifyStatusEnum.NOT_START.getStatus());
                        orgInfoDao.update(update);
                    }
                    return;
                }
                boolean sameMobile = Objects.equals(load.getContactMobile(), result.getApplicantPhone());
                boolean sameContactName = Objects.equals(load.getContactMobile(), result.getApplicantName());

                if (status.equals(2)) {
                    if (sameContactName && sameMobile) {
                        OrgInfoPo update = new OrgInfoPo();
                        update.setId(load.getId());
                        update.setVerifyStatus(OrgVerifyStatusEnum.VERIFIED.getStatus());
                        update.setVerifyTime(new Date());
                        orgInfoDao.update(update);
                    } else {
                        OrgInfoPo update = new OrgInfoPo();
                        update.setId(load.getId());
                        update.setVerifyStatus(OrgVerifyStatusEnum.FAIL.getStatus());
                        String reason = "企业名称已认证，但联系人信息不一致:";
                        update.setReason(reason);
                        orgInfoDao.update(update);
                    }
                    return;
                }
                if (sameContactName&&sameMobile){
                    if (status.equals(3)) {
                        OrgInfoPo update = new OrgInfoPo();
                        update.setId(load.getId());
                        update.setVerifyStatus(OrgVerifyStatusEnum.FAIL.getStatus());
                        update.setReason(result.getBasicReason());
                        orgInfoDao.update(update);
                    } else if (status.equals(4)) {
                        OrgInfoPo update = new OrgInfoPo();
                        update.setId(load.getId());
                        update.setVerifyStatus(OrgVerifyStatusEnum.ON_VERIFY.getStatus());
                        update.setReason(result.getBasicReason());
                        orgInfoDao.update(update);
                    }
                }


            }

        }


    }


    private boolean notChange(OrgInfoPo org, JSONObject auth) {

        String name = auth.getString("name");
        String applicantName = auth.getString("applicantName");
        String applicantPhone = auth.getString("applicantPhone");

        if (!Objects.equals(org.getFullName(), name)) {
            log.error("org;id-{},name-{},verify-name-{}不一致", org.getId(), org.getFullName(), name);
            return false;
        }

        if (!Objects.equals(org.getContactMobile(), applicantPhone)) {
            log.error("org;id-{},mobile-{},verify-phone-{}不一致", org.getId(), org.getContactMobile(), applicantPhone);
            return false;
        }

        if (!Objects.equals(org.getContactName(), applicantName)) {
            log.error("org;id-{},contactName-{},verify-name-{}不一致", org.getId(), org.getContactName(), applicantName);
            return false;
        }
        return true;
    }

    public OrgInfoDto getOrgInfo(Integer userId) {

        OrgInfoPo infoPo = orgInfoDao.getByUserId(userId);

        if (infoPo != null) {
            return OrgInfoConvert.convert(infoPo);
        } else {
            UserDo user = userDao.getById(userId);
            Preconditions.checkArgument(user != null, "当前用户不存在");
            OrgInfoDto dto = new OrgInfoDto();
            if (StringUtils.isEmpty(user.getMobile())) {
                dto.setContactMobile(user.getMobile());
            }
            dto.setVerifyStatus(OrgVerifyStatusEnum.NOT_START.getStatus());
            dto.setVerifyStatusName(OrgVerifyStatusEnum.NOT_START.getName());
            return dto;
        }
    }

    public OrgInfoDto verify(OrgInfoDto orgInfoDto) {
        Integer userId = ThreadLocalHolder.getUserId();
        Preconditions.checkArgument(userId != null, "用户未登录");
        OrgInfoDto infoDto = addOrUpdate(orgInfoDto);

        if (OrgVerifyStatusEnum.VERIFIED.getStatus().equals(infoDto.getVerifyStatus())) {
            return infoDto;
        }
        SdkResponse<CompanyAuthResult> viewInfo = qysService.vimEnterprise(infoDto.getFullName());
        if (viewInfo != null && viewInfo.getResult() != null) {
            CompanyAuthResult result = viewInfo.getResult();
            Integer status = result.getStatus();
            if (status != null && status.equals(2)) {
                String applicantName = result.getApplicantName();
                String applicantPhone = result.getApplicantPhone();
                String msg = null;
                if (!Objects.equals(orgInfoDto.getContactMobile(), applicantPhone)) {
                    msg = "企业已认证，但绑定手机号与已认证手机号:" + applicantPhone + "不一致";

                }
                if (msg != null) {
                    infoDto.setVerifyStatus(OrgVerifyStatusEnum.FAIL.getStatus());
                    infoDto.setVerifyStatusName(OrgVerifyStatusEnum.FAIL.getName());
                    infoDto.setReason(msg);
                    return infoDto;
                }
                if (!Objects.equals(orgInfoDto.getContactName(), applicantName)) {
                    msg = "企业已认证，但联系人与不一致:" + applicantName + "不一致";
                }
                if (msg != null) {
                    infoDto.setVerifyStatus(OrgVerifyStatusEnum.FAIL.getStatus());
                    infoDto.setVerifyStatusName(OrgVerifyStatusEnum.FAIL.getName());
                    infoDto.setReason(msg);
                    return infoDto;
                }
                //认证成功
                verifySuccess(infoDto);
            }
        }
        SdkResponse<CompanyAuthPageResult> enterprise = qysService.enterprise(infoDto);
        if (enterprise != null) {
            if (enterprise.getCode() == 0) {
                CompanyAuthPageResult result = enterprise.getResult();
                String authUrl = result.getAuthUrl();
                infoDto.setAuthUrl(authUrl);

            } else if (enterprise.getMessage() != null && enterprise.getMessage().contains("已完成认证")) {
                verifySuccess(infoDto);
            }
        }
        return infoDto;
    }

    private void verifySuccess(OrgInfoDto infoDto) {
        infoDto.setVerifyStatus(OrgVerifyStatusEnum.VERIFIED.getStatus());
        infoDto.setVerifyStatusName(OrgVerifyStatusEnum.VERIFIED.getName());

        OrgInfoPo update = new OrgInfoPo();
        update.setId(infoDto.getId());
        update.setVerifyStatus(OrgVerifyStatusEnum.VERIFIED.getStatus());
        update.setVerifyTime(new Date());
        orgInfoDao.update(update);
    }

    public OrgInfoDto addOrUpdate(OrgInfoDto orgInfoDto) {
        Integer userId = ThreadLocalHolder.getUserId();
        Preconditions.checkArgument(userId != null, "当前用户未登录");
        Preconditions.checkArgument(orgInfoDto != null, "参数为空");
        Preconditions.checkArgument(!StringUtils.isEmpty(orgInfoDto.getFullName()), "公司全称不能为空");
        Preconditions.checkArgument(!StringUtils.isEmpty(orgInfoDto.getContactName()), "联系人名称不能为空");
        Preconditions.checkArgument(!StringUtils.isEmpty(orgInfoDto.getContactMobile()), "联系人手机号不能为空");

        Integer id = orgInfoDto.getId();

        if (id != null) {
            OrgInfoPo loadDb = orgInfoDao.load(id);
            Preconditions.checkArgument(loadDb != null, "id=" + id + "企业不存在");
            Preconditions.checkArgument(userId.equals(loadDb.getUserId()), "当前用户没权限");

            OrgInfoPo convert = OrgInfoConvert.convert(orgInfoDto);
            orgInfoDao.update(convert);
        } else {
            OrgInfoPo orgDb = orgInfoDao.getByUserId(userId);
            if (orgDb != null) {
                orgInfoDto.setId(orgDb.getId());
            }
            orgInfoDto.setUserId(userId);
            OrgInfoPo insert = OrgInfoConvert.convert(orgInfoDto);
            if (insert.getId() == null) {
                orgInfoDao.add(insert);
            } else {
                orgInfoDao.update(insert);
            }

            orgInfoDto.setId(insert.getId());
        }
        OrgInfoPo load = orgInfoDao.load(orgInfoDto.getId());
        return OrgInfoConvert.convert(load);

    }
}
