package com.tigerobo.x.pai.biz.ai.photo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tigerobo.x.pai.api.ai.enums.AiArtImageProcessEnum;
import com.tigerobo.x.pai.api.ai.req.ArtImageChooseMainReq;
import com.tigerobo.x.pai.api.ai.req.ArtImageOnlineReq;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.dal.ai.dao.AiArtImageDao;
import com.tigerobo.x.pai.dal.ai.dao.AiPhotoFixDao;
import com.tigerobo.x.pai.dal.ai.entity.AiArtImagePo;
import com.tigerobo.x.pai.dal.ai.entity.AiPhotoFixPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

@Slf4j
@Service
public class PhotoFixOperateService {

    @Autowired
    private AiPhotoFixDao aiPhotoFixDao;

//
//    public void failRetry(Integer id) {
//
//        AiArtImagePo po = aiArtImageDao.load(id);
//        permissionCheck(po);
//
//        Validate.isTrue(AiArtImageProcessEnum.FAIL.getStatus().equals(po.getProcessStatus()),"只有失败的才能重试");
//        Validate.isTrue(po.getRetryTime()<5,"重试次数过多");
//        AiArtImagePo update = new AiArtImagePo();
//        update.setProcessStatus(AiArtImageProcessEnum.PREPARE.getStatus());
//        update.setId(id);
//        update.setRetryTime(po.getRetryTime()+1);
//        aiArtImageDao.update(update);
//    }

    private void permissionCheck(AiPhotoFixPo po) {
        Validate.isTrue(po != null, "图片生成不存在");

        Integer userId = ThreadLocalHolder.getUserId();
        Validate.isTrue(Objects.equals(userId, po.getUserId()), "用户没权限");
    }

    public void delete(Integer id) {

        AiPhotoFixPo load = aiPhotoFixDao.load(id);

        Validate.isTrue(load!=null,"数据不存在");

        permissionCheck(load);
        Integer userId = ThreadLocalHolder.getUserId();

        Validate.isTrue(Objects.equals(load.getUserId(), userId), "没权限");

        aiPhotoFixDao.delete(id);
    }
}
