package com.tigerobo.x.pai.biz.ai.spatio.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tigerobo.x.pai.api.ai.enums.AiArtImageProcessEnum;
import com.tigerobo.x.pai.api.ai.req.ArtImageChooseMainReq;
import com.tigerobo.x.pai.api.ai.req.ArtImageOnlineReq;
import com.tigerobo.x.pai.api.enums.BusinessEnum;
import com.tigerobo.x.pai.biz.ai.AiUserInteractService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.dal.ai.dao.AiSpatioActionDao;
import com.tigerobo.x.pai.dal.ai.dao.AiUserInterActDao;
import com.tigerobo.x.pai.dal.ai.entity.AiSpatioActionPo;
import com.tigerobo.x.pai.dal.ai.entity.AiUserInteractPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

@Slf4j
@Service
public class AiSpatioActionOperateService {

    @Autowired
    private AiSpatioActionDao aiSpatioActionDao;

    @Autowired
    private AiUserInterActDao aiUserInterActDao;
    @Autowired
    private AiUserInteractService aiUserInteractService;

    public void online(ArtImageOnlineReq req, Integer adminUserId) {

        Validate.isTrue(req.getId() != null, "参数为空");
//        Validate.isTrue(StringUtils.isNotBlank(req.getTitle()), "标题为空");

        AiSpatioActionPo po = aiSpatioActionDao.load(req.getId());
        permissionCheck(po, adminUserId);
        AiSpatioActionPo update = new AiSpatioActionPo();
        Integer status = 1;
        if (!po.getStatus().equals(status)) {
            update.setStatus(status);
            update.setPublishTime(new Date());
        }
        update.setTitle(req.getTitle());
        update.setDesc(req.getDesc());
        update.setId(po.getId());
        aiSpatioActionDao.update(update);

        if (!aiUserInteractService.isSupport(SpatioConstant.businessEnum.getType())){
            return;
        }
        AiUserInteractPo aiUserInteractPo = new AiUserInteractPo();

        boolean needUpdate = false;
        if (update.getStatus() != null) {
            aiUserInteractPo.setStatus(update.getStatus());
            needUpdate = true;
        }
        aiUserInteractPo.setPublishTime(update.getPublishTime());
        if (StringUtils.isNotBlank(update.getTitle())) {
            aiUserInteractPo.setTitle(update.getTitle());

            needUpdate = true;
        }

        if (needUpdate) {
            aiUserInterActDao.online(String.valueOf(req.getId()),
                    BusinessEnum.SPATIO_ACTION.getType(), aiUserInteractPo);
        }

    }

    public void offline(Integer id, Integer adminUserId) {

        AiSpatioActionPo po = aiSpatioActionDao.load(id);

        permissionCheck(po, adminUserId);

        Integer status = 0;
        if (po.getStatus().equals(status)) {
            return;
        }

        AiSpatioActionPo update = new AiSpatioActionPo();
        update.setStatus(status);
        update.setId(id);
        aiSpatioActionDao.update(update);
        if (!aiUserInteractService.isSupport(SpatioConstant.businessEnum.getType())){
            return;
        }
        aiUserInterActDao.offline(String.valueOf(id), BusinessEnum.SPATIO_ACTION.getType());
    }

    public void failRetry(Integer id) {

        AiSpatioActionPo po = aiSpatioActionDao.load(id);
        permissionCheck(po, null);

        Validate.isTrue(AiArtImageProcessEnum.FAIL.getStatus().equals(po.getProcessStatus()), "只有失败的才能重试");
        Validate.isTrue(po.getRetryTime() < 5, "重试次数过多");
        AiSpatioActionPo update = new AiSpatioActionPo();
        update.setProcessStatus(AiArtImageProcessEnum.PREPARE.getStatus());
        update.setId(id);
        update.setRetryTime(po.getRetryTime() + 1);
        aiSpatioActionDao.update(update);
    }

    private void permissionCheck(AiSpatioActionPo po, Integer adminUserId) {
        Validate.isTrue(po != null, "图片生成不存在");

        Integer userId = ThreadLocalHolder.getUserId();

        if (adminUserId != null && adminUserId > 0) {
            return;
        }
        Validate.isTrue(Objects.equals(userId, po.getUserId()), "用户没权限");
    }

    public void delete(Integer id, Integer adminUserId) {

        AiSpatioActionPo load = aiSpatioActionDao.load(id);

        if (load == null) {
            return;
        }


        permissionCheck(load, adminUserId);

        aiSpatioActionDao.delete(id);
        if (!aiUserInteractService.isSupport(SpatioConstant.businessEnum.getType())){
            return;
        }
        aiUserInterActDao.delete(String.valueOf(id), BusinessEnum.SPATIO_ACTION.getType());
    }

}
