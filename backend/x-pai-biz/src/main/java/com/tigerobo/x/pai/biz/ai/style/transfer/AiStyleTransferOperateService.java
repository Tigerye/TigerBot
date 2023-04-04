package com.tigerobo.x.pai.biz.ai.style.transfer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tigerobo.x.pai.api.ai.enums.AiArtImageProcessEnum;
import com.tigerobo.x.pai.api.ai.req.ArtImageChooseMainReq;
import com.tigerobo.x.pai.api.ai.req.ArtImageOnlineReq;
import com.tigerobo.x.pai.api.enums.BusinessEnum;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.dal.ai.dao.AiStyleTransferDao;
import com.tigerobo.x.pai.dal.ai.dao.AiUserInterActDao;
import com.tigerobo.x.pai.dal.ai.entity.AiStyleTransferPo;
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
public class AiStyleTransferOperateService {

    @Autowired
    private AiStyleTransferDao aiStyleTransferDao;

    @Autowired
    private AiUserInterActDao aiUserInterActDao;

    public void chooseMain(ArtImageChooseMainReq req) {

        Validate.isTrue(req.getId() != null, "参数为空");
        Validate.isTrue(req.getProgress() != null, "未选择图片");

        Integer userId = ThreadLocalHolder.getUserId();
        Validate.isTrue(userId != null, "用户不正确");


        AiStyleTransferPo po = aiStyleTransferDao.load(req.getId());
        permissionCheck(po, null);

        Integer processStatus = po.getProcessStatus();
        Validate.isTrue(AiArtImageProcessEnum.SUCCESS.getStatus().equals(processStatus), "图片未处理成功");

        String progressImages = po.getProgressImages();
        Validate.isTrue(StringUtils.isNotBlank(progressImages), "设置图片不存在");

        JSONObject jsonObject = JSON.parseObject(progressImages);
        String img = jsonObject.getString(String.valueOf(req.getProgress()));
        Validate.isTrue(StringUtils.isNotBlank(img), "选择图片不存在");

        AiStyleTransferPo update = new AiStyleTransferPo();
        update.setOutputImage(img);
        update.setId(po.getId());
        update.setImageProgress(req.getProgress());
        aiStyleTransferDao.update(update);
    }

    public void online(ArtImageOnlineReq req, Integer adminUserId) {

        Validate.isTrue(req.getId() != null, "参数为空");
//        Validate.isTrue(StringUtils.isNotBlank(req.getTitle()), "标题为空");

        AiStyleTransferPo po = aiStyleTransferDao.load(req.getId());
        permissionCheck(po, adminUserId);
        AiStyleTransferPo update = new AiStyleTransferPo();
        Integer status = 1;
        if (!po.getStatus().equals(status)) {
            update.setStatus(status);
            update.setPublishTime(new Date());
        }
        update.setTitle(req.getTitle());
        update.setDesc(req.getDesc());
        update.setId(po.getId());
        aiStyleTransferDao.update(update);
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
                    BusinessEnum.STYLE_TRANSFER.getType(), aiUserInteractPo);
        }

    }

    public void offline(Integer id, Integer adminUserId) {

        AiStyleTransferPo po = aiStyleTransferDao.load(id);

        permissionCheck(po, adminUserId);

        Integer status = 0;
        if (po.getStatus().equals(status)) {
            return;
        }

        AiStyleTransferPo update = new AiStyleTransferPo();
        update.setStatus(status);
        update.setId(id);
        aiStyleTransferDao.update(update);

        aiUserInterActDao.offline(String.valueOf(id), BusinessEnum.STYLE_TRANSFER.getType());
    }

    public void failRetry(Integer id) {

        AiStyleTransferPo po = aiStyleTransferDao.load(id);
        permissionCheck(po, null);

        Validate.isTrue(AiArtImageProcessEnum.FAIL.getStatus().equals(po.getProcessStatus()), "只有失败的才能重试");
        Validate.isTrue(po.getRetryTime() < 5, "重试次数过多");
        AiStyleTransferPo update = new AiStyleTransferPo();
        update.setProcessStatus(AiArtImageProcessEnum.PREPARE.getStatus());
        update.setId(id);
        update.setRetryTime(po.getRetryTime() + 1);
        aiStyleTransferDao.update(update);
    }

    private void permissionCheck(AiStyleTransferPo po, Integer adminUserId) {
        Validate.isTrue(po != null, "图片生成不存在");

        Integer userId = ThreadLocalHolder.getUserId();

        if (adminUserId != null && adminUserId > 0) {
            return;
        }
        Validate.isTrue(Objects.equals(userId, po.getUserId()), "用户没权限");
    }

    public void delete(Integer id, Integer adminUserId) {

        AiStyleTransferPo load = aiStyleTransferDao.load(id);

        if (load == null) {
            return;
        }


        permissionCheck(load, adminUserId);

        aiStyleTransferDao.delete(id);
        aiUserInterActDao.delete(String.valueOf(id), BusinessEnum.STYLE_TRANSFER.getType());
    }
}
