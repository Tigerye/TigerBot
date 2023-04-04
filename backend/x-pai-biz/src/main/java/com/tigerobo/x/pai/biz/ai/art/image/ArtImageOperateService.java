package com.tigerobo.x.pai.biz.ai.art.image;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tigerobo.x.pai.api.ai.enums.AiArtImageProcessEnum;
import com.tigerobo.x.pai.api.ai.req.ArtImageChooseMainReq;
import com.tigerobo.x.pai.api.ai.req.ArtImageOnlineReq;
import com.tigerobo.x.pai.api.ai.req.art.image.ArtImageHideReq;
import com.tigerobo.x.pai.api.enums.BusinessEnum;
import com.tigerobo.x.pai.api.enums.ProcessStatusEnum;
import com.tigerobo.x.pai.biz.auth.RoleService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.dal.ai.dao.AiArtImageDao;
import com.tigerobo.x.pai.dal.ai.dao.AiUserInterActDao;
import com.tigerobo.x.pai.dal.ai.entity.AiArtImagePo;
import com.tigerobo.x.pai.dal.ai.entity.AiUserInteractPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Objects;

@Slf4j
@Service
public class ArtImageOperateService {

    @Autowired
    private AiArtImageDao aiArtImageDao;

    @Autowired
    private AiUserInterActDao aiUserInterActDao;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ArtImageResultService artImageResultService;

    @Autowired
    private ArtImageAuditPassService artImageAuditPassService;

    public void hide(ArtImageHideReq req){

        Validate.isTrue(req.getId() != null, "参数为空");
        Validate.isTrue(req.getHide() != null, "未设定隐藏");

        AiArtImagePo po = aiArtImageDao.load(req.getId());

        permissionCheck(po, null);

        if (req.getHide().equals(po.getHide())){
            return;
        }
        AiArtImagePo update = new AiArtImagePo();
        update.setId(req.getId());
        update.setHide(req.getHide());

        aiArtImageDao.update(update);
    }


    public void chooseMain(ArtImageChooseMainReq req) {

        Validate.isTrue(req.getId() != null, "参数为空");
        Validate.isTrue(req.getProgress() != null, "未选择图片");

        Integer userId = ThreadLocalHolder.getUserId();
        Validate.isTrue(userId != null, "用户不正确");


        AiArtImagePo po = aiArtImageDao.load(req.getId());
        permissionCheck(po, null);

        Integer processStatus = po.getProcessStatus();
        Validate.isTrue(AiArtImageProcessEnum.SUCCESS.getStatus().equals(processStatus), "图片未处理成功");

        String progressImages = po.getProgressImages();
        Validate.isTrue(StringUtils.isNotBlank(progressImages), "设置图片不存在");

        JSONObject jsonObject = JSON.parseObject(progressImages);
        String img = jsonObject.getString(String.valueOf(req.getProgress()));
        Validate.isTrue(StringUtils.isNotBlank(img), "选择图片不存在");

        AiArtImagePo update = new AiArtImagePo();
        update.setOutputImage(img);
        update.setId(po.getId());
        update.setImageProgress(req.getProgress());
        aiArtImageDao.update(update);
    }

    public void online(ArtImageOnlineReq req, Integer adminId) {

        Validate.isTrue(req.getId() != null, "参数为空");
//        Validate.isTrue(StringUtils.isNotBlank(req.getTitle()), "标题为空");

        Integer userId = ThreadLocalHolder.getUserId();
        AiArtImagePo po = aiArtImageDao.load(req.getId());
        permissionCheck(po, adminId);
        AiArtImagePo update = new AiArtImagePo();
        Integer status = 1;
        if (!po.getStatus().equals(status)) {
            update.setStatus(status);
            if (po.getPublishTime()==null){
                update.setPublishTime(new Date());
            }
        }
        update.setTitle(req.getTitle());
        update.setDesc(req.getDesc());
        update.setId(po.getId());
        if (adminId!=null){
            update.setOperator(adminId.toString());
        }
        aiArtImageDao.update(update);


        AiUserInteractPo aiUserInteractPo = new AiUserInteractPo();
        boolean needUpdate =false;
        if (update.getStatus()!=null){
            aiUserInteractPo.setStatus(update.getStatus());
            needUpdate = true;
        }
        aiUserInteractPo.setPublishTime(update.getPublishTime());
        if (StringUtils.isNotBlank(update.getTitle())){
            aiUserInteractPo.setTitle(update.getTitle());

            needUpdate = true;
        }


        if (needUpdate) {

            aiUserInterActDao.online(String.valueOf(req.getId()),
                    BusinessEnum.ART_IMAGE.getType(),aiUserInteractPo);
        }

    }

