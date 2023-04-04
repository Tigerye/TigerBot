package com.tigerobo.x.pai.biz.ai.art.image;

import com.algolet.pay.api.dto.AlgCoinAddDto;
import com.algolet.pay.api.enums.AlgCoinBusinessType;
import com.algolet.pay.biz.service.AlgCoinService;
import com.alibaba.fastjson.JSON;
import com.tigerobo.x.pai.api.ai.enums.AiArtImageProcessEnum;
import com.tigerobo.x.pai.api.ai.req.AiArtImageGenerateReq;
import com.tigerobo.x.pai.api.ai.req.art.image.ArtImageUserReq;
import com.tigerobo.x.pai.api.enums.ArtImageType;
import com.tigerobo.x.pai.api.enums.BusinessEnum;
import com.tigerobo.x.pai.biz.ai.ImageSizeService;
import com.tigerobo.x.pai.biz.biz.customer.ModelConsumeCheckService;
import com.tigerobo.x.pai.biz.cache.biz.MachineUtil;
import com.tigerobo.x.pai.biz.lake.LakeSensitiveService;
import com.tigerobo.x.pai.biz.notify.ArtImageNotifier;
import com.tigerobo.x.pai.biz.user.BlackUserService;
import com.tigerobo.x.pai.biz.utils.IdGenerator;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.dal.ai.dao.AiArtImageDao;
import com.tigerobo.x.pai.dal.ai.dao.AiUserInterActDao;
import com.tigerobo.x.pai.dal.ai.entity.AiArtImagePo;
import com.tigerobo.x.pai.dal.ai.entity.AiImageSizePo;
import com.tigerobo.x.pai.dal.ai.entity.AiUserInteractPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ArtImageUserReqService {
    @Autowired
    private AiArtImageDao aiArtImageDao;

    @Autowired
    private ModelConsumeCheckService modelConsumeCheckService;
    @Autowired
    private MachineUtil machineUtil;
    @Autowired
    private LakeSensitiveService lakeSensitiveService;

    @Autowired
    private ArtImageWorkEnvService artImageWorkEnvService;

    @Autowired
    private AiUserInterActDao aiUserInterActDao;

    @Autowired
    private ImageSizeService imageSizeService;

    @Autowired
    private ArtImageNotifier artImageNotifier;

    @Autowired
    private ArtImageCoinCalculateService artImageCoinCalculateService;

    @Autowired
    private AlgCoinService algCoinService;

    @Autowired
    private BlackUserService blackUserService;

    @Autowired
    private ArtImageAuditPassService artImageAuditPassService;
    @Value("${pai.artImage.useFree:false}")
    boolean useFree;
    public Long pageReq(ArtImageUserReq req){
        final Integer userId = ThreadLocalHolder.getUserId();

        final AiArtImageGenerateReq params = req.getParams();
        params.setUserId(userId);
        return userReqProduceImage(params);
    }

    @Transactional(transactionManager = "paiTm")
    public Long userReqProduceImage(AiArtImageGenerateReq req) {
        checkParam(req);

        AiArtImagePo po = getAddPo(req,true);
        aiArtImageDao.add(po);
        add2interact(po);

        final Integer processStatus = po.getProcessStatus();
        if (AiArtImageProcessEnum.WAIT_AUDIT.getStatus().equals(processStatus)){
            artImageNotifier.waitAuditNotifier();
        }

        final Integer userId = req.getUserId();
        if (req.getCoinTotal()!=null&&req.getCoinTotal()>0){
            final AlgCoinAddDto coinAdd = AlgCoinAddDto.builder().userId(userId)
                    .num(req.getCoinTotal())
                    .bizType(AlgCoinBusinessType.ART_IMAGE_CONSUME)
                    .refId(String.valueOf(po.getId()))
                    .build();
            algCoinService.addBusiness(coinAdd);
        }
        return po.getReqId();
    }
    @Transactional(transactionManager = "paiTm")
    public Integer innerReqProduceImage(AiArtImageGenerateReq req) {
        AiArtImagePo po = getAddPo(req,false);
        aiArtImageDao.add(po);
        return po.getId();
    }

    private AiArtImagePo getAddPo(AiArtImageGenerateReq req,boolean checkSense) {
        final List<AiArtImageGenerateReq.ArtImageParams> inputParam = req.getInputParam();

        final String styleType = req.getStyleType();
        final ArtImageType imageType = ArtImageType.getByName(styleType);

        Validate.isTrue(imageType!=null,"风格不支持");

        AiArtImagePo po = new AiArtImagePo();
        Long reqId =IdGenerator.getBaseId(machineUtil.getMachineId());
        po.setReqId(reqId);
        po.setUserId(req.getUserId());
        po.setInputParam(JSON.toJSONString(inputParam));
        po.setStyleType(imageType.getType());

        List<String> allModifiers = getModifiers(inputParam);
        if (allModifiers.size()>0){
            po.setModifiers(JSON.toJSONString(allModifiers));
        }
        final AiArtImageGenerateReq.ArtImageParams first = inputParam.get(0);

        if (checkSense){
            checkSensitive(po, first);
        }


        po.setText(first.getText());

        String itemWorkPath = artImageWorkEnvService.getWorkPath(po.getReqId());
        po.setWorkPath(itemWorkPath);
        po.setInputImage(req.getImage());

//        po.setProcessStatus(AiArtImageProcessEnum.ON_PROCESS.getStatus());

        po.setReqTime(new Date());
//        po.setApiKey(req.getApiKey());
        po.setAiVersion(2);

        this.initSize(req.getSizeId(),po);

        po.setTotalProgress(artImageWorkEnvService.totalProgress);

        po.setUseFree(req.isUseFree());

        if (req.getCoinTotal()!=null&&req.getCoinTotal()>0){
            po.setCoinTotal(req.getCoinTotal());
            po.setCoinStatus(1);
        }else {
            po.setCoinStatus(0);
        }

        po.setSteps(req.getSteps());
        po.setPromptWeight(req.getPromptWeight());
        po.setImageStrength(req.getImageStrength());
        po.setNIter(req.getNIter());
        po.setSeed(req.getSeed());
        po.setModelVersion(req.getModelVersion());
        return po;
    }

    private void checkSensitive(AiArtImagePo po, AiArtImageGenerateReq.ArtImageParams first) {
        AiArtImageProcessEnum processEnum = AiArtImageProcessEnum.PREPARE;
        if (artImageWorkEnvService.checkSensitive){

            String label = lakeSensitiveService.getHupuLabel(first.getText());
            if (StringUtils.isNotBlank(label)){
                final boolean prePass = artImageAuditPassService.isPrePass(first.getText());
                if (!prePass){
                    po.setMsg("违规:"+label);
                    processEnum = AiArtImageProcessEnum.WAIT_AUDIT;
                }
            }
        }
        po.setProcessStatus(processEnum.getStatus());

    }

    private void initSize(Integer sizeId,AiArtImagePo po ){
        final AiImageSizePo sizePo = imageSizeService.getById(sizeId);
        if (sizePo!=null){
            po.setHeight(sizePo.getHeight());
            po.setWidth(sizePo.getWidth());
        }else {
            po.setHeight(512);
            po.setWidth(512);
        }
    }

    private void add2interact(AiArtImagePo po) {
        AiUserInteractPo aiUserInteractPo = new AiUserInteractPo();
        aiUserInteractPo.setBizType(BusinessEnum.ART_IMAGE.getType());
        aiUserInteractPo.setBizId(String.valueOf(po.getId()));
        aiUserInteractPo.setUserId(po.getUserId());
        aiUserInteractPo.setTitle(po.getText());
        aiUserInterActDao.add(aiUserInteractPo);
    }

    private void checkParam(AiArtImageGenerateReq req) {
        Validate.isTrue(req.getUserId() != null && req.getUserId() > 0, "用户未登录");

        final List<AiArtImageGenerateReq.ArtImageParams> inputParam = req.getInputParam();
        Validate.isTrue(!CollectionUtils.isEmpty(inputParam), "文本不能为空");


        final boolean blackUser = blackUserService.isBlackUser(req.getUserId());

        Validate.isTrue(!blackUser,"用户没有操作权限");
        for (int i = 0; i < inputParam.size(); i++) {
            final AiArtImageGenerateReq.ArtImageParams artImageParams = inputParam.get(i);
            Validate.isTrue(StringUtils.isNotBlank(artImageParams.getText()),"第"+i+"行文本为空");
        }

        final Integer userId = req.getUserId();
        if (useFree){
            modelConsumeCheckService.checkArtImgCall(userId);
            return;
        }

        final Integer cal = artImageCoinCalculateService.cal(req);
        Validate.isTrue(cal.equals(req.getCoinTotal()),"积分已变更，请刷新重试");
        final int userTotalCoin = algCoinService.getUserTotalCoin(userId);
        Validate.isTrue(userTotalCoin>=cal,"积分不足");
    }

    private List<String> getModifiers(List<AiArtImageGenerateReq.ArtImageParams> inputParam) {
        List<String> allModifiers = new ArrayList<>();
        for (AiArtImageGenerateReq.ArtImageParams params : inputParam) {
            final List<String> modifiers = params.getModifiers();
            if (modifiers!=null){
                for (String modifier : modifiers) {
                    if (StringUtils.isBlank(modifier)){
                        continue;
                    }
                    if (!allModifiers.contains(modifier)){
                        allModifiers.add(modifier);
                        if (allModifiers.size()>=3){
                            break;
                        }
                    }
                }
            }
            if (allModifiers.size()>=3){
                break;
            }
        }
        return allModifiers;
    }

}
