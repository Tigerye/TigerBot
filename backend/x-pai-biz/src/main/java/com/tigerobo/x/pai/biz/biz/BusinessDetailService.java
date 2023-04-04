package com.tigerobo.x.pai.biz.biz;

import com.tigerobo.x.pai.api.enums.BusinessEnum;
import com.tigerobo.x.pai.api.vo.IBusinessDetailVo;
import com.tigerobo.x.pai.biz.ai.art.image.ArtImageService;
import com.tigerobo.x.pai.biz.ai.multi.object.track.MultiObjectTrackService;
import com.tigerobo.x.pai.biz.ai.photo.PhotoFixService;
import com.tigerobo.x.pai.biz.ai.spatio.action.SpatioActionService;
import com.tigerobo.x.pai.biz.ai.style.transfer.StyleTransferService;
import com.tigerobo.x.pai.biz.biz.blog.BlogChatService;
import com.tigerobo.x.pai.biz.biz.blog.BlogService;
import com.tigerobo.x.pai.biz.biz.service.WebDemandService;
import com.tigerobo.x.pai.biz.biz.service.WebTaskService;
import com.tigerobo.x.pai.biz.user.comment.UserCommentService;
import com.tigerobo.x.pai.biz.utils.ThreadUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class BusinessDetailService {

    @Autowired
    private ArtImageService artImageService;
    @Autowired
    private StyleTransferService styleTransferService;

    @Autowired
    private PhotoFixService photoFixService;

    @Autowired
    private SpatioActionService spatioActionService;
    @Autowired
    private MultiObjectTrackService multiObjectTrackService;

    @Autowired
    private WebDemandService webDemandService;

    @Autowired
    private WebTaskService webTaskService;

    @Autowired
    private BlogService blogService;

    @Autowired
    private BlogChatService blogChatService;

    @Autowired
    private UserCommentService userCommentService;

    public Map<BusinessReq, IBusinessDetailVo> getReqDetailMap(Collection<BusinessReq> businessReqs) {
        final Map<BusinessDetailService.BusinessReq,IBusinessDetailVo> detailMap = new ConcurrentHashMap<>();
        if (businessReqs.size()>0){
            ThreadUtil.detailPool.submit(()->{
                businessReqs.parallelStream().forEach(br->{
                    try {
                        final IBusinessDetailVo detail = getDetail(br.getType(), br.getId());
                        if(detail!=null){
                            detailMap.put(br,detail);
                        }
                    }catch (Exception ex){
                        log.error("biz:{}",br,ex);
                    }
                });
            }).join();
        }
        return detailMap;
    }


    public IBusinessDetailVo getDetail(Integer businessType, String bizId) {

        final IBusinessDetailFetchService detailFetchService = getDetailFetchService(businessType, bizId);

        if (detailFetchService == null) {
            return null;
        }
        return detailFetchService.getBusinessDetail(bizId);
    }

    private IBusinessDetailFetchService getDetailFetchService(Integer businessType, String bizId) {

        if (StringUtils.isBlank(bizId)) {
            return null;
        }
        final BusinessEnum businessEnum = BusinessEnum.getByType(businessType);
        if (businessEnum == null) {
            return null;
        }
        IBusinessDetailFetchService fetchService = null;
        switch (businessEnum) {
            case DEMAND:
                fetchService = webDemandService;
                break;
            case APP:
                fetchService = webTaskService;
                break;
            case BLOG: {
                if (bizId.length()>18){
                    fetchService = blogChatService;
                }else {
                    fetchService = blogService;
                }
            }
            break;
            case COMMENT:
                fetchService = userCommentService;
                break;
            case ART_IMAGE:
                fetchService = artImageService;
                break;
            case PHOTO_FIX:
                fetchService = photoFixService;
                break;
            case STYLE_TRANSFER:
                fetchService = styleTransferService;
                break;
            case SPATIO_ACTION:
                fetchService = spatioActionService;
                break;
            case MULTI_OBJECT_TRACK:
                fetchService = multiObjectTrackService;
                break;
        }
        return fetchService;
    }


    @Data
    @AllArgsConstructor
    public static class BusinessReq{
        String id;
        Integer type;

        @Override
        public int hashCode() {
            if (id == null){
                return 0;
            }
            return id.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null){
                return false;
            }
            if (!(obj instanceof BusinessReq)){
                return false;
            }
            BusinessReq req = (BusinessReq)obj;

            return id.equals(req.id)&& Objects.equals(type,req.type);
        }
    }
}
