package com.tigerobo.x.pai.biz.ai.photo;

import com.github.pagehelper.Page;
import com.tigerobo.x.pai.api.ai.enums.AiArtImageProcessEnum;
import com.tigerobo.x.pai.api.ai.req.photo.PhotoFixPageReq;
import com.tigerobo.x.pai.api.ai.vo.PhotoFixVo;
import com.tigerobo.x.pai.api.auth.entity.Role;
import com.tigerobo.x.pai.api.auth.entity.User;
import com.tigerobo.x.pai.api.auth.service.UserService;
import com.tigerobo.x.pai.api.enums.BusinessEnum;
import com.tigerobo.x.pai.api.vo.IBusinessDetailVo;
import com.tigerobo.x.pai.api.vo.IdReqVo;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.biz.interact.InteractVo;
import com.tigerobo.x.pai.biz.ai.convert.PhotoFixConvert;
import com.tigerobo.x.pai.biz.auth.InteractionService;
import com.tigerobo.x.pai.biz.biz.IBusinessDetailFetchService;
import com.tigerobo.x.pai.biz.biz.ViewCountService;
import com.tigerobo.x.pai.biz.biz.log.ShareLogService;
import com.tigerobo.x.pai.biz.utils.ListConvertUtil;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.dal.ai.dao.AiPhotoFixDao;
import com.tigerobo.x.pai.dal.ai.entity.AiPhotoFixPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PhotoFixService implements IBusinessDetailFetchService {

    @Autowired
    private AiPhotoFixDao aiPhotoFixDao;

    @Autowired
    private UserService userService;

    @Autowired
    private ShareLogService shareLogService;


    @Autowired
    private ViewCountService viewCountService;

    @Autowired
    private InteractionService interactionService;

    BusinessEnum businessEnum = BusinessEnum.PHOTO_FIX;

    @Value("${pai.ai.photo.averageTime:20}")
    private Integer averageTime;

    public PageVo<PhotoFixVo> getMyList(PhotoFixPageReq req) {
        Integer userId = ThreadLocalHolder.getUserId();
        Page<AiPhotoFixPo> userPage = aiPhotoFixDao.getUserPage(userId, req);

        PageVo<PhotoFixVo> pageVo = new PageVo<>();

        pageVo.setTotal(userPage.getTotal());
        pageVo.setPageSize(req.getPageSize());
        pageVo.setPageNum(req.getPageNum());

        List<PhotoFixVo> vos = PhotoFixConvert.po2vos(userPage);
        buildListUsers(vos);
        initOwner(vos, userId);
        if (!CollectionUtils.isEmpty(vos)){
            for (PhotoFixVo vo : vos) {
                String predictTime = getPredictTime(vo.getProcessStatus(), vo.getId());
                vo.setPredictRemainTime(predictTime);
            }
        }

        pageVo.setList(vos);
        return pageVo;
    }


    private void initOwner(List<PhotoFixVo> vos, Integer loginUserId) {

        if (CollectionUtils.isEmpty(vos)) {
            return;
        }

        for (PhotoFixVo vo : vos) {
            Integer userId = vo.getUserId();
            boolean owner = loginUserId != null && loginUserId.equals(userId);
            vo.setRole(owner ? Role.OWNER.toString() : Role.GUEST.toString());
        }

    }

    private void buildListUsers(List<PhotoFixVo> vos) {
        if (CollectionUtils.isEmpty(vos)) {
            return;
        }
        List<Integer> userIds = vos.stream().map(vo -> vo.getUserId()).filter(id -> id != null && id > 0).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(userIds)) {
            return;
        }
        List<User> userList = userService.getBaseUserByIds(userIds);
        if (CollectionUtils.isEmpty(userIds)) {
            return;
        }
        Map<Integer, User> userMap = ListConvertUtil.list2map(userList);

        for (PhotoFixVo vo : vos) {
            Integer userId = vo.getUserId();
            if (userId == null) {
                continue;
            }
            User user = userMap.get(userId);
            vo.setUser(user);
        }
//        
//        List<String> bizIds = vos.stream().map(vo -> String.valueOf(vo.getId())).collect(Collectors.toList());
//        Map<String, InteractVo> interactVos = interactionService.completeCount(bizIds, businessEnum.getType());
//
//        for (PhotoFixVo vo : vos) {
//            InteractVo interactVo = interactVos.get(String.valueOf(vo.getId()));
//            vo.setInteract(interactVo);
//        }


    }

    public PhotoFixVo getByReqId(Long reqId) {
        Validate.isTrue(reqId != null, "参数不正确");
        AiPhotoFixPo imagePo = aiPhotoFixDao.getByReqId(reqId);

        return buildDetailVo(imagePo);
    }

    public PhotoFixVo getDetail(IdReqVo req) {

        PhotoFixVo vo = getById(req.getId());

        if (vo != null && vo.getId() != null) {
            BusinessEnum artImage = BusinessEnum.ART_IMAGE;
            if (req.getShareUserId() != null && req.getShareUserId() > 0) {

                shareLogService.addLog(String.valueOf(vo.getId()), businessEnum.getType(), req.getShareUserId());
            }
            viewCountService.incrView(String.valueOf(vo.getId()), artImage.getType());

            InteractVo interactVo = interactionService.completeCount(String.valueOf(vo.getId()), businessEnum.getType());
            vo.setInteract(interactVo);
        }

        return vo;
    }

    public PhotoFixVo getById(Integer id) {
        Validate.isTrue(id != null, "参数不正确");
        AiPhotoFixPo imagePo = aiPhotoFixDao.load(id);
        return buildDetailVo(imagePo);
    }

    private PhotoFixVo buildDetailVo(AiPhotoFixPo imagePo) {
        Validate.isTrue(imagePo != null, "图片生成记录不存在");

        Integer userId = ThreadLocalHolder.getUserId();
        boolean isOwner = Objects.equals(imagePo.getUserId(), userId);


        PhotoFixVo photoFixVo = PhotoFixConvert.po2vo(imagePo);

        String role = isOwner ? Role.OWNER.toString() : Role.GUEST.toString();

        String predictTime = getPredictTime(imagePo.getProcessStatus(),imagePo.getId());
        photoFixVo.setPredictRemainTime(predictTime);

        User user = userService.getCacheUserByOther(photoFixVo.getUserId());
        photoFixVo.setUser(user);
        photoFixVo.setRole(role);
        return photoFixVo;
    }


    private String getPredictTime(Integer processStatus,Integer id){
        if (!AiArtImageProcessEnum.PREPARE.getStatus().equals(processStatus)){
            return "";
        }
        int count = aiPhotoFixDao.countPreIdQueue(id);
        if (count==0){
            return "";
        }

        int waitSecond = averageTime * count;

        if (waitSecond>3600*24){
            return "预计一天后完成";
        }
        if (waitSecond>=3600){

            int hour = waitSecond / 3600;
            return "预计"+hour+"小时后完成";
        }
        if (waitSecond>=60){
            int minute = waitSecond / 60;
            return "预计"+minute+"分钟后完成";
        }
        return "预计"+waitSecond+"秒后完成";



    }

    @Override
    public IBusinessDetailVo getBusinessDetail(String id) {

        final Integer intId = getIntId(id);
        if (intId == null){
            return null;
        }
        return getById(intId);
    }
}