    public void offline(Integer id, Integer adminId) {

        AiArtImagePo po = aiArtImageDao.load(id);

        permissionCheck(po, adminId);

        Integer status = 0;
        if (po.getStatus().equals(status)) {
            return;
        }

        AiArtImagePo update = new AiArtImagePo();
        update.setStatus(status);
        update.setId(id);
        if (adminId!=null){
            update.setOperator(adminId.toString());
        }
        aiArtImageDao.update(update);

        aiUserInterActDao.offline(String.valueOf(id), BusinessEnum.ART_IMAGE.getType());
    }

    public void auditPass(Integer id,Integer adminId){
        Validate.isTrue(adminCheck(adminId),"没有权限");
        AiArtImagePo po = aiArtImageDao.load(id);
        final Integer processStatus = po.getProcessStatus();
        Validate.isTrue(ProcessStatusEnum.WAIT_AUDIT.getStatus().equals(processStatus),"当前状态不是待审核");

        AiArtImagePo update = new AiArtImagePo();
        update.setProcessStatus(ProcessStatusEnum.ON_QUEUE.getStatus());
        update.setId(id);
        if (adminId!=null){
            update.setOperator(adminId.toString());
        }

        artImageAuditPassService.addWord(po.getText());
        aiArtImageDao.update(update);
    }

    public void auditRefuse(Integer id,String msg,Integer adminId){

        AiArtImagePo po = aiArtImageDao.load(id);

        Validate.isTrue(adminCheck(adminId),"没有权限");

        final Integer processStatus = po.getProcessStatus();
        Validate.isTrue(ProcessStatusEnum.WAIT_AUDIT.getStatus().equals(processStatus),"当前状态不是待审核");

        if (StringUtils.isBlank(msg)){
            msg = "审核不通过";
        }

        artImageResultService.updateFail(po,msg,adminId);
    }


    public void failRetry(Integer id) {

        AiArtImagePo po = aiArtImageDao.load(id);
        permissionCheck(po, null);

        Validate.isTrue(AiArtImageProcessEnum.FAIL.getStatus().equals(po.getProcessStatus()), "只有失败的才能重试");
        Validate.isTrue(po.getRetryTime() < 5, "重试次数过多");

        final String msg = po.getMsg();
        AiArtImagePo update = new AiArtImagePo();

        boolean ignore = false;

        if (StringUtils.isNotBlank(po.getOperator())){
            ignore = true;
        }else if (StringUtils.isNotBlank(msg)){
            ignore=msg.startsWith("内容不合法:")
                    ||msg.startsWith("违规")
                    ||msg.contains("审核");
        }
        if (ignore){
            update.setProcessStatus(AiArtImageProcessEnum.FAIL.getStatus());
        }else {
            update.setProcessStatus(AiArtImageProcessEnum.PREPARE.getStatus());
        }

        update.setId(id);
        update.setRetryTime(po.getRetryTime() + 1);
        aiArtImageDao.update(update);
    }


    private void permissionCheck(AiArtImagePo po, Integer adminUserId) {
        Validate.isTrue(po!=null,"生成图不存在");
        if (adminCheck(adminUserId)) {
            return;
        }
        Integer userId = ThreadLocalHolder.getUserId();
        Validate.isTrue(Objects.equals(userId, po.getUserId()), "用户没权限");
    }

    private boolean adminCheck(Integer adminUserId) {
        if (adminUserId != null && adminUserId > 0) {
            return true;
        }
        if (roleService.isAdmin()){
            return true;
        }
        return false;
    }

    public void delete(Integer id, Integer adminUserId) {

        AiArtImagePo load = aiArtImageDao.load(id);

        if (load == null) {
            return;
        }


        permissionCheck(load, adminUserId);

        aiArtImageDao.delete(id);

        aiUserInterActDao.delete(String.valueOf(id), BusinessEnum.ART_IMAGE.getType());
    }
}
