package com.tigerobo.x.pai.biz.ai.art.image;

import com.tigerobo.x.pai.api.ai.enums.AiArtImageProcessEnum;
import com.tigerobo.x.pai.dal.ai.dao.AiArtImageDao;
import com.tigerobo.x.pai.dal.ai.entity.AiArtImagePo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
@Service
public class ArtImageTaskService {
    @Autowired
    private ArtImageAssignService artImageAssignService;

    @Autowired
    private ArtImageAiService artImageAiService;
    @Autowired
    private AiArtImageDao aiArtImageDao;


    @Autowired
    private ArtImageResultService artImageResultService;

    public void dealPrepareTask(boolean test) {

        final List<AiArtImagePo> waitDealList = artImageAssignService.getWaitTaskList(test,false);
        doDealList(waitDealList);
    }

    private void doDealList(List<AiArtImagePo> waitDealList) {
        if (CollectionUtils.isEmpty(waitDealList)) {
            return;
        }
        for (AiArtImagePo po : waitDealList) {
            try {
                artImageAiService.consume(po);
            } catch (IllegalArgumentException ex) {
                log.error("dealPrepareTask,id:{}", po.getId(), ex);
                artImageResultService.updateFail(po, ex.getMessage());
            } catch (Exception ex) {
                log.error("dealPrepareTask,id:{}", po.getId(), ex);
                artImageResultService.updateFail(po, "处理异常");
            }
        }
    }

    public void dealTaskResult() {
        int size = 100;
        List<AiArtImagePo> waitDealList = aiArtImageDao.getDealList(AiArtImageProcessEnum.ON_PROCESS.getStatus(),size);
        if (CollectionUtils.isEmpty(waitDealList)) {
            return;
        }
        for (AiArtImagePo po : waitDealList) {
            try {
                artImageResultService.updateResult(po);
            } catch (IllegalArgumentException ex) {
                log.error("id:{}", po.getId(), ex);
                artImageResultService.updateFail(po, ex.getMessage());
            } catch (Exception ex) {
                log.error("id:{}", po.getId(), ex);
                artImageResultService.updateFail(po, "处理异常");
            }
        }
    }


}
