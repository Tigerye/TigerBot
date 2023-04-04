package com.tigerobo.x.pai.biz.ai.art.image;

import com.tigerobo.x.pai.api.ai.enums.AiArtImageProcessEnum;
import com.tigerobo.x.pai.api.ai.req.AiArtImageGenerateReq;
import com.tigerobo.x.pai.api.entity.Pair;
import com.tigerobo.x.pai.api.enums.ArtImageType;
import com.tigerobo.x.pai.biz.utils.DownloadImageUtil;
import com.tigerobo.x.pai.biz.utils.ImageUtil;
import com.tigerobo.x.pai.biz.utils.TimeUtil;
import com.tigerobo.x.pai.dal.ai.dao.AiArtImageDao;
import com.tigerobo.x.pai.dal.ai.entity.AiArtImagePo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j
@Service
public class ArtImageAiService {

    @Autowired
    private AiArtImageDao aiArtImageDao;
    @Value("${pai.ai.artImage.progress:100}")
    Integer totalProgress;

    String workPath = "/mnt/xpai/application/";
    @Value("${pai.env.aml.prefix}")
    String prefix;

    @Autowired
    private ArtImageUserReqService artImageUserReqService;


    @Autowired
    private ArtImageLakeService artImageLakeService;

    public Long userReqProduceImage(AiArtImageGenerateReq req) {
        return artImageUserReqService.userReqProduceImage(req);
    }

    @Transactional(value = "paiTm")
    public void consume(AiArtImagePo reqPo) {
        String imageFilePath = downloadImageUrl(reqPo);
        String itemWorkPath = getWorkPath(reqPo.getReqId());

        Integer currentTotalProgress = reqPo.getTotalProgress();
        if (currentTotalProgress == null || currentTotalProgress < 100) {
            currentTotalProgress = totalProgress;
        }

        AiArtImagePo po = getProcessReqAiPo(reqPo, itemWorkPath, currentTotalProgress);

        if (ArtImageType.STABLE.getType().equals(reqPo.getStyleType())){
            artImageLakeService.reqStable(reqPo, imageFilePath, itemWorkPath);
        }else if (ArtImageType.DISCO.getType().equals(reqPo.getStyleType())){
            artImageLakeService.reqDisco(reqPo, imageFilePath, itemWorkPath);
        }else {
            throw new IllegalArgumentException("styleType不支持");
        }
        aiArtImageDao.update(po);
    }

    private AiArtImagePo getProcessReqAiPo(AiArtImagePo reqPo, String itemWorkPath, Integer currentTotalProgress) {
        AiArtImagePo po = new AiArtImagePo();
        po.setWorkPath(itemWorkPath);
        po.setProgress(0);
        po.setTotalProgress(currentTotalProgress);
        po.setProcessStatus(AiArtImageProcessEnum.ON_PROCESS.getStatus());
        po.setReqTime(new Date());
        po.setId(reqPo.getId());
        return po;
    }

    private String downloadImageUrl(AiArtImagePo reqPo) {

        if (StringUtils.isBlank(reqPo.getInputImage())) {
            return null;
        }
        String imageFilePath = DownloadImageUtil.getTmpImgFile(reqPo.getInputImage());
        Pair<Integer, Integer> imageAround = ImageUtil.getImageAround(imageFilePath);
        if (imageAround != null) {
            Integer width = imageAround.getV1();
            Integer height = imageAround.getV2();
            Validate.isTrue(width<10000&&height<10000,"图片太大，尺寸不支持");
        }
        return imageFilePath;
    }

    private String getWorkPath(Long reqId) {
        int day = TimeUtil.getDayValue(new Date());

        String itemWorkPath = workPath;
        if (!StringUtils.isBlank(prefix)) {
            itemWorkPath += prefix + "/";
        }
        itemWorkPath += day + "/" + reqId + "/";
        return itemWorkPath;
    }

}
