package com.tigerobo.pai.biz.test.ai;

import com.alibaba.fastjson.JSON;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.ai.req.*;
import com.tigerobo.x.pai.api.ai.vo.AiArtImageVo;
import com.tigerobo.x.pai.api.dto.ai.ArtModifierModel;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.biz.ai.ImageSizeService;
import com.tigerobo.x.pai.biz.ai.art.image.*;
import com.tigerobo.x.pai.biz.biz.customer.ModelConsumeCheckService;
import com.tigerobo.x.pai.biz.notify.ArtImageNotifier;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AiArtServiceTest extends BaseTest {

    @Autowired
    private ArtImageService artImageService;
    @Autowired
    private ArtImageOperateService aiArtImageOperateService;

    @Autowired
    private ArtImageUserReqService artImageUserReqService;

    @Autowired
    private ArtImageTaskService artImageTaskService;

    @Autowired
    private ArtImageNotifier artImageNotifier;

    @Autowired
    private ModelConsumeCheckService modelConsumeCheckService;

    @Autowired
    private ArtImageSearchService artImageSearchService;

    @Autowired
    private ImageSizeService imageSizeService;



    @Autowired
    private ArtImageCoinCalculateService artImageCoinCalculateService;

    @Autowired
    private ArtImageLakeService artImageLakeService;
    @Test
    public void replaceTest(){
        final Map<String, String> replaceMap = artImageLakeService.getReplaceMap();
        System.out.println(JSON.toJSONString(replaceMap));
    }
    @Test
    public void initTest(){

        ArtImageLakeService.StableReq req = new ArtImageLakeService.StableReq();

        req.setText("月亮");

        req.setHeight(400);
        req.setWidth(400);
        req.setOutputPath("/mnt/xpai/application/t/20220928/1664355456070002/");

//        artImageLakeService.dealStable(req);
    }

    @Test
    public void sizeTest(){

        final ImageSizeService.ImageSizTypeVo cache = imageSizeService.getCache();

        System.out.println(JSON.toJSONString(cache));
    }
    @Test
    public void userHomeListTest(){


        ArtImagePublicPageReq req = new ArtImagePublicPageReq();
        req.setUserId(3);
        ThreadLocalHolder.setUserId(18);
        req.setStatus(1);
        final PageVo<AiArtImageVo> userHomeList = artImageSearchService.getUserHomeList(req);

        System.out.println(JSON.toJSONString(userHomeList));
    }

    @Test
    public void countCheckTest(){
        System.out.println( modelConsumeCheckService.countUserArtImgRemainCall(2));
        System.out.println( modelConsumeCheckService.countUserArtImgRemainCall(1824));

        modelConsumeCheckService.checkArtImgCall(1824);
    }

    @Test
    public void notifierTest(){
        artImageNotifier.blockNotifier();
    }
    @Test
    public void chooseMainTest(){

        ArtImageChooseMainReq req = new ArtImageChooseMainReq();

        ThreadLocalHolder.setUserId(3);
        req.setId(473);
        req.setProgress(150);

        aiArtImageOperateService.chooseMain(req);
    }
    @Test
    public void urlTest(){

        AiArtImageGenerateReq req = new AiArtImageGenerateReq();

//        req.setText("海上升明月,天涯若比邻");
//        req.setImage("https://x-pai.algolet.com/biz/bigshot/img/13.jpg");
        List<String> modifiers = new ArrayList<>();
//        modifiers.add("涂鸦");


        AiArtImageGenerateReq.ArtImageParams params = new AiArtImageGenerateReq.ArtImageParams();
        params.setText("长城");
        req.setInputParam(Arrays.asList(params));
        req.setUserId(3);
        req.setUseFree(true);
        req.setPromptWeight(9f);
        req.setImageStrength(0.8f);
        req.setSteps(50);
        req.setStyleType("stable");
        req.setSizeId(168);
        req.setNIter(1);
        req.setCoinTotal(1);

        req.setSeed((int)(System.currentTimeMillis()/1000));
        req.setWidth(512);
        req.setHeight(512);

        req.setModelVersion("v2.1");


//        final Integer cal = artImageCoinCalculateService.cal(req);
//        System.out.println("base="+cal);

//        req.setCoinTotal(cal);


        Integer aLong = artImageUserReqService.innerReqProduceImage(req);

        System.out.println(aLong);

    }


    @Autowired
    private AiParamDictService aiParamDictService;

    @Test
    public void taskTest(){

        artImageTaskService.dealPrepareTask(true);
    }

    @Test
    public void getModifierTest(){

        final List<ArtModifierModel> classTypeDicts = aiParamDictService.getClassTypeDicts();
        System.out.println(JSON.toJSONString(classTypeDicts));

        final ArtModifierModel model = aiParamDictService.getByClassType(classTypeDicts.get(0).getClassType());
        System.out.println(JSON.toJSONString(model));

    }
    @Test
    public void getPublishTest(){

        ThreadLocalHolder.setUserId(2);
        ArtImagePublicPageReq req =  new ArtImagePublicPageReq();
//        req.setTabType("follow");

//        req.setHotType("oneWeek");
//        req.setKeyword("海贼");
//        req.setModifier("日漫");

        PageVo<AiArtImageVo> page = artImageService.getPublishList(req);

        System.out.println(JSON.toJSONString(page));

    }

    @Test
    public void getHotTest(){

        ThreadLocalHolder.setUserId(3);
        ArtImagePublicPageReq req =  new ArtImagePublicPageReq();


        PageVo<AiArtImageVo> page = artImageService.getHotList(req);

        for (AiArtImageVo vo : page.getList()) {
            System.out.println(vo.getId()+"\t"+vo.getInteract().getThumbUpNum());
        }

        System.out.println(JSON.toJSONString(page));

    }
    @Test
    public void getMineTest(){

        ThreadLocalHolder.setUserId(3);
        ArtImagePublicPageReq req =  new ArtImagePublicPageReq();

        req.setKeyword("星空");

        PageVo<AiArtImageVo> page = artImageService.getMyList(req);

        System.out.println(JSON.toJSONString(page));

    }

    @Test
    public void getDeTailTest(){


        AiArtDetailReq req =  new AiArtDetailReq();

        Long reqId = 1670693590600002L;

        AiArtImageVo page = artImageService.getByReqId(null,reqId);

        System.out.println(JSON.toJSONString(page));

    }
    @Test
    public void dealWaitTest(){

        artImageTaskService.dealTaskResult();
    }
}
