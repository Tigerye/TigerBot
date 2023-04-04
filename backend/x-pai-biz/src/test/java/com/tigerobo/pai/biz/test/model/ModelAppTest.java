package com.tigerobo.pai.biz.test.model;

import com.alibaba.fastjson.JSON;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.enums.Style;
import com.tigerobo.x.pai.api.vo.biz.ModelApiReq;
import com.tigerobo.x.pai.biz.biz.process.ImageFactory;
import com.tigerobo.x.pai.biz.biz.service.ModelAppService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class ModelAppTest extends BaseTest {

    @Autowired
    private ModelAppService modelAppService;

    @Autowired
    private ImageFactory imageFactory;


    @Test
    public void imageTest(){


        String image1 = imageFactory.getImage();

        List<String> images = imageFactory.getImagePool();

        for (String image : images) {
            System.out.println(image);
        }

    }

    @Test
    public void addModelTest(){
//        doAdd(getZeroSampleApiReq());
//        doAdd(getXueqiuApiReq());
//        doAdd(getAdApiReq());
//        doAdd(getPhotoFixApiReq());
//        doAdd(commonTranslateReq());
//        doAdd(searchReq());
//        doAdd(getOcrReq());

//        doAdd(getIdn2cnReq());
//        doAdd(getJiJinAuditReq());
//        doAdd(getMultiObjectTractActionReq());
        doAdd(enTextGenerateReq());

    }


    private void doAdd(ModelApiReq req) {
        modelAppService.addApp(req);
    }

    private ModelApiReq enTextGenerateReq() {

        String image = imageFactory.getImage();
        String modelName = "英文GPT2文本生成";
        Integer userId = 3;
//        userId = 20;
        String appShortName = "en_text_generate";

        String apiUrl = "http://gbox9.aigauss.com:9207/infer";

        //虎博科技
        String groupUuid = "";

        Integer groupId = 100000001;

        String style = Style.TEXT_GENERATE.toString();

        String baseModelUid = "qa_produce";

        Map<String,Object> pageDemoMap = null;

        Map<String,String> demo = new HashMap<>();

        demo.put("text","Kobe Bryant is");
//        demo.put("styleImageId","0");

        String tect = "英文GPT2-Large";
        String scene = "英文文本生成";
        String slogan = "英文文本生成";
        ModelApiReq req = ModelApiReq.builder()
                .modelName(modelName)
                .image(image)
                .apiUrl(apiUrl)
                .demo(JSON.toJSONString(demo))
                .pageDemo(null)
                .tectIntro(tect)
                .sceneIntro(scene)
                .style(style)
                .userId(userId)
                .groupId(groupId)
                .groupUuid(groupUuid)
                .baseModelUid(baseModelUid)
                .modelUpdateTime(new Date())
                .appShortName(appShortName)
                .slogan(slogan)
                .build();
        return req;
    }
    private ModelApiReq textGenerateReq() {

        String image = imageFactory.getImage();
        String modelName = "中文GPT2文本生成";
        Integer userId = 3;
//        userId = 20;
        String appShortName = "text_generate";

        String apiUrl = "http://gbox9.aigauss.com:9206/infer";

        //虎博科技
        String groupUuid = "";

        Integer groupId = 100000001;

        String style = Style.TEXT_TO_TEXT.toString();

        String baseModelUid = "qa_produce";

        Map<String,Object> pageDemoMap = null;

        Map<String,String> demo = new HashMap<>();

        demo.put("text","杭州位于");
//        demo.put("styleImageId","0");

        String tect = "中文GPT2-3.5B";
        String scene = "文本生成";
        String slogan = "文本生成";
        ModelApiReq req = ModelApiReq.builder()
                .modelName(modelName)
                .image(image)
                .apiUrl(apiUrl)
                .demo(JSON.toJSONString(demo))
                .pageDemo(null)
                .tectIntro(tect)
                .sceneIntro(scene)
                .style(style)
                .userId(userId)
                .groupId(groupId)
                .groupUuid(groupUuid)
                .baseModelUid(baseModelUid)
                .modelUpdateTime(new Date())
                .appShortName(appShortName)
                .slogan(slogan)
                .build();
        return req;
    }
    private ModelApiReq getMultiObjectTractActionReq() {

        String image = imageFactory.getImage();
        String modelName = "多目标跟踪";
        Integer userId = 3;
//        userId = 20;
        String appShortName = "mutti_object_tract";

        String apiUrl = "http://gbox9.aigauss.com:9527/inferr";

        //虎博科技
        String groupUuid = "";

        Integer groupId = 100000001;

        String style = Style.MULTI_OBJECT_TRACK.toString();

        String baseModelUid = "video_understand";

        Map<String,Object> pageDemoMap = null;

        String url = "https://x-pai.algolet.com/model/multi_object_tract/sample/demo.mp4";
        String pageDemoJson = "{\"samples\":[{\"url\":\""+url+"\",\"label\":\"video\"}]}";

//        pageDemoMap = JSON.parseObject(pageDemoJson);

        Map<String,String> demo = new HashMap<>();

        demo.put("url",url);
//        demo.put("styleImageId","0");

        String tect = "基于ByteTrack的多目标跟踪";
        String scene = "基于MOT 数据集的多目标跟踪";
        String slogan = "基于MOT 数据集的多目标跟踪";
        ModelApiReq req = ModelApiReq.builder()
                .modelName(modelName)
                .image(image)
                .apiUrl(apiUrl)
                .demo(JSON.toJSONString(demo))
                .pageDemo(pageDemoJson)
                .tectIntro(tect)
                .sceneIntro(scene)
                .style(style)
                .userId(userId)
                .groupId(groupId)
                .groupUuid(groupUuid)
                .baseModelUid(baseModelUid)
                .modelUpdateTime(new Date())
                .appShortName(appShortName)
                .slogan(slogan)
                .build();
        return req;
    }


    private ModelApiReq getVideoSpatioActionReq() {

        String image = imageFactory.getImage();
        String modelName = "时空动作检测";
        Integer userId = 3;
        userId = 20;
        String appShortName = "spatio_action";

        String apiUrl = "http://gbox9.aigauss.com:9526/infer";

        //虎博科技
        String groupUuid = "";

        Integer groupId = 100000001;

        String style = Style.SPATIO_ACTION.toString();

        String baseModelUid = "video_understand";

        Map<String,Object> pageDemoMap = null;

        String url = "https://x-pai.algolet.com/model/spatio_action/example/sample.mp4";
        String pageDemoJson = "{\"samples\":[{\"url\":\"https://x-pai.algolet.com/model/image_entity_recognize/sample.jpg\",\"label\":\"video\"}]}";

//        pageDemoMap = JSON.parseObject(pageDemoJson);

        Map<String,String> demo = new HashMap<>();

        demo.put("url",url);
//        demo.put("styleImageId","0");

        String tect = "基于SlowFast的时空动作检测";
        String scene = "基于AVA 数据集时空动作检测";
        String slogan = "基于AVA 数据集时空动作检测";
        ModelApiReq req = ModelApiReq.builder()
                .modelName(modelName)
                .image(image)
                .apiUrl(apiUrl)
                .demo(JSON.toJSONString(demo))
                .pageDemo(pageDemoJson)
                .tectIntro(tect)
                .sceneIntro(scene)
                .style(style)
                .userId(userId)
                .groupId(groupId)
                .groupUuid(groupUuid)
                .baseModelUid(baseModelUid)
                .modelUpdateTime(new Date())
                .appShortName(appShortName)
                .slogan(slogan)
                .build();
        return req;
    }

    private ModelApiReq getImageInstanceSegReq() {

        String image = imageFactory.getImage();
        String modelName = "图片实例分割";
        Integer userId = 3;
//        userId = 20;
        String appShortName = "img_instance_seg";

        String apiUrl = "http://gbox9.aigauss.com:8080/predictions/mask_rcnn";

        //虎博科技
        String groupUuid = "";

        Integer groupId = 100000001;

        String style = Style.IMAGE_INSTANCE_SEGMENTATION.toString();

        String baseModelUid = "image_label";

        Map<String,Object> pageDemoMap = null;

        String pageDemoJson = "{\"samples\":[{\"imageUrl\":\"https://x-pai.algolet.com/model/image_entity_recognize/sample.jpg\",\"label\":\"car\"},{\"imageUrl\":\"https://x-pai.algolet.com/model/image_entity_recognize/sample_02.png\",\"label\":\"zebra\"},{\"imageUrl\":\"https://x-pai.algolet.com/model/image_entity_recognize/sample_03.jpg\",\"label\":\"person\"}]}";

//        pageDemoMap = JSON.parseObject(pageDemoJson);

        Map<String,String> demo = new HashMap<>();

        demo.put("image","https://x-pai.algolet.com/model/image_entity_recognize/sample.jpg");
//        demo.put("styleImageId","0");

        String tect = "基于Mask R-CNN的instance segmentation";
        String scene = "基于coco 数据集的instance segmentation";
        String slogan = "基于coco 数据集的instance segmentation";
        ModelApiReq req = ModelApiReq.builder()
                .modelName(modelName)
                .image(image)
                .apiUrl(apiUrl)
                .demo(JSON.toJSONString(demo))
                .pageDemo(pageDemoJson)
                .tectIntro(tect)
                .sceneIntro(scene)
                .style(style)
                .userId(userId)
                .groupId(groupId)
                .groupUuid(groupUuid)
                .baseModelUid(baseModelUid)
                .modelUpdateTime(new Date())
                .appShortName(appShortName)
                .slogan(slogan)
                .build();
        return req;
    }
    private ModelApiReq getImageEntityRecognizeReq() {

        String image = imageFactory.getImage();
        String modelName = "图片目标检测";
        Integer userId = 3;
//        userId = 20;
        String appShortName = "img_entity_recognize";

        String apiUrl = "http://gbox9.aigauss.com:8080/predictions/faster_rcnn";

        //虎博科技
        String groupUuid = "";

        Integer groupId = 100000001;

        String style = Style.IMAGE_ENTITY_RECOGNIZE.toString();

        String baseModelUid = "image_label";

        Map<String,Object> pageDemoMap = null;

        String pageDemoJson = "{\"samples\":[{\"imageUrl\":\"https://x-pai.algolet.com/model/image_entity_recognize/sample.jpg\",\"label\":\"car\"},{\"imageUrl\":\"https://x-pai.algolet.com/model/image_entity_recognize/sample_02.png\",\"label\":\"zebra\"},{\"imageUrl\":\"https://x-pai.algolet.com/model/image_entity_recognize/sample_03.jpg\",\"label\":\"person\"}]}";

//        pageDemoMap = JSON.parseObject(pageDemoJson);

        Map<String,String> demo = new HashMap<>();

        demo.put("image","https://x-pai.algolet.com/model/image_entity_recognize/sample.jpg");
//        demo.put("styleImageId","0");

        String tect = "基于Faster R-CNN的目标检测模型";
        String scene = "基于coco object detection数据集的目标检测";
        String slogan = "基于coco object detection数据集的目标检测";
        ModelApiReq req = ModelApiReq.builder()
                .modelName(modelName)
                .image(image)
                .apiUrl(apiUrl)
                .demo(JSON.toJSONString(demo))
                .pageDemo(pageDemoJson)
                .tectIntro(tect)
                .sceneIntro(scene)
                .style(style)
                .userId(userId)
                .groupId(groupId)
                .groupUuid(groupUuid)
                .baseModelUid(baseModelUid)
                .modelUpdateTime(new Date())
                .appShortName(appShortName)
                .slogan(slogan)
                .build();
        return req;
    }
    private ModelApiReq getXueqiuReq() {

        String image = imageFactory.getImage();
        String modelName = "多语言实体识别";
        Integer userId = 3;
//        userId = 20;
        String appShortName = "xq_ml_er";

        String apiUrl = "http://gbox4.aigauss.com:9525/infer";

        //虎博科技
        String groupUuid = "";

        Integer groupId = 100000001;

        String style = Style.TEXT_TO_ENTITY.toString();

        String baseModelUid = "entity_recognition";

        Map<String,Object> pageDemoMap = null;

        Map<String,String> demo = new HashMap<>();

        demo.put("text","北京勘察设计协会副会长周荫如在北京");
//        demo.put("styleImageId","0");

        String tect = "基于multilingual-bert的实体识别模型";
        String scene = "识别英文，中文，法语，德语等多语言文本中的地点，人名，组织等实体";
        String slogan = "识别英文，中文，法语，德语等多语言文本中的地点，人名，组织等实体";
        ModelApiReq req = ModelApiReq.builder()
                .modelName(modelName)
                .image(image)
                .apiUrl(apiUrl)
                .demo(JSON.toJSONString(demo))
                .pageDemo(null)
                .tectIntro(tect)
                .sceneIntro(scene)
                .style(style)
                .userId(userId)
                .groupId(groupId)
                .groupUuid(groupUuid)
                .baseModelUid(baseModelUid)
                .modelUpdateTime(new Date())
                .appShortName(appShortName)
                .slogan(slogan)
                .build();
        return req;
    }
    private ModelApiReq getJiJinAuditReq() {

        String image = imageFactory.getImage();
        String modelName = "基金产品宣传推介材料审核";
        Integer userId = 3;
//        userId = 20;
        String appShortName = "material_review";

        String apiUrl = "http://gbox4.aigauss.com:9522/infer";

        //虎博科技
        String groupUuid = "";

        Integer groupId = 100000001;

        String style = Style.TEXT_TO_ENTITY_REVIEW.toString();

        String baseModelUid = "entity_recognition";

        Map<String,Object> pageDemoMap = null;

        Map<String,String> demo = new HashMap<>();

        demo.put("text","由泰和县人民政府批准，在泰和县工信委和金融办共同指导和监督下成立的引导民间融资规范化、阳光化运作的改革试点机构。消费和医疗，这个位置也可以越跌越买，但是跟我前面提到的孰轻孰重，一看便知。限时秒杀，您只负责轻松躺赢");
//        demo.put("styleImageId","0");

        String tect = "结合分类模型，实体识别模型，阅读理解模型的金融违规审核";
        String scene = "对基金产品的宣传推介材料进行审核，以预防潜在的金融宣传推介违规。";
        String slogan = "对基金产品的宣传推介材料进行审核，以预防潜在的金融宣传推介违规。";
        ModelApiReq req = ModelApiReq.builder()
                .modelName(modelName)
                .image(image)
                .apiUrl(apiUrl)
                .demo(JSON.toJSONString(demo))
                .pageDemo(null)
                .tectIntro(tect)
                .sceneIntro(scene)
                .style(style)
                .userId(userId)
                .groupId(groupId)
                .groupUuid(groupUuid)
                .baseModelUid(baseModelUid)
                .modelUpdateTime(new Date())
                .appShortName(appShortName)
                .slogan(slogan)
                .build();
        return req;
    }
    private ModelApiReq getIdn2cnReq() {

        String image = imageFactory.getImage();
        String modelName = "印尼语翻译中文";
        Integer userId = 3;
//        userId = 20;
        String appShortName = "idn_cn";

        String apiUrl = "http://gbox4.aigauss.com:9520/infer";

        //虎博科技
        String groupUuid = "";

        Integer groupId = 100000001;

        String style = Style.TEXT_TO_TEXT.toString();

        String baseModelUid = "machine_translation";

        Map<String,Object> pageDemoMap = null;

        Map<String,String> demo = new HashMap<>();

        demo.put("text","Halo Dunia");
//        demo.put("styleImageId","0");

        String tect = "基于Mbart模型，在文本语料上做印尼语翻译成英文任务。";
        String scene = "将印尼语翻译成中文";
        String slogan = "将印尼语翻译成中文";
        ModelApiReq req = ModelApiReq.builder()
                .modelName(modelName)
                .image(image)
                .apiUrl(apiUrl)
                .demo(JSON.toJSONString(demo))
                .pageDemo(null)
                .tectIntro(tect)
                .sceneIntro(scene)
                .style(style)
                .userId(userId)
                .groupId(groupId)
                .groupUuid(groupUuid)
                .baseModelUid(baseModelUid)
                .modelUpdateTime(new Date())
                .appShortName(appShortName)
                .slogan(slogan)
                .build();
        return req;
    }

    private ModelApiReq getOcrReq() {

        String image = imageFactory.getImage();
        String modelName = "图片OCR";
        Integer userId = 3;
        userId = 20;
        String appShortName = "ocr";

        String apiUrl = "http://gbox6.aigauss.com:9500/infer";

        //虎博科技
        String groupUuid = "";

        Integer groupId = 100000001;

        String style = Style.OCR.toString();

        String baseModelUid = "image_generate";

        Map<String,Object> pageDemoMap = null;

        Map<String,String> demo = new HashMap<>();

        demo.put("image","https://x-pai.algolet.com/model/ocr/demo_1.jpg");
//        demo.put("styleImageId","0");

        String tect = "基于CRAFT文本检测算法和CRNN文本识别算法实现OCR";
        String scene = "文本检测与文本识别";
        String slogan = "文本检测与文本识别";
        ModelApiReq req = ModelApiReq.builder()
                .modelName(modelName)
                .image(image)
                .apiUrl(apiUrl)
                .demo(JSON.toJSONString(demo))
                .pageDemo(null)
                .tectIntro(tect)
                .sceneIntro(scene)
                .style(style)
                .userId(userId)
                .groupId(groupId)
                .groupUuid(groupUuid)
                .baseModelUid(baseModelUid)
                .modelUpdateTime(new Date())
                .appShortName(appShortName)
                .slogan(slogan)
                .build();
        return req;
    }
    private ModelApiReq getStyleTransferReq() {

        String image = imageFactory.getImage();
        String modelName = "图像风格迁移";
        Integer userId = 3;
//        userId = 20;
        String appShortName = "style_transfer";

        String apiUrl = "http://gbox4.aigauss.com:9518/infer";

        //虎博科技
        String groupUuid = "472538c74908d478bf7bc93956b5f50d";

        Integer groupId = 100000001;

        String style = Style.STYLE_TRANSFER.toString();

        String baseModelUid = "image_generate";

        Map<String,Object> pageDemoMap = null;

        Map<String,String> demo = new HashMap<>();

        demo.put("contentImage","https://x-pai.algolet.com/model/style_transfer/sample/eiffel-tower.jpg");
        demo.put("styleImage","https://x-pai.algolet.com/model/style_transfer/sample/style.jpg");
//        demo.put("styleImageId","0");

        String tect = "基于卷积神经网络模型VGG的图像风格迁移";
        String scene = "图像风格迁移";
        String slogan = "将你的图片变成大师级作品";
        ModelApiReq req = ModelApiReq.builder()
                .modelName(modelName)
                .image(image)
                .apiUrl(apiUrl)
                .demo(JSON.toJSONString(demo))
                .pageDemo(null)
                .tectIntro(tect)
                .sceneIntro(scene)
                .style(style)
                .userId(userId)
                .groupId(groupId)
                .groupUuid(groupUuid)
                .baseModelUid(baseModelUid)
                .modelUpdateTime(new Date())
                .appShortName(appShortName)
                .slogan(slogan)
                .build();
        return req;
    }

    private ModelApiReq searchReq() {

        String image = imageFactory.getImage();
        String modelName = "搜索";
        Integer userId = 3;
//        userId = 20;
        String appShortName = "key_search";

        String apiUrl = "";

        //虎博科技
        String groupUuid = "472538c74908d478bf7bc93956b5f50d";

        Integer groupId = 100000001;

        String style = Style.KEY_SEARCH.toString();

        String baseModelUid = "search";

        Map<String,Object> pageDemoMap = null;

        Map<String,String> demo = new HashMap<>();

//        demo.put("text","Accelerating PyTorch distributed fine-tuning with Intel technologies");

        String tect = "对关键词建立倒排索引,使用tf-idf相关性返回搜索结果";
        String scene = "文本关键词搜索";
        ModelApiReq req = ModelApiReq.builder()
                .modelName(modelName)
                .image(image)
                .apiUrl(apiUrl)
                .demo(JSON.toJSONString(demo))
                .pageDemo(null)
                .tectIntro(tect)
                .sceneIntro(scene)
                .style(style)
                .userId(userId)
                .groupId(groupId)
                .groupUuid(groupUuid)
                .baseModelUid(baseModelUid)
                .modelUpdateTime(new Date())
                .appShortName(appShortName)
                .build();
        return req;
    }


    private ModelApiReq commonTranslateReq() {

        String image = imageFactory.getImage();
        String modelName = "通用文本英翻中";
        Integer userId = 3;
//        userId = 20;
        String appShortName = "com_en2cn";

        String apiUrl = "http://gbox4.aigauss.com:9500/infer";

        //虎博科技
        String groupUuid = "472538c74908d478bf7bc93956b5f50d";

        Integer groupId = 100000001;

        String style = Style.TEXT_TO_TEXT.toString();

        String baseModelUid = "machine-translation";

        Map<String,Object> pageDemoMap = null;

        Map<String,String> demo = new HashMap<>();

        demo.put("text","Accelerating PyTorch distributed fine-tuning with Intel technologies");

        String tect = "基于MarianMT框架，在通用文本语料上做英翻中任务。";
        String scene = "通用文本英翻中";
        ModelApiReq req = ModelApiReq.builder()
                .modelName(modelName)
                .image(image)
                .apiUrl(apiUrl)
                .demo(JSON.toJSONString(demo))
                .pageDemo(null)
                .tectIntro(tect)
                .sceneIntro(scene)
                .style(style)
                .userId(userId)
                .groupId(groupId)
                .groupUuid(groupUuid)
                .baseModelUid(baseModelUid)
                .modelUpdateTime(new Date())
                .appShortName(appShortName)
                .build();
        return req;
    }

    private ModelApiReq getPhotoFixApiReq() {

        String image = imageFactory.getImage();
        String modelName = "老照片修复";
        Integer userId = 3;
//        userId = 20;
        String appShortName = "photo_fix";

        String apiUrl = "http://gbox5.aigauss.com:9512/infer";

        //虎博科技
        String groupUuid = "472538c74908d478bf7bc93956b5f50d";

        Integer groupId = 100000001;

        String style = Style.PHOTO_FIX.toString();

        String baseModelUid = "";

        Map<String,Object> pageDemoMap = null;

        Map<String,String> demo = new HashMap<>();

        demo.put("image","https://x-pai.algolet.com/model/photo_fix/sample_01.jpg");

        String tect = "基于生成对抗网络，对现实场景的图片，采用GFP-GAN和Real-Esrgan对分别对人脸和背景进行修复";
        String scene = "针对模糊图片，实现超分辨率修复。";
        ModelApiReq req = ModelApiReq.builder()
                .modelName(modelName)
                .image(image)
                .apiUrl(apiUrl)
                .demo(JSON.toJSONString(demo))
                .pageDemo(null)
                .tectIntro(tect)
                .sceneIntro(scene)
                .style(style)
                .userId(userId)
                .groupId(groupId)
                .groupUuid(groupUuid)
                .baseModelUid(baseModelUid)
                .modelUpdateTime(new Date())
                .appShortName(appShortName)
                .build();
        return req;
    }



    private ModelApiReq getAdApiReq() {

        String image = imageFactory.getImage();
        String modelName = "广告生成模型";
        Integer userId = 3;
//        userId = 20;

        String appShortName = "ad_gt";

        String apiUrl = "http://gbox4.aigauss.com:9516/infer";

        //虎博科技
        String groupUuid = "472538c74908d478bf7bc93956b5f50d";

        Integer groupId = 100000001;

        String style = Style.TEXT_TO_TEXT.toString();

        String baseModelUid = "qa_produce";

        Map<String,Object> pageDemoMap = null;

        Map<String,String> demo = new HashMap<>();

        demo.put("text","类型#裙*材质#蕾丝*风格#宫廷*图案#刺绣*图案#蕾丝*裙型#大裙摆*裙下摆#花边*裙袖型#泡泡袖");

        String tect = "基于mt5预训练文本生成模型，在开源AdvertiseGen广告文案生成数据集上做fine-tune。";
        String scene = "根据key-value关键词输入生成开放式广告文案";
        ModelApiReq req = ModelApiReq.builder()
                .modelName(modelName)
                .image(image)
                .apiUrl(apiUrl)
                .demo(JSON.toJSONString(demo))
                .pageDemo(null)
                .tectIntro(tect)
                .sceneIntro(scene)
                .style(style)
                .userId(userId)
                .groupId(groupId)
                .groupUuid(groupUuid)
                .baseModelUid(baseModelUid)
                .modelUpdateTime(new Date())
                .appShortName(appShortName)
                .build();
        return req;
    }


    private ModelApiReq getXueqiuApiReq() {

        String image = imageFactory.getImage();
        String modelName = "涉政舆情审核";
        Integer userId = 3;
        userId = 600;
        String appShortName = "xq_sentiment_audit";

        String apiUrl = "http://gbox4.aigauss.com:9527/infer";

        //虎博科技
        String groupUuid = "1583764959000691";

        Integer groupId = 100000093;

        String style = Style.TEXT_TO_LABEL.toString();

        String baseModelUid = "text_classification";

        Map<String,Object> pageDemoMap = null;

        Map<String,Object> demo = new HashMap<>();

        demo.put("text","福建号航母下水之际，中国与美国围绕台湾问题的争端和对峙正在愈演愈烈。");

        String tect = "基于NLP文本分类技术，审核新闻舆情";
        String scene = "自动识别新闻舆情中的涉政内容";
        ModelApiReq req = ModelApiReq.builder()
                .modelName(modelName)
                .image(image)
                .apiUrl(apiUrl)
                .demo(JSON.toJSONString(demo))
                .pageDemo(null)
                .tectIntro(tect)
                .sceneIntro(scene)
                .style(style)
                .userId(userId)
                .groupId(groupId)
                .groupUuid(groupUuid)
                .baseModelUid(baseModelUid)
                .modelUpdateTime(new Date())
                .appShortName(appShortName)
                .build();
        return req;
    }

    private ModelApiReq getArtImageApiReq() {

        String image = imageFactory.getImage();
        String modelName = "生成艺术图";
        Integer userId = 3;
        userId = 3;
        String appShortName = "art_image";

        String apiUrl = "http://gbox9.aigauss.com:9500/infer";

        //虎博科技
        String groupUuid = "472538c74908d478bf7bc93956b5f50d";

        Integer groupId = 100000001;

        String style = Style.ART_IMAGE.toString();

        String baseModelUid = "image_generate";

        Map<String,Object> pageDemoMap = null;

        Map<String,Object> demo = new HashMap<>();


        Map<String,Object> map = new HashMap<>();
        map.put("text","星空下的草莓");
        demo.put("inputParam",map);

        String tect = "VQGAN+CLIP Text To Image Art";
        String scene = "给定文本参数，输出图片";
        ModelApiReq req = ModelApiReq.builder()
                .modelName(modelName)
                .image(image)
                .apiUrl(apiUrl)
                .demo(JSON.toJSONString(demo))
                .pageDemo(null)
                .tectIntro(tect)
                .sceneIntro(scene)
                .style(style)
                .userId(userId)
                .groupId(groupId)
                .groupUuid(groupUuid)
                .baseModelUid(baseModelUid)
                .modelUpdateTime(new Date())
                .appShortName(appShortName)
                .build();
        return req;
    }


    private ModelApiReq getHuPuLabelApiReq() {

        String image = imageFactory.getImage();
        String modelName = "互联网社区UGC文本内容审核";
        Integer userId = 3;
        userId = 20;
        userId = 34;//周勇

        String appShortName = "hp_ugc_audit";

        String apiUrl = "http://gbox3.aigauss.com:8547/ch_classify/classify";

        //虎博科技
        String groupUuid = "38db1d39cc723be9a739f88ff8935829";

        Integer groupId = 100000002;

        String style = Style.TEXT_TO_LABEL.toString();

        String baseModelUid = "text_classification";

        Map<String,Object> pageDemoMap = null;

        Map<String,String> demo = new HashMap<>();

        demo.put("text","红色家族的韭菜罢了");

        String tect = "基于NLP文本分类技术，自动检测文本中涉政，谩骂，色情等违规内容。";
        String scene = "不仅能识别涉政等违规问题，也能灵敏识别嘲讽、谩骂等违反论坛规则问题。";
        ModelApiReq req = ModelApiReq.builder()
                .modelName(modelName)
                .image(image)
                .apiUrl(apiUrl)
                .demo(JSON.toJSONString(demo))
                .pageDemo(null)
                .tectIntro(tect)
                .sceneIntro(scene)
                .style(style)
                .userId(userId)
                .groupId(groupId)
                .groupUuid(groupUuid)
                .baseModelUid(baseModelUid)
                .appShortName(appShortName)
                .build();
        return req;
    }

    private ModelApiReq getImageLabelApiReq() {

        String image = imageFactory.getImage();
        String modelName = "图片分类";
        Integer userId = 3;
//        userId = 20;

        String appShortName = "image_label";

        String apiUrl = "http://gbox5.aigauss.com:9505/infer";

        //虎博科技
        String groupUuid = "472538c74908d478bf7bc93956b5f50d";

        Integer groupId = 100000001;

        String style = Style.IMAGE_LABEL.toString();

        String baseModelUid = "image_label";

        Map<String,Object> pageDemoMap = new HashMap<>();

        List<String> images = Arrays.asList("https://x-pai.algolet.com/model/sample/image_label/umbrella.jpg",
                "https://x-pai.algolet.com/model/sample/image_label/strawberry.jpg",
                "https://x-pai.algolet.com/model/sample/image_label/sunflowler.jpg",
                "https://x-pai.algolet.com/model/sample/image_label/umbrella.jpg");
        List<Map<String,String>> list = new ArrayList<>();
        for (String s : images) {
            Map<String,String> map = new HashMap<>();
            map.put("imageUrl",s);
            list.add(map);
        }
        pageDemoMap.put("samples", list);

        Map<String,String> demo = new HashMap<>();
        demo.put("imageUrl",images.get(0));


        String tect = "基于Vision Transformer视觉预训练模型，在开源数据集CALTECH_101（共101个类别）上微调，以实现视觉图片分类。";
        String scene = "对图片进行分类";
        ModelApiReq req = ModelApiReq.builder()
                .modelName(modelName)
                .image(image)
                .apiUrl(apiUrl)
                .demo(JSON.toJSONString(demo))
                .pageDemo(JSON.toJSONString(pageDemoMap))
                .tectIntro(tect)
                .sceneIntro(scene)
                .style(style)
                .userId(userId)
                .groupId(groupId)
                .groupUuid(groupUuid)
                .baseModelUid(baseModelUid)
                .appShortName(appShortName)
                .build();
        return req;
    }

    private class Image{
        String imageUrl;
    }
    /**
     *
     * id =20 wei.cai
     * @return
     */
    private ModelApiReq getZeroSampleApiReq() {

        String image = imageFactory.getImage();
        String modelName = "英文零样本分类";
        Integer userId = 3;
        userId = 20;

        String appShortName = "en_zeroshot";
        String apiUrl = "http://gbox4.aigauss.com:9506/infer";

        //虎博科技
        String groupUuid = "472538c74908d478bf7bc93956b5f50d";

        Integer groupId = 100000001;

        String style = Style.ZERO_SAMPLE_LABEL.toString();

        String baseModelUid = "text-classification";

        Map<String,Object> demoMap = new HashMap<>();

        String text = "At The Impact Seat, we invest in both innovation and in forward-thinking founders. We’re so thrilled to announce Arria NLG’s acquisition of our portfolio company Boost Sport AI — congrats to Boost’s phenomenal leaders: Mustafa Abdul-Hamid (now EVP, Arria) and Inga Nakhmanson (now EVP of Product Engineering, Arria)!In partnering with Arria, the two companies formed a strategic alliance artificial intelligence (AI) platform that empowered content creators to build and deploy data-driven sports stories. Arria Boost empowers brands to maximize audience engagement through personalized, 1:1 conversations delivered by technology experts, proprietary sports analytics, and industry-leading linguistics.";
        demoMap.put("text", text);


        List<String> candidateLabels = new ArrayList<>();

        candidateLabels.addAll(Arrays.asList("Natural Language Processing",
                "Blockchain",
                "Reinforcement Learning",
                "AI Cloud Services",
                "Financial Services",
                "NFT"));
        demoMap.put("candidate_labels",candidateLabels);


        String tect = "基于大规模预训练语言模型large roberta, 通过多语言MNLI数据集构建推理text entailment 任务, 实现zero-shot(零样本) or few-shot(少样本) 分类。";
        String scene = "在无样本或者少样本的情况下，根据标签的语义信息，实现文本分类任务。";
        ModelApiReq req = ModelApiReq.builder()
                .modelName(modelName)
                .image(image)
                .apiUrl(apiUrl)
                .demo(JSON.toJSONString(demoMap))
                .tectIntro(tect)
                .sceneIntro(scene)
                .style(style)
                .userId(userId)
                .groupId(groupId)
                .groupUuid(groupUuid)
                .baseModelUid(baseModelUid)
                .appShortName(appShortName)
                .build();
        return req;
    }



}
