package com.tigerobo.x.pai.biz.serving;

import com.tigerobo.x.pai.api.auth.service.UserService;
import com.tigerobo.x.pai.api.biz.entity.API;
import com.tigerobo.x.pai.api.dto.ApiDto;
import com.tigerobo.x.pai.api.enums.Style;
import com.tigerobo.x.pai.biz.ai.art.image.ArtImageAiService;
import com.tigerobo.x.pai.biz.ai.multi.object.track.AiMultiObjectTrackService;
import com.tigerobo.x.pai.biz.ai.photo.PhotoFixAiService;
import com.tigerobo.x.pai.biz.ai.spatio.action.AiSpatioActionService;
import com.tigerobo.x.pai.biz.ai.style.transfer.AiStyleTransferService;
import com.tigerobo.x.pai.biz.converter.APIConvert;
import com.tigerobo.x.pai.biz.serving.execute.*;
import com.tigerobo.x.pai.biz.serving.execute.image.ArtImageExecutor;
import com.tigerobo.x.pai.biz.serving.execute.image.ImageEntityRecognizeExecutor;
import com.tigerobo.x.pai.biz.serving.execute.video.MultiObjectTractExecutor;
import com.tigerobo.x.pai.biz.serving.execute.video.SpatioActionExecutor;
import com.tigerobo.x.pai.dal.biz.dao.ApiDao;
import com.tigerobo.x.pai.dal.biz.dao.ModelNewWordDao;
import com.tigerobo.x.pai.dal.biz.entity.ApiDo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description:
 * @modified By:
 * @version: $
 */
//@Slf4j
//@Data
@Component
public class ExecutorFactory {
    @Autowired
    private ModelNewWordDao modelNewWordDao;
    @Autowired
    private ApiDao apiDao;

    @Autowired
    private ArtImageAiService artImageAiService;

    @Autowired
    private PhotoFixAiService photoFixAiService;

    @Autowired
    private AiStyleTransferService aiStyleTransferService;
    @Autowired
    private AiSpatioActionService aiSpatioActionService;


    @Autowired
    private AiMultiObjectTrackService aiMultiObjectTractService;

    @Autowired
    private UserService userService;

    public Executable getApi(String key){

        if (StringUtils.isEmpty(key)){
            return null;
        }
        ApiDo apiDo = apiDao.getByModelUuid(key);
        return buildExecutable(apiDo);
    }

    public Executable buildExecutable(ApiDo apiDo) {
        if (apiDo == null){
            return null;
        }
        ApiDto api = APIConvert.po2dto(apiDo);
        if (api == null){
            return null;
        }

        Executable executor = null;
        String apiStyle = api.getStyle();
        if (Style.KEY_TO_WORD.toString().equalsIgnoreCase(apiStyle)){
            executor = new NewWordApiExecutor(api,modelNewWordDao);
        }else if(Style.TEXT_CORRECT.toString().equalsIgnoreCase(apiStyle)){
            executor = new TextCorrectExecutor(api);
        }else if (Style.CONTENT_UNDERSTAND.toString().equalsIgnoreCase(apiStyle)){
            executor = new ContentUnderstandExecutor(api);
        }else if (Style.QA_DOMAIN.toString().equalsIgnoreCase(apiStyle)){
            executor = new OpenDomainQAApiExecutor(api);
        }else if (Style.ZERO_SAMPLE_LABEL.toString().equalsIgnoreCase(apiStyle)){
            executor = new ZeroSampleLabelExecutor(api);
        }else if (Style.TEXT_TO_QA.toString().equalsIgnoreCase(apiStyle)){
            executor = new ProduceQAExecutor(api);
        }else if (Style.IMAGE_LABEL.toString().equalsIgnoreCase(apiStyle)){
            executor = new ImageLabelExecutor(api);
        }else if (Style.ART_IMAGE.toString().equalsIgnoreCase(apiStyle)){
            executor = new ArtImageExecutor(api,artImageAiService,userService);
        }else if (Style.PHOTO_FIX.toString().equalsIgnoreCase(apiStyle)){
            executor = new PhotoFixExecutor(api,photoFixAiService,userService);
        }else if (Style.STYLE_TRANSFER.toString().equalsIgnoreCase(apiStyle)){
            executor = new StyleTransferExecutor(api,aiStyleTransferService,userService);
        }else if (Style.OCR.toString().equalsIgnoreCase(apiStyle)){
            executor = new OcrExecutor(api);
        }else if (Style.IMAGE_ENTITY_RECOGNIZE.toString().equalsIgnoreCase(apiStyle)){
            executor = new ImageEntityRecognizeExecutor(api);
        }else if (Style.IMAGE_INSTANCE_SEGMENTATION.toString().equalsIgnoreCase(apiStyle)){
            executor = new ImageEntityRecognizeExecutor(api);
        }else if (Style.SPATIO_ACTION.toString().equalsIgnoreCase(apiStyle)){
            executor = new SpatioActionExecutor(api,aiSpatioActionService,userService);
        }else if (Style.MULTI_OBJECT_TRACK.toString().equalsIgnoreCase(apiStyle)){
            executor = new MultiObjectTractExecutor(api,aiMultiObjectTractService,userService);
        }else if (Style.TEXT_GENERATE.toString().equalsIgnoreCase(apiStyle)){
            executor = new TextGenerateExecutor(api);
        }
        if (executor == null){
            executor = new UriApiExecutor(api);
        }
        return executor;
    }

    public Executable get(String apiKey) {

        return this.getApi(apiKey);
    }

    public API profile(String apiKey) {
        Executable executor = this.get(apiKey);
        if (executor == null) {
            return null;
        }
        return executor.profileClean();
    }

}
