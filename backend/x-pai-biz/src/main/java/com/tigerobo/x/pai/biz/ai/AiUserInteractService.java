package com.tigerobo.x.pai.biz.ai;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.tigerobo.x.pai.api.ai.base.IAiUserInteract;
import com.tigerobo.x.pai.api.ai.req.ArtImageOnlineReq;
import com.tigerobo.x.pai.api.ai.req.ArtImagePublicPageReq;
import com.tigerobo.x.pai.api.ai.req.interact.AiOnlineReq;
import com.tigerobo.x.pai.api.ai.req.interact.UserInteractPublicPageReq;
import com.tigerobo.x.pai.api.ai.vo.AiArtImageVo;
import com.tigerobo.x.pai.api.enums.BusinessEnum;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.biz.ai.art.image.ArtImageOperateService;
import com.tigerobo.x.pai.biz.ai.art.image.ArtImageService;
import com.tigerobo.x.pai.biz.ai.spatio.action.AiSpatioActionOperateService;
import com.tigerobo.x.pai.biz.ai.spatio.action.SpatioActionService;
import com.tigerobo.x.pai.biz.ai.style.transfer.AiStyleTransferOperateService;
import com.tigerobo.x.pai.biz.ai.style.transfer.StyleTransferService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.dal.ai.dao.AiUserInterActDao;
import com.tigerobo.x.pai.dal.ai.entity.AiUserInteractPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AiUserInteractService {


    @Autowired
    private AiUserInterActDao aiUserInterActDao;
    @Autowired
    private ArtImageService artImageService;
    @Autowired
    private StyleTransferService styleTransferService;

    @Autowired
    private ArtImageOperateService artImageOperateService;

    @Autowired
    private AiStyleTransferOperateService aiStyleTransferOperateService;

    @Autowired
    private SpatioActionService spatioActionService;
    @Autowired
    private AiSpatioActionOperateService aiSpatioActionOperateService;

    List<BusinessEnum> supportList = Arrays.asList(BusinessEnum.ART_IMAGE
            ,BusinessEnum.STYLE_TRANSFER
    );

    public PageVo<? extends IAiUserInteract> getNewPublishList(UserInteractPublicPageReq req) {

        String tabType = req.getTabType();
        Integer bizType = req.getBizType();

        if (bizType!=null&&!isSupport(bizType)){
            return new PageVo<>();
        }
        if ("hot".equalsIgnoreCase(tabType)){
            if (bizType == null){
                bizType = BusinessEnum.ART_IMAGE.getType();
            }
            ArtImagePublicPageReq artReq = new ArtImagePublicPageReq();
            BeanUtils.copyProperties(req,artReq);
            if (BusinessEnum.ART_IMAGE.getType().equals(bizType)){

                final PageVo<AiArtImageVo> hotList = artImageService.getHotList(artReq);
                return hotList;
            }else if (BusinessEnum.STYLE_TRANSFER.getType().equals(bizType)){
                return styleTransferService.getHotList(artReq);
            }else if (BusinessEnum.SPATIO_ACTION.getType().equals(bizType)){
                return spatioActionService.getHotList(artReq);
            }
            throw new IllegalArgumentException("业务类型不支持");
        }

        final List<Integer> supportTypes = supportList.stream().map(bus -> bus.getType()).collect(Collectors.toList());
        final Page<AiUserInteractPo> pageList = aiUserInterActDao.getPublishListQuery(req,supportTypes);
        PageVo<IAiUserInteract> pageVo = new PageVo<>();

        pageVo.setTotal(pageList.getTotal());
        pageVo.setPageSize(req.getPageSize());
        pageVo.setPageNum(req.getPageNum());

        final List<IAiUserInteract> list = getList(pageList);
        pageVo.setList(list);
        return pageVo;
    }

    public PageVo<IAiUserInteract> getMyList(UserInteractPublicPageReq req) {
        Integer userId = ThreadLocalHolder.getUserId();
        final List<Integer> supportTypes = supportList.stream().map(bus -> bus.getType()).collect(Collectors.toList());

        Page<AiUserInteractPo> userPage = aiUserInterActDao.getUserPage(userId, req,supportTypes);

        PageVo<IAiUserInteract> pageVo = new PageVo<>();

        pageVo.setTotal(userPage.getTotal());
        pageVo.setPageSize(req.getPageSize());
        pageVo.setPageNum(req.getPageNum());
        final List<IAiUserInteract> list = getList(userPage);

        pageVo.setList(list);
        return pageVo;
    }

    private List<IAiUserInteract> getList(Page<AiUserInteractPo> userPage) {
        if (CollectionUtils.isEmpty(userPage)){
            return new ArrayList<>();
        }
        final List<IAiUserInteract> list = userPage.parallelStream().map(interactPo -> {
            return build(interactPo);
        }).filter(Objects::nonNull).collect(Collectors.toList());
        return list;
    }

    IAiUserInteract build(AiUserInteractPo interactPo) {
        final int bizType = interactPo.getBizType();
        if (!isSupport(bizType)){
            return null;
        }
        final String bizId = interactPo.getBizId();
        try {
            if (BusinessEnum.ART_IMAGE.getType().equals(bizType)) {
                final int id = Integer.parseInt(bizId);
                return artImageService.getDetail(id);
            } else if (BusinessEnum.STYLE_TRANSFER.getType().equals(bizType)) {
                return styleTransferService.getDetail(Integer.parseInt(bizId));
            } else if (BusinessEnum.SPATIO_ACTION.getType().equals(bizType)) {
                return spatioActionService.getDetail(Integer.parseInt(bizId));
            } else {
                log.warn("bizType:{},不支持", bizType);
            }
        }catch (Exception ex){
            log.error("interAct:bizId:{},type:{}",bizId,bizType,ex);
        }
        return null;
    }


    public IAiUserInteract getDetail( String id,Integer bizType) {
        Validate.isTrue(!StringUtils.isEmpty(id),"id为空");
        Validate.isTrue(bizType!=null,"业务类型为空");

        if (BusinessEnum.ART_IMAGE.getType().equals(bizType)){
            return artImageService.getById(Integer.parseInt(id));
        }else if (BusinessEnum.STYLE_TRANSFER.getType().equals(bizType)){
            return styleTransferService.getById(Integer.parseInt(id));
        }else if (BusinessEnum.SPATIO_ACTION.getType().equals(bizType)){
            return spatioActionService.getById(Integer.parseInt(id));
        }
        throw new IllegalArgumentException("业务类型不支持");
    }

    public void offline(String bizId, Integer bizType) {
        Validate.isTrue(!StringUtils.isEmpty(bizId),"id为空");
        Validate.isTrue(bizType!=null,"业务类型为空");

        if (BusinessEnum.ART_IMAGE.getType().equals(bizType)){
            artImageOperateService.offline(Integer.parseInt(bizId),null);
        }else if (BusinessEnum.STYLE_TRANSFER.getType().equals(bizType)){
            aiStyleTransferOperateService.offline(Integer.parseInt(bizId),null);
        }else if (BusinessEnum.SPATIO_ACTION.getType().equals(bizType)){
            aiSpatioActionOperateService.offline(Integer.parseInt(bizId),null);
        }

    }

    public void delete(String bizId, Integer bizType) {
        Validate.isTrue(!StringUtils.isEmpty(bizId),"id为空");
        Validate.isTrue(bizType!=null,"业务类型为空");

        if (BusinessEnum.ART_IMAGE.getType().equals(bizType)){
            artImageOperateService.delete(Integer.parseInt(bizId),null);
        }else if (BusinessEnum.STYLE_TRANSFER.getType().equals(bizType)){
            aiStyleTransferOperateService.delete(Integer.parseInt(bizId),null);
        }else if (BusinessEnum.SPATIO_ACTION.getType().equals(bizType)){
            aiSpatioActionOperateService.delete(Integer.parseInt(bizId),null);
        }
    }

    public void online(AiOnlineReq req) {
        Validate.isTrue(!StringUtils.isEmpty(req.getId()),"id为空");
        Validate.isTrue(req.getBizType()!=null,"业务类型为空");

        final String bizId = req.getId();
        final Integer bizType = req.getBizType();
        if (BusinessEnum.ART_IMAGE.getType().equals(bizType)){
            final ArtImageOnlineReq artImageOnlineReq = JSON.parseObject(JSON.toJSONString(req), ArtImageOnlineReq.class);
            artImageOperateService.online(artImageOnlineReq,null);
        }else if (BusinessEnum.STYLE_TRANSFER.getType().equals(bizType)){

            final ArtImageOnlineReq artImageOnlineReq = JSON.parseObject(JSON.toJSONString(req), ArtImageOnlineReq.class);
            aiStyleTransferOperateService.online(artImageOnlineReq,null);
        }else if (BusinessEnum.SPATIO_ACTION.getType().equals(bizType)){
            final ArtImageOnlineReq artImageOnlineReq = JSON.parseObject(JSON.toJSONString(req), ArtImageOnlineReq.class);

            aiSpatioActionOperateService.online(artImageOnlineReq,null);
        }

    }

    public void failRetry(String bizId, Integer bizType) {
        Validate.isTrue(!StringUtils.isEmpty(bizId),"id为空");
        Validate.isTrue(bizType!=null,"业务类型为空");

        if (BusinessEnum.ART_IMAGE.getType().equals(bizType)){
            artImageOperateService.failRetry(Integer.parseInt(bizId));
        }else if (BusinessEnum.STYLE_TRANSFER.getType().equals(bizType)){
            aiStyleTransferOperateService.failRetry(Integer.parseInt(bizId));
        }else if (BusinessEnum.SPATIO_ACTION.getType().equals(bizType)){
            aiSpatioActionOperateService.failRetry(Integer.parseInt(bizId));
        }


    }


    public boolean isSupport(Integer bizType){
        final BusinessEnum businessEnum = BusinessEnum.getByType(bizType);

        return businessEnum!=null&&supportList.contains(businessEnum);

    }
}
