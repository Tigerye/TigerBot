package com.tigerobo.pai.biz.test.model;

import com.alibaba.fastjson.JSON;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.dto.model.ModelCategoryDto;
import com.tigerobo.x.pai.api.enums.Style;
import com.tigerobo.x.pai.api.biz.entity.Tag;
import com.tigerobo.x.pai.api.vo.biz.ModelApiReq;
import com.tigerobo.x.pai.biz.biz.service.WebModelService;
import com.tigerobo.x.pai.biz.utils.IdGenerator;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelTest extends BaseTest {

    @Autowired
    private WebModelService webModelService;

    @Test
    public void tagTest(){

        List<ModelCategoryDto> basicTags = webModelService.getBasicTags();

        System.out.println(JSON.toJSONString(basicTags));
    }

    @Test
    public void addModelTest(){
//
//        doAdd(getShortText());
//        doAdd(getLongText());
//        doAdd(getCorrectReq());
//        doAdd(getQuestionUnderstandReq());
//        doAdd(getSummaryReq());
//        doAdd(getGameQuestionUnderstandReq());
//        doAdd(getBixinApiReq());
//        doAdd(getGameYongJieWuJianQuestionUnderstandReq());

//        doAdd(getDiYIDanApiReq());
//        doAdd(getUKIApiReq());
//        doAdd(getCaiJingApiReq());
//        doAdd(getZeroSampleApiReq());

//        doAdd(getProduceQaApiReq());
//        doAdd(getLiangShanQaApiReq());
//        doAdd(getYingMiUgcApiReq());
        doAdd(getGPT3Req());
    }



    private ModelApiReq getGPT3Req() {
        String taskUuid = IdGenerator.getId();
        Integer userId = 3;
        String apiUrl = "http://gbox3.aigauss.com:8547/ch_classify/classify";
        apiUrl = "http://gbox9.aigauss.com:9206/infer";

        String style =Style.TEXT_TO_TEXT.toString();

        Map<String,Object> map = new HashMap<>();
        String demo = "{\"text\":\"杭州位于\"}";

        String tect = "中文GPT2-3.5B";
        String scene = "文本生成";

        String appShortName = "text_generate";
        Date modelUpdateTime = new Date();
        ModelApiReq req = ModelApiReq.builder()
                .taskUuid(taskUuid)
                .apiUrl(apiUrl)
                .demo(demo)
                .tectIntro(tect)
                .sceneIntro(scene)
                .style(style)
                .baseModelUid("qa_produce")
                .appShortName(appShortName)
                .modelUpdateTime(modelUpdateTime)
                .userId(userId).build();
        return req;
    }


    private ModelApiReq getYingMiHeiZuiApiReq() {
        String taskUuid = "3f5680293526902c02468c8456bb970e";
        Integer userId = 20;
        String apiUrl = "http://gbox3.aigauss.com:8547/ch_classify/classify";
        apiUrl = "http://121.46.232.167:9305/ch_classify/classify";

        String style =Style.TEXT_TO_LABEL.toString();

        Map<String,Object> map = new HashMap<>();
        String demo = "{\"text\":\"我带小白入股市（实盘分享）小白股民，速来\"}";

        String tect = "基于NLP文本分类技术，审核其是否是财经类信息违规";
        String scene = "识别基金平台涉嫌金融“黑嘴”违规资讯，包括恶意唱空或哄抬个股价格及扰乱正常市场秩序的潜在违规资讯。";

        String appShortName = "ym_heizui";
        Date modelUpdateTime = new Date();
        ModelApiReq req = ModelApiReq.builder().taskUuid(taskUuid)
                .apiUrl(apiUrl)
                .demo(demo)
                .tectIntro(tect)
                .sceneIntro(scene)
                .style(style)
                .baseModelUid("text_classification")
                .appShortName(appShortName)
                .modelUpdateTime(modelUpdateTime)
                .userId(userId).build();
        return req;
    }



    private ModelApiReq getYingMiUgcApiReq() {
        String taskUuid = "43a7062ba085d390cc660eb8e24a02cf";
        Integer userId = 20;
        String apiUrl = "http://gbox3.aigauss.com:8547/ch_classify/classify";

        String style =Style.TEXT_TO_LABEL.toString();

        Map<String,Object> map = new HashMap<>();
        String demo = "{\"text\":\"红色家族的韭菜罢了\"}";

        String tect = "基于NLP文本分类技术，自动检测文本中涉政，谩骂，色情等违规内容。";
        String scene = "不仅能识别涉政等违规问题，也能灵敏识别嘲讽、谩骂等违反论坛规则问题。";

        String appShortName = "ym_ugc";
        Date modelUpdateTime = new Date();
        ModelApiReq req = ModelApiReq.builder().taskUuid(taskUuid)
                .apiUrl(apiUrl)
                .demo(demo)
                .tectIntro(tect)
                .sceneIntro(scene)
                .style(style)
                .baseModelUid("text_classification")
                .appShortName(appShortName)
                .modelUpdateTime(modelUpdateTime)
                .userId(userId).build();
        return req;
    }


    private ModelApiReq getTransApiReq() {
        String taskUuid = "f5f7632b8e1336ec4920e66cefe301a8";
        Integer userId = 20;
//        userId = 20;
//        userId = 75;
        String apiUrl = "http://gbox4.aigauss.com:9509/infer";

        String style = Style.TEXT_TO_TEXT.toString();

        String demo = "{\"text\": \"Halo Dunia\"}";
        String tect = "基于MarianMT框架，在文本语料上做印尼语翻译成英文任务。";
        String scene = "将印尼语翻译成英文";
        String appShortName = "translate_id2en";
        ModelApiReq req = ModelApiReq.builder().taskUuid(taskUuid)
                .apiUrl(apiUrl)
                .demo(demo)
                .tectIntro(tect)
                .sceneIntro(scene)
                .style(style)
                .userId(userId)
                .appShortName(appShortName)
                .build();
        return req;
    }



    private ModelApiReq getLiangShanQaApiReq() {
        String taskUuid = "f09553a40b0029fedf4255419525b6e7";
        Integer userId = 20;
        String apiUrl = "http://gbox7.aigauss.com:8199/qa";

        String style =Style.QA_DOMAIN.toString();

        Map<String,Object> map = new HashMap<>();
        String demo = "{\"text\": \"智能小额农贷和传统小额农贷的区别\"} ";

        String tect = "基于BERT中文预训练模型，添加span分类层，在公开问答数据集上做fine-tune，训练出提取式QA模型。通过问题在海量数据中搜索候选文档，然后通过QA模型提取出答案。";
        String scene = "银行问答机器人。";

        String appShortName = "lsyh_qa";
        ModelApiReq req = ModelApiReq.builder().taskUuid(taskUuid)
                .apiUrl(apiUrl)
                .demo(demo)
                .tectIntro(tect)
                .sceneIntro(scene)
                .style(style)
                .baseModelUid("machine_qa")
                .appShortName(appShortName)
                .userId(userId).build();
        return req;
    }


    private ModelApiReq getProduceQaApiReq() {
        String taskUuid = "e29e4647f12db1b13b83ca696378d190";
        Integer userId = 3;
//        userId = 20;
//        userId = 75;
        String apiUrl = "http://gbox5.aigauss.com:9504/infer";

        String style = Style.TEXT_TO_QA.toString();

        String demo = "{\"text\": [\"黄独（学名：）为薯蓣科薯蓣属的植物。多年生缠绕藤本。地下有球形或圆锥形块茎。叶腋内常生球形或卵圆形珠芽，大小不一，外皮黄褐色。心状卵形的叶子互生，先端尖锐，具有方格状小横脉，全缘，叶脉明显，7-9条，基出；叶柄基部扭曲而稍宽，与叶片等长或稍短。夏秋开花，单性，雌雄异株，穗状花序丛生。果期9-10月。分布于大洋洲、朝鲜、非洲、印度、日本、台湾、缅甸以及中国的江苏、广东、广西、安徽、江西、四川、甘肃、云南、湖南、西藏、河南、福建、浙江、贵州、湖北、陕西等地，生长于海拔300米至2,000米的地区，多生于河谷边、山谷阴沟或杂木林边缘，目前尚未由人工引种栽培。在美洲也可发现其踪迹，对美洲而言是外来种，有机会在农田大量繁殖，攀上高树争取日照。英文别名为air potato。黄药（本草原始），山慈姑（植物名实图考），零余子薯蓣（俄、拉、汉种子植物名称），零余薯（广州植物志、海南植物志），黄药子（江苏、安徽、浙江、云南等省药材名），山慈姑（云南楚雄）\"]}";
        String tect = "基于多语言mt5-small模型，通过问题生成数据集构建seq2seq生成任务。生成问题之后结合上下文通过阅读理解模型抽取出答案。";
        String scene = "给定上下文，生成对应的问题与答案。 ";
        ModelApiReq req = ModelApiReq.builder().taskUuid(taskUuid)
                .apiUrl(apiUrl)
                .demo(demo)
                .tectIntro(tect)
                .sceneIntro(scene)
                .style(style)
                .userId(userId).build();
        return req;
    }

    private ModelApiReq getZeroSampleApiReq() {
        String taskUuid = "c774b695cabf246c5eee3c5991945132";
        Integer userId = 105;
        userId = 20;
//        userId = 34;
        String apiUrl = "http://gbox4.aigauss.com:9505/infer";

        String style = Style.ZERO_SAMPLE_LABEL.toString();
        String demo = "{\"text\":\"《小王子》是法国作家安托万·德·圣·埃克苏佩里于1942年写成的著名儿童文学短篇小说。本书的主人公是来自外星球的小王子。书中以一位飞行员作为故事叙述者，讲述了小王子从自己星球出发前往地球的过程中，所经历的各种历险。作者以小王子的孩子式的眼光，透视出成人的空虚、盲目，愚妄和死板教条，用浅显天真的语言写出了人类的孤独寂寞、没有根基随风流浪的命运。同时，也表达出作者对金钱关系的批判，对真善美的讴歌。\",\"candidate_labels\":[\"儿童读物\",\"提升勇气\",\"乡愁\",\"言情小说\",\"成长\",\"畅销作品\"]} ";

        String tect = "基于大规模预训练语言模型large roberta, 通过多语言MNLI数据集构建推理text entailment 任务, 实现zero-shot(零样本) or few-shot(少样本) 分类。";
        String scene = "在无样本或者少样本的情况下，根据标签的语义信息，实现文本分类任务。 ";
        ModelApiReq req = ModelApiReq.builder().taskUuid(taskUuid)
                .apiUrl(apiUrl)
                .demo(demo)
                .tectIntro(tect)
                .sceneIntro(scene)
                .style(style)
                .userId(userId).build();
        return req;
    }

    private ModelApiReq getCaiJingApiReq() {
        String taskUuid = "454ab190cb547e78978fda8d0252f1b0";
        Integer userId = 105;
        userId = 3;
        userId = 34;
        String apiUrl = "http://121.46.232.167:9305/ch_classify/classify";

        String style = "TEXT_TO_LABEL";
        style = "TEXT_TO_LABEL";
        String demo = "{\"text\": \"我带小白入股市（实盘分享）小白股民，速来\"} ";


        String tect = "基于NLP文本分类技术，审核其是否是财经类信息违规";
        String scene = "针对财经类“自媒体”账号、主要公众账号平台、主要商业网站平台财经版块、主要财经资讯平台等网上传播主体，审核其是否违规采编发布财经类信息。 ";
        ModelApiReq req = ModelApiReq.builder().taskUuid(taskUuid)
                .apiUrl(apiUrl)
                .demo(demo)
                .tectIntro(tect)
                .sceneIntro(scene)
                .style(style)
                .userId(userId).build();
        return req;
    }
    private ModelApiReq getUKIApiReq() {
        String taskUuid = "2b60e183059894ea813ae259b9f2e4af";
        Integer userId = 105;
        userId = 3;
        userId = 23;
        String apiUrl = "http://121.46.232.167:8864/ch_classify/classify";

        String style = "TEXT_TO_LABEL";
        style = "TEXT_TO_LABEL";
        String demo = "{\"text\": \"有人吗，无聊中，一起聊聊\"} ";


        String tect = "基于BERT中文预训练模型，添加sequence分类层，在分类数据集上做fine-tune。";
        String scene = "基于NLP文本分类技术，自动检测文本中涉政，谩骂，软色情等违规内容。 ";
        ModelApiReq req = ModelApiReq.builder().taskUuid(taskUuid)
                .apiUrl(apiUrl)
                .demo(demo)
                .tectIntro(tect)
                .sceneIntro(scene)
                .style(style)
                .userId(userId).build();
        return req;
    }


    private ModelApiReq getDiYIDanApiReq() {
        String taskUuid = "9f596822f6b01b9abbc751a4b194689b";
        Integer userId = 105;
        userId = 3;
        userId = 34;
        String apiUrl = "http://10.0.8.99:9301/ch_classify/classify";

        String style = "TEXT_TO_LABEL";
        style = "TEXT_TO_LABEL";
        String demo = "{\"text\": \"烧杀抢掠样样通\"} ";


        String tect = "基于NLP文本分类技术，自动检测文本中涉政，谩骂，色情等违规内容。";
        String scene = "基于NLP文本分类技术，自动检测文本中涉政，谩骂，色情等违规内容。";
        ModelApiReq req = ModelApiReq.builder().taskUuid(taskUuid)
                .apiUrl(apiUrl)
                .demo(demo)
                .tectIntro(tect)
                .sceneIntro(scene)
                .style(style)
                .userId(userId).build();
        return req;
    }

    private ModelApiReq getBiXinApiReq() {
        String taskUuid = "9b8c4dcb7a62db981bb09b8d5d403cd3";
        Integer userId = 105;
        userId = 3;
        userId = 34;
        String apiUrl = "http://gbox3.aigauss.com:8547/ch_classify/classify";

        String style = "TEXT_TO_LABEL";
        style = "TEXT_TO_LABEL";
        String demo = "红色家族的韭菜罢了";


        String tect = "基于NLP文本分类技术，自动检测文本中涉政，谩骂，色情等违规内容。";
        String scene = "基于NLP文本分类技术，自动检测文本中涉政，谩骂，色情等违规内容。";
        ModelApiReq req = ModelApiReq.builder().taskUuid(taskUuid)
                .apiUrl(apiUrl)
                .demo(demo)
                .tectIntro(tect)
                .sceneIntro(scene)
                .style(style)
                .userId(userId).build();
        return req;
    }


    private void doAdd(ModelApiReq req) {

            webModelService.completeModel(req);
    }
    private ModelApiReq getGameYongJieWuJianQuestionUnderstandReq() {
        String taskUuid = "9f267a26380cca7dd9e393cc075a59e9";
        Integer userId = 11;
        String apiUrl = "http://gbox5.aigauss.com:8001/query";

        String style =Style.QA_DOMAIN.toString();

        Map<String,Object> map = new HashMap<>();
        String demo = "{\"text\": \"属于弓箭类型的魂玉有\"} ";

        String tect = "基于BERT中文预训练模型，添加span分类层，在公开问答数据集上做fine-tune，训练出提取式QA模型。通过问题在海量数据中搜索候选文档，然后通过QA模型提取出答案。";
        String scene = "游戏社交APP的群聊、私聊或社区中，当用户询问问答机器人爆款游戏的官方公告、攻略、世界观、剧情等资讯时，问答机器人能及时、准确回答。";
        ModelApiReq req = ModelApiReq.builder().taskUuid(taskUuid)
                .apiUrl(apiUrl)
                .demo(demo)
                .tectIntro(tect)
                .sceneIntro(scene)
                .style(style)
                .userId(userId).build();
        return req;
    }



    private ModelApiReq getGameQuestionUnderstandReq() {
        String taskUuid = "3082db3d535ccb5f1b64aa26bc5294ac";
        Integer userId = 78;
        String apiUrl = "http://gbox5.aigauss.com:8000/query";

        String style =Style.QA_DOMAIN.toString();

        Map<String,Object> map = new HashMap<>();
        String demo = "{\"text\": \"豹尾多少钱\"} ";

        String tect = "基于BERT中文预训练模型，添加span分类层，在公开问答数据集上做fine-tune，训练出提取式QA模型。通过问题在海量数据中搜索候选文档，然后通过QA模型提取出答案。";
        String scene = "游戏社交APP的群聊、私聊或社区中，当用户询问问答机器人爆款游戏的官方公告、攻略、世界观、剧情等资讯时，问答机器人能及时、准确回答。";
        ModelApiReq req = ModelApiReq.builder().taskUuid(taskUuid)
                .apiUrl(apiUrl)
                .demo(demo)
                .tectIntro(tect)
                .sceneIntro(scene)
                .style(style)
                .userId(userId).build();
        return req;
    }

    private ModelApiReq getSummaryReq() {
        String taskUuid = "121fbd179ed325badb918bde5f997ccd";
        Integer userId = 11;
        String apiUrl = "http://gbox5.aigauss.com:9503/infer";

        String style =Style.TEXT_TO_TEXT.toString();

        Map<String,Object> map = new HashMap<>();
        String value = "A survey of Fortune 500 companiesIn a separate study, CSA also found that 76% of global consumers prefer products with information in their native language, and two out of three use online translation tools such as Google Translate to understand content and communications in languages outside their own. How can a company create loyal customers if they expect buyers to do the translation work themselves? Fixing this type of language inequality in an English-centric business world requires companies to provide high-quality, seamless multilingual translations that offer a native-level customer experience for everyone. In a separate studylanguage inequality";

        value = "For more than 70 years, plasma physicists have dreamed of controlled “breakeven” fusion, where a system is capable of releasing more energy in a fusion reaction than it takes to initiate and sustain those reactions. The challenge is that the reactor must create a plasma at a temperature of tens of millions of degrees, which requires a highly complex, finely tuned system to confine and sustain. Further, creating the plasma and maintaining it, requires substantial amounts of energy, which, to date, have exceeded that released in the fusion reaction itself. Nevertheless, if a “breakeven” system could be achieved, it could provide ample zero-carbon electricity, the potential impact of which has driven interest by government laboratories, such as ITER and the National Ignition Facility, as well as several privately funded efforts.\n" +
                "\n" +
                "Today we highlight two recently published papers arising from our collaboration with TAE Technologies1, which demonstrate exciting advancements in the field. In “Overview of C-2W: High-temperature, steady-state beam-driven field-reversed configuration plasmas,” published in Nuclear Fusion, we describe the experimental program implemented by TAE, which leverages our improved version of the Optometrist Algorithm for machine optimization. Due in part to this contribution, the current state-of-the-art reactor is able to achieve plasma lifetimes up to three times longer than its predecessor. In “Multi-instrument Bayesian reconstruction of plasma shape evolution in the C-2W experiment,” published in Physics of Plasmas, we detail new methods developed for analyzing indirect measurements of plasma to reconstruct its properties in detail. This work enabled us to better understand how instabilities in the plasma arise and to understand how to mitigate these perturbations in practice.";
        map.put("text", value);

        String tect = "基于mt5预训练文本生成模型，在摘要数据集上做fine-tune。";
        String scene = "对文章进行摘要生成";
        ModelApiReq req = ModelApiReq.builder().taskUuid(taskUuid)
                .apiUrl(apiUrl)
                .demo(JSON.toJSONString(map))
                .tectIntro(tect)
                .sceneIntro(scene)
                .style(style)
                .userId(userId).build();
        return req;
    }

    private ModelApiReq getQuestionUnderstandReq() {
        String taskUuid = "dae15034a221586008085a14466ce599";
        Integer userId = 11;
        String apiUrl = "http://gbox5.aigauss.com:9502/infer";

        String style =Style.CONTENT_UNDERSTAND.toString();

        Map<String,Object> map = new HashMap<>();
        String demo = "{\"question\": \"飞机上充电宝不能超过多少毫安\", \"context\": \"据机场最新规定，充电宝超过2万毫安不得上飞机，要被机场没收，根据民航安全要求，旅客乘机时，充电宝等锂电池产品，其单块额定能量值最好不超过100Wh(瓦特小时)，且必须随身携带，不能托运。微博发布者根据“初中物理”计算得出，2万毫安的充电宝额定能量值“正好”是100Wh，因此不能上飞机。\"}";

        String tect = "基于BERT中文预训练模型，添加span分类层，在公开问答数据集上做fine-tune，训练出提取式QA模型";
        String scene = "根据问题从文章中找出一个连续的片段作为答案";
        ModelApiReq req = ModelApiReq.builder().taskUuid(taskUuid)
                .apiUrl(apiUrl)
                .demo(demo)
                .tectIntro(tect)
                .sceneIntro(scene)
                .style(style)
                .userId(userId).build();
        return req;
    }
    private ModelApiReq getCorrectReq() {
        String taskUuid = "2989e8f4aba1ffea166e55157d8352c7";
        Integer userId = 105;
        userId = 20;
//        userId = 2;
        String apiUrl = "http://gbox5.aigauss.com:9501/infer";

        String style = "TEXT_TO_LABEL";
        style = Style.TEXT_CORRECT.toString();
        String demo = "任总向顾客道千，保证不会出先损失";
        Map<String,String> map = new HashMap<>();
        map.put("text",demo);

        String scene = "识别短视频标题中的错别字，基于语义理解给出正确的用语。";
        String tect = "基于Roberta中文预训练模型，添加language modeling head，训练出masked language模型。";
        ModelApiReq req = ModelApiReq.builder().taskUuid(taskUuid)
                .apiUrl(apiUrl)
                .demo(JSON.toJSONString(map))
                .style(style)
                .sceneIntro(scene)
                .tectIntro(tect)
                .userId(userId).build();
        return req;
    }


    private ModelApiReq getModelApiReq() {
        String taskUuid = "0d5aedc77549cd44fbb1d1a6f4e76c1a";
        Integer userId = 105;
        userId = 3;
        userId = 2;
        String apiUrl = "http://gbox9.aigauss.com:9501/infer";

        String style = "TEXT_TO_LABEL";
        style = "TEXT_TO_ENTITY";
        String demo = "安踏体育中报点评：上半年业绩超之前预告，各品牌表现均有亮点！nn安踏发布21H1中报，公司实现营业收入228亿元，同比增长55.5%，实现净利润38.4亿元，同比增长131.6%，扣除Amer亏损后净利润为41.9亿元，同比增长76.1%，超出业绩预喜指引。nn安踏主品牌：受DTC转型影响，收入毛利实现高增，巩固国内运动品牌领导地位。安踏主品牌实现收入105.8亿元，同比增长56.1%，分业务来看，电商渠道继续高增，同比增长70.4%至36.1亿元，批发业务同比下滑30%至32.7亿（部分业务转入DTC），DTC业务实现收入37亿元；受高毛利率的DTC业务占比提升影响，公司毛利率同比增长11.2 pcts至52.8%；经营利润率方面，由于DTC导致店铺层面租赁费用及员工成本增加，公司实现经营利润率23.1%，同比下滑4.3 pcts。nnFILA品牌：销售持续快乐增长，折扣恢复+经营效率提升，盈利能力改善明显。FILA品牌实现收入108.3亿元，同比增长51.4%，主要由电商渠道高增拉动。盈利能力方面，FILA品牌毛利率同比增长1.8 pcts至72.3%，主要由终端零售折扣改善所致；受益于经营效率的提升，经营利润率同比增长4.3 pcts至29%。nn迪桑特可隆维持高增，Amer大幅减亏。其他品牌收入同比增长90.1%至14.1亿元，其中迪桑特和可隆分别实现100%+和60%+增长；盈利能力方面，毛利率和经营利润率分别为70.4%和21%，同比提升5,9和18.4 pcts，其中可隆品牌实现扭亏。Amer Sports上半年实现亏损3.46亿元，同比大幅减亏，三大品牌（始祖鸟、萨洛蒙和威尔逊）相比2019年均有双位数增长；此外，Amer中国受始祖鸟品牌拉动，实现业绩翻番，未来表现值得期待。nn公司上半年业绩增长强劲，各品牌表现均有亮点，表明集团的“单聚焦、多品牌、全渠道”战略已初获成功，未来有望通过多层次的品牌矩阵，最大化享受运动赛道的高景气。";

        ModelApiReq req = ModelApiReq.builder().taskUuid(taskUuid)
                .apiUrl(apiUrl)
                .demo(demo)
                .style(style)
                .userId(userId).build();
        return req;
    }


    private ModelApiReq getNewWordModelApiReq() {
        String taskUuid = "4a2cc136d8fdfa5758ad4c8f8b43309d";
        Integer userId = 105;
        userId = 3;
        userId = 3;
        String apiUrl = "http://gbox7.aigauss.com:9581/new_words_discovery";

        String style = "TEXT_TO_LABEL";
        style = "KEY_TO_WORD";
        String demo = "";

        ModelApiReq req = ModelApiReq.builder().taskUuid(taskUuid)
                .apiUrl(apiUrl)
                .demo(demo)
                .style(style)
                .userId(userId).build();
        return req;
    }

}
