package com.tigerobo.x.pai.biz.ai.multi.object.track;

import com.tigerobo.x.pai.api.ai.enums.AiArtImageProcessEnum;
import com.tigerobo.x.pai.api.ai.req.ArtImageOnlineReq;
import com.tigerobo.x.pai.api.enums.BusinessEnum;
import com.tigerobo.x.pai.biz.ai.AiUserInteractService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.dal.ai.dao.AiMultiObjectTrackDao;
import com.tigerobo.x.pai.dal.ai.dao.AiUserInterActDao;
import com.tigerobo.x.pai.dal.ai.entity.AiMultiObjectTrackPo;
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
public class AiMultiObjectTrackOperateService {
    final BusinessEnum businessEnum = BusinessEnum.MULTI_OBJECT_TRACK;
    @Autowired
    private AiMultiObjectTrackDao aiMultiObjectTrackDao;

    @Autowired
    private AiUserInterActDao aiUserInterActDao;
    @Autowired
    private AiUserInteractService aiUserInteractService;

    public void online(ArtImageOnlineReq req, Integer adminUserId) {

        Validate.isTrue(req.getId() != null, "参数为空");
//        Validate.isTrue(StringUtils.isNotBlank(req.getTitle()), "标题为空");

        AiMultiObjectTrackPo po = aiMultiObjectTrackDao.load(req.getId());
        permissionCheck(po, adminUserId);
        AiMultiObjectTrackPo update = new AiMultiObjectTrackPo();
        Integer status = 1;
        if (!po.getStatus().equals(status)) {
            update.setStatus(status);
            update.setPublishTime(new Date());
        }
        update.setTitle(req.getTitle());
        update.setDesc(req.getDesc());
        update.setId(po.getId());
        aiMultiObjectTrackDao.update(update);

        if (!aiUserInteractService.isSupport(businessEnum.getType())){
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

        AiMultiObjectTrackPo po = aiMultiObjectTrackDao.load(id);

        permissionCheck(po, adminUserId);

        Integer status = 0;
        if (po.getStatus().equals(status)) {
            return;
        }

        AiMultiObjectTrackPo update = new AiMultiObjectTrackPo();
        update.setStatus(status);
        update.setId(id);
        aiMultiObjectTrackDao.update(update);
        if (!aiUserInteractService.isSupport(businessEnum.getType())){
            return;
        }
        aiUserInterActDao.offline(String.valueOf(id), businessEnum.getType());
    }

    public void failRetry(Integer id) {

        AiMultiObjectTrackPo po = aiMultiObjectTrackDao.load(id);
        permissionCheck(po, null);

        Validate.isTrue(AiArtImageProcessEnum.FAIL.getStatus().equals(po.getProcessStatus()), "只有失败的才能重试");
        Validate.isTrue(po.getRetryTime() < 5, "重试次数过多");
        AiMultiObjectTrackPo update = new AiMultiObjectTrackPo();
        update.setProcessStatus(AiArtImageProcessEnum.PREPARE.getStatus());
        update.setId(id);
        update.setRetryTime(po.getRetryTime() + 1);
        aiMultiObjectTrackDao.update(update);
    }

    private void permissionCheck(AiMultiObjectTrackPo po, Integer adminUserId) {
        Validate.isTrue(po != null, "图片生成不存在");

        Integer userId = ThreadLocalHolder.getUserId();

        if (adminUserId != null && adminUserId > 0) {
            return;
        }
        Validate.isTrue(Objects.equals(userId, po.getUserId()), "用户没权限");
    }

    public void delete(Integer id, Integer adminUserId) {

        AiMultiObjectTrackPo load = aiMultiObjectTrackDao.load(id);

        if (load == null) {
            return;
        }


        permissionCheck(load, adminUserId);

        aiMultiObjectTrackDao.delete(id);

        if (!aiUserInteractService.isSupport(businessEnum.getType())){
            return;
        }
        aiUserInterActDao.delete(String.valueOf(id), businessEnum.getType());
    }

}
