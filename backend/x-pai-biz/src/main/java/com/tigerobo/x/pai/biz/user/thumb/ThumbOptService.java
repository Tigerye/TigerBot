package com.tigerobo.x.pai.biz.user.thumb;

import com.github.pagehelper.Page;
import com.tigerobo.x.pai.api.enums.BusinessEnum;
import com.tigerobo.x.pai.api.enums.ThumbAction;
import com.tigerobo.x.pai.api.vo.IBusinessDetailVo;
import com.tigerobo.x.pai.api.vo.user.thumb.ThumbReq;
import com.tigerobo.x.pai.biz.ai.art.image.ArtImageOperateService;
import com.tigerobo.x.pai.biz.biz.BusinessDetailService;
import com.tigerobo.x.pai.biz.cache.RedisCacheService;
import com.tigerobo.x.pai.biz.hot.HotBusinessService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.dal.ai.dao.AiArtImageDao;
import com.tigerobo.x.pai.dal.biz.dao.user.UserThumbDao;
import com.tigerobo.x.pai.dal.biz.entity.BizCountPo;
import com.tigerobo.x.pai.dal.biz.entity.user.UserThumbPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
public class ThumbOptService {

    @Autowired
    private UserThumbDao userThumbUpDao;
    @Autowired
    private RedisCacheService redisCacheService;


    @Autowired
    private HotBusinessService hotBusinessService;

    @Autowired
    private BusinessDetailService businessDetailService;
    @Autowired
    private AiArtImageDao aiArtImageDao;

    public void thumbUp(ThumbReq req) {
        req.setActionType(ThumbAction.THUMB_UP.getType());
        thumb(req);
    }

    public void thumbDown(ThumbReq req) {
        req.setActionType(ThumbAction.THUMB_DOWN.getType());
        thumb(req);
    }

    private void thumb(ThumbReq req) {
        Integer userId = ThreadLocalHolder.getUserId();

        String bizId = req.getBizId();
        Integer bizType = req.getBizType();
        if (StringUtils.isEmpty(bizId) && bizType != null) {
            return;
        }
        final IBusinessDetailVo detail = businessDetailService.getDetail(bizType, bizId);
        UserThumbPo userThumbUpPo = userThumbUpDao.get(userId, bizId, bizType);
        if (userThumbUpPo != null) {
            if (userThumbUpPo.getActionType().equals(req.getActionType())){
                return;
            }
            userThumbUpDao.deleteByBiz(bizType,bizId);
        }
        UserThumbPo po = new UserThumbPo();
        po.setUserId(userId);
        po.setBizId(bizId);
        po.setBizType(bizType);

        if (detail!=null){
            final BusinessEnum byType = BusinessEnum.getByType(bizType);
            if (byType!=null&&byType.isCanNotify()){
                po.setNotifyUserId(detail.getUserId());
            }
        }

        po.setActionType(req.getActionType());
        userThumbUpDao.add(po);
//        incrThumb(bizId, bizType,actionType);
        if (BusinessEnum.ART_IMAGE.getType().equals(bizType)){
            if (req.getActionType().equals(0)){
                aiArtImageDao.incrThumb(Integer.parseInt(bizId));
            }
        }
        reloadThumbUp(bizId, bizType);
    }

    public void cancelThumbUp(ThumbReq req) {
        Integer userId = ThreadLocalHolder.getUserId();

        String bizId = req.getBizId();
        Integer bizType = req.getBizType();
        if (StringUtils.isEmpty(bizId) && bizType != null) {
            return;
        }

        if (StringUtils.isEmpty(bizId)) {
            return;
        }
        UserThumbPo userThumbUpPo = userThumbUpDao.get(userId, bizId, bizType);
        if (userThumbUpPo != null) {
            userThumbUpDao.deleteByBiz(bizType,bizId);
            if (BusinessEnum.ART_IMAGE.getType().equals(bizType)){
                if (req.getActionType().equals(0)){
                    aiArtImageDao.decrThumb(Integer.parseInt(bizId));
                }
            }
        }

        reloadThumbUp(bizId, bizType);
    }

    int reloadThumbUp(String bizId, Integer type) {

        String key = getKey(bizId, type,0);
        String downkey = getKey(bizId, type,1);
        int count = userThumbUpDao.count(bizId, type,0);
        int downcount = userThumbUpDao.count(bizId, type,1);
        redisCacheService.set(key, String.valueOf(count));
//        if (count==0){
//            redisCacheService.expire(key,300);
//        }
        redisCacheService.set(downkey, String.valueOf(downcount));
//        if (downcount==0){
//            redisCacheService.expire(downkey,300);
//        }
        hotBusinessService.setBizScore(bizId, type, (double) count);
        return count;
    }


    public void reloadBizCache(Integer type){

        final List<BizCountPo> all = userThumbUpDao.getBizAllGroupCount(type, 1, 1);
        if (all == null||all.isEmpty()){
            return;
        }
        Page page = (Page)all;
        final long total = page.getTotal();
        int pageSize = 50;
        int pageTotal = (int)((total%pageSize>0?1:0)+total%pageSize);

        for (int i = 1; i <=pageTotal; i++) {
            final List<BizCountPo> batch = userThumbUpDao.getBizAllGroupCount(type, i, pageSize);

            if (batch!=null&&batch.size()>0){
                batch.parallelStream().forEach(countPo->{
                    final String key = getKey(countPo.getBiz_id(), type, 0);
                    final Integer n = countPo.getN();
                    redisCacheService.set(key, n.toString());
                });
            }
        }

    }

    static String getKey(String id, Integer type,Integer actionType) {
        return "pai:thumb:biz:" + type + ":" + id+":"+actionType;
    }

}
