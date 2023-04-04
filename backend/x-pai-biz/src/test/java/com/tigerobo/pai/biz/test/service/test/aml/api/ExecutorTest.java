package com.tigerobo.pai.biz.test.service.test.aml.api;

import com.alibaba.fastjson.JSON;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.serving.vo.ApiReqVo;
import com.tigerobo.x.pai.api.serving.vo.ApiResultVo;
import com.tigerobo.x.pai.biz.aml.service.AmlApiServiceImpl;
import com.tigerobo.x.pai.biz.lake.LakeInferService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Slf4j
public class ExecutorTest extends BaseTest {

    @Autowired
    private LakeInferService lakeInferService;

    @Autowired
    private AmlApiServiceImpl amlApiService;
    @Test
    public void test()throws Exception{


        ApiReqVo apiReqVo = new ApiReqVo();
        apiReqVo.setApiKey("81073");

        Map<String,Object> map = new HashMap<>();

        String path = "https://x-pai.oss-cn-shanghai.aliyuncs.com/biz/evaluation/tmp/1-202111061611.xlsx";
        path = "https://x-pai.oss-cn-shanghai.aliyuncs.com/biz/evaluation/tmp/1-202111061611.xlsx";
//        path = "/mnt/黑产.xlsx";
        path = "/tmp/oss/is/1.csv";
        map.put("filePath", path);

        apiReqVo.setParams(map);
        ApiResultVo apiResultVo = amlApiService.batchEvaluate(apiReqVo, 3);

        System.out.println(JSON.toJSONString(apiResultVo));
    }

    @Test
    public void jsonApiTest(){
        String json = "{\"apiKey\":\"81073\",\"params\":{\"batchSize\":200,\"filePath\":\"https://x-pai.oss-cn-shanghai.aliyuncs.com/biz/evaluation/tmp/1-202111061611.xlsx\"},\"authorization\":{\"token\":\"d46f3f036cb0a722ef1c0f91f4b06fe0\",\"uid\":\"d2c1c4f00697ac39a4d8b9a4ca189d11\",\"gid\":null}}";


    }

    @Test
    public void callTest(){
        call();
    }
    private void call(){

        List<String> list = getList();

        System.out.println("list-size:"+list.size());

        ExecutorService readCachePool  = new ThreadPoolExecutor(100, 100,
                120L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(1000));

        long start = System.currentTimeMillis();
        CountDownLatch latch = new CountDownLatch(list.size());
        list.stream().forEach(s->{
            readCachePool.submit(new Runnable() {
                @Override
                public void run() {
                    try {

                        doCall(s);
                    }catch (Exception e){
                        log.warn("{},failed to getArticles",s, e);
                    }finally {
                        latch.countDown();
                    }
                }
            });

        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            log.error("getArticles error", e);
        }
        System.out.println("delta:"+(System.currentTimeMillis()-start));

    }

    private void doCall(String text){
        ApiReqVo reqVo = new ApiReqVo();
        reqVo.setParams(new HashMap<>());
        reqVo.setUserId(3);
        reqVo.getParams().put("text",text);
        reqVo.setApiKey("610767");
        ApiResultVo execute = amlApiService.apiExecute(reqVo,3);


        System.out.println(text+"-------\t"+JSON.toJSONString(execute));

    }

    private List<String> getList(){
        String[] split = getStr().split("\n");

        return Arrays.stream(split).map(s->JSON.parseObject(s).getString("sentence1")).collect(Collectors.toList());
    }

    private String getStr(){
        StringBuilder builder = new StringBuilder();

        builder.append("{\"sentence1\":\"为自己相个亲  姑娘希望你在生活中也是文字里流露出来的朴实模样 愿你在帝都一切顺利 万事胜意 你也一定会遇到你的老北鼻 加油 不加\uD83D\uDC36\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"霍林格对鹈鹕的观察评价 起码能多赢几场 多赢那几场能进季后赛吗？\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"官方正式公布Netflix海贼王真人电视剧选角！东海五人组集体公布！  娜美身材怎么样？？？？？？\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"荣耀了，我这种算肝帝么 感觉我也没怎么理解这些英雄，主要是看视野，打着打着就上去了。。。   强啊铁子\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"在外打拼的jr们，奋斗真的很幸福吗。 家里但凡能给的起钱买房子，就不要出去吃苦，除非你的能力是人中龙凤，感觉小地方承载不了你的大梦想。我反正不是有大报复的人，就是想安安逸逸过一辈子 今年23，家在甘肃，从18岁上大学开始出来，就回家过两次，江苏上学实习，工作在三亚，今年在温州和朋友开个小店，最近觉得顿悟了，考公还是进厂都想回家，那种感觉太强烈，尤其是最近知道朋友的妈妈得癌症，子欲望而亲不待……\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"【中文字幕】杜兰特：欧文可能会回来，谁知道了，但更衣室现在气氛挺好的  哈登只要恢复了，就还是总冠军级别的球队\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"我的草原我的马，我想咋耍就咋耍。  这脚\uD83E\uDDB650码？？？？\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"窗口期进国家队名单 赵睿肯定会去，徐杰被孙明辉替代。。。 睿的禁赛期还没有过，只是同去国家队训练，近期各俱乐部入选球员已经收到通知了，但睿大致不会选择去训练，所以。。。\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"博格坎普多强，和哪些球星同等级？  博格坎普实际业内评选历史地位。大多也在克林斯曼和巴蒂斯图塔两大情怀巨星上面。他最大错误就是去了国米这个球星黑洞，95阿贾克斯超级黄金一代，利特曼宁能力远不如他。他留在阿贾克斯，阿贾克斯就是一个新王朝。\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"迈巴赫这氛围灯，满满的夜店风啊 很难想象一些大boss 老总工作忙了一天愿意被这些赛博朋克环绕着一路回家.. 不会关?人均若智残疾\",\"label\":\"嘲讽谩骂引战\"}\n" +
                "{\"sentence1\":\"防三分啊…  三分我是搞不懂，那么轻松的出手\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"好冷清论坛，发个贴玩，GEX某职业选手直播中恶臭PCL战队 那咱们看的不是一场，我看的那场有yanli，他们跟17roll的。虽然他最后说不是说大陆，不过我觉得他就是在说PCL的。。因为他确实说的没错，我们一般就是连打带补，不过这句“恶心国家”。。真给我吐了，妈的lok这逼前几天还跟木子李rank。。。想想恶心 哦对了，这个叫lok是个香港人，不是台湾的。\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"求教家人们，胡行托砂这练度够深渊满星吗  够，建议去b站找一下0命匣里胡桃单通视频，看完之后就知道打不过去是自己最基本拉怪操作不行，跟伤害量无关。\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"巴特勒这波证明了他不是真正的强硬  知道打不起来，戏份做足，你要牛逼就场上直接干！\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"[流言板]约基奇被禁赛1场会让他损失210417美元 为什么挣不到，打工人一年3.6万。上个40年帮退休！月薪3000都能完成！ 到底中国有多少人能上40年班，中国人均20岁小年轻？\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"[流言板]怀特塞德：我开玩笑说多少球队替补中锋比对手首发中锋强  最强的白边是当年执着于2K评分的白边，动不动盖帽上双，签下大合同之后就王小二过年，一年不如一年了。\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"搬运。约基奇二哥TKO对手视频，现在二莫胜算几成？ 我寻思着这不才6尺6吗？ 我这三句话说的分别是三个兄弟\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"[谜语人]韩网流言：A想回到LCK，B在努力寻找一支球队，C得到许多LCK合同 卧槽，我去找了下，原来那时候烧烤摊掉级了，我没记清楚，我就记得以前有个外援打野啥的 你在看啥哦烧烤摊啥时候掉级哦，那是SKT二队\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"[流言板]某高管：现在各队都不想接触欧文，管理层不信任这种人 别闹了，他能发起什么进攻？  威少有突破有转换，没了威少队里还有谁现在能用的\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"选切特是很好，但是选后几年能上场是个大问题！不上就永远是潜力独角兽吧？ 几年能首发？ 第一年直接首发！第二年进全明星！\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"xdm marsell瑕疵求看  赚麻了\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"瓦哈尔：当世最强我会选梅西内马尔  这正常吧，马儿太容易伤病了，这样看梅西真是强中强\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"宋朝有名相，有名将，偏偏没有个明君！真是可惜  说到外交，可能许多人以为动辄对外族喊打喊杀才是正确路线，譬如明朝的天子守国门，君王死社稷，不割地，不赔款，不和亲但是这样一根筋的对外思想真的好么？大英帝国外交官表示有话要说。个人认为中国历史上最具外交手腕的朝代当数清朝前期（清末那是两千年未有之大变局另说）。然后是能屈能伸的汉唐。宋代起初用经济手段搞外交不失为一种基于实力考量的现实路径，不过是后来走了极端成了路径依赖。但是宋辽之间实现120年和平共存这一点在中国古代也是仅此一例值得关注。\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"保罗乔治打球也太丝滑了。  最近流行吹乔治？\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"久诚来了ag之后，应该感谢ag带着感恩的心的 ag（队员等）对久诚挺好的，是教练组不做人…… 换个教练组的事情，我实在不明白为什么久诚粉丝天天希望他转会，环境不好就换个环境哪有那么容易\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"[回帖抽奖]达尔优A710上手体验——这款游戏耳机体验如何？  分母＋1\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"带viper节奏的我想问一句  辟谣没人看，没人看也就算了还没人信\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"[流言板]何时退役？哈斯勒姆：上帝同意的话，想把数字从19变成20 是的，我知道哈队对于热火的贡献，当年本来有机会去独行侠拿高薪，但选择留在热火。他的精神确实对球队有激励作用，但哈队其实从17赛季开始基本上捞不到出场时间了，等于说做了几年啦啦队队长了，韦德19年退的时候我以为哈队要退役了，没想到又硬撑了几年。现实就是现在他就是除了资历最老，在更衣室骂骂年轻人激励一下，其他真的没什么用了，占着一个名额。卡特42岁还能贡献自己的力量，但他发现自己已经打不了球的时候也选择退役了，现在哈队再硬留着也挺尴尬的，已经四五年没球打占着个名额了 当时热火三巨头为了让哈队留下集体降薪，哈队在这待着估计也是能赚一年是一年，退役了也不知道干嘛吧\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"[流言板]约基奇因昨日比赛冲突被禁赛1场，大莫里斯被罚款5万美元  约架，推特上见。\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"湖勇鹿网分别是几核球队  篮网单核，哈登现在表现就普通首发级别\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"一张图看懂杜兰特单打无法带动球队 泰伦卢说库里是最危险的人你们就信了，那他在同一个系列赛说kd是前二的球星你们就不信了？还是你觉得他说的前二不包括他们的当家球星勒布朗詹姆斯？ 说库里那是三年后了，还是比赛完了以后得感官更真实吧\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"有些人有没有底线了？ 我上次说他 还被他喷了 习惯就好，他看谁都像阿尔斯兰球迷\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"[流言板]库里：追梦把球传给伊格达拉空接，让我想到了过去的勇士 还有这个呢！ 我是保罗我也吐了\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"詹姆斯职业生涯只有两次50分10次助攻，最近的一次2009年25岁的时候 第一詹姆斯的季后赛总得分达到了7491分，成为了NBA历史第一，可怕的是这项纪录依旧在不断创造的路上。第二詹姆斯季后赛抢断445个，历史第一，詹姆斯还能打几年NBA，纪录依旧在创新高的路途上。第三詹姆斯季后赛出手总数达到了5388个，历史第一，还在创造历史的路上。第四詹姆斯季后赛进球2671个，历史第一，还在继续前行中。第五詹姆斯季后赛三分出手1235个，历史第一，这几年詹姆斯的三分出手也比以前多了不少。第六詹姆斯季后赛得到罚球2341个，历史第一，詹姆斯两年的突破造犯规比以往少了，更依靠中远投篮。第七詹姆斯季后赛罚球命中数1735个，历史第一。第八詹姆斯季后赛出场数260场，历史第一，这个记录依旧在前行，只要詹姆斯保持稳定的状态，冲击季后赛保持稳定创造记录是没问题的。第九詹姆斯季后赛总出战10811分钟，历史第一，这十几年的季后赛也让詹姆斯积累了不少的上场时间，让不少人想起骑士时期泰伦卢的时候，季后赛对詹姆斯的过度使用。第十詹姆斯生涯14次季后赛得到至少250分+100篮板+75助攻，历史第一，这个怕是很少有球员能打破了，毕竟能像詹姆斯这样巅峰那么多年的球员已经很少见了。第十一詹姆斯两轮季后赛场均25分+10篮板+5助攻，35岁球员历史唯一人。第十二詹姆斯季后赛生涯15次得到单场30+大三双的数据，历史第一。第十三詹姆斯季后赛生涯9次得到至少500分，超越乔丹排名历史第一。第十四詹姆斯季后赛生涯6次至少500分+150个篮板+150个助攻，历史第一。第十五詹姆斯是35岁后依旧能在季后赛得到4次三双的球员，历史第一。第十六詹姆斯也是季后赛50次单场至少30+，超越乔丹成为历史第一。第十七詹姆斯成为NBA首位单赛季总决赛得到150分+50篮板+50助攻的球员。第十八詹姆斯总决赛生涯24次得到至少25分+10篮板的数据，历史第一多。第十九詹姆斯总决赛得到20次至少25分+10篮板+5助攻的数据，历史第一多。第二十詹姆斯季后赛生涯已经得到39次系列赛胜利，历史第一多。第二十一詹姆斯生涯已经有11场总决赛得分+篮板+助攻三项数据全部领跑全队，历史第一多。第二十二詹姆斯成为NBA历史第一位在三支球队季后赛场均25+并且带队杀进总决赛并且夺冠的球员。第二十三35岁零284天的詹姆斯成为NBA总决赛历史砍下40+最老的球员。第二十四35岁286天的詹姆斯，成为NBA总决赛得到三双最老的球员。第二十五詹姆斯是NBA历史第一位在三支球队分别夺冠并且荣膺FMVP的球员，前无古人。第二十六季后赛至少35分15篮板5助攻的数据，现役球员勒布朗-詹姆斯（7次）和凯文-杜兰特（3次）和乔治（2次）第二十七季后赛五次压哨绝杀 黑子：打的好能打到绝杀？\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"长平之战赵国面临的是死局吗？ 得嘞，还改自己的回复搜一下就知道，长平之战从来都是两个主题，一个说9个月，一个说三年实际上就是两边打太久，导致赵孝成王拖不下去一变议和一边借粮同时想着自己亲征，最后拿赵括换了廉颇虽然赵国产粮地不大，但打一年就没粮食了，猪都不信。你能给一个赵孝成王换帅的理由吗？ 所以赵国本来就不缺粮，你用缺粮来论证赵国打了三年，又用打了三年来证明赵国会缺粮，无限套娃呢。上面那段记载的很清楚，赵国从廉颇出战到赵括被围，也就几个月的时间。至于，赵王为什么换廉颇，记载的很清楚：1、赵王嫌弃廉颇打败仗，又不敢出站。2.中了秦国的反间计。你以为赵王是什么英明神武的明君？\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"现在cba交易价值最高的球员是谁 我不说，我说的是胡金秋，第一：CBA内线比外线稀缺，内线比外线值钱。第二郭艾伦年纪摆在那，30出头了，下面肯定是生理机能走下破路了，而且他的打法太吃身体，投射不是他的强项，生理机能下降以后对他的影响是比较大的，胡金秋20出头，正式当打之年，而且还有上升潜力。且是CBA铁人，整个职业生涯67年打下来就缺席了几场比赛，第三：胡金秋不争球权，不论到哪个球队都是即插即用，郭艾伦不管去了哪里都是要球权当老大，这点你也不得不考虑，第四：胡金秋老实孩子，说实话没有郭艾伦场外那么多事情。 废话多  你摆上去  自己让他们选  你自己看嘛   非得在这说年龄的问题   真的就差郭艾伦把身份证给你看了\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"不懂就问，杜兰特没有库里是不是就没有那两个冠军 不一样没fmvp吗 有总冠军啊\uD83C\uDFC6\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"韩方中路这么西都赢不了  输出再高，一个中单也不可能1v5啊\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"兄弟们看看有戏吗喜欢很久了  梅西\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"Alan直播时爆料cat因为没面子不回复微信 猫神至少当年法刺很猛，梦泪偷个家吹上天，真不觉得多强 很强的\uD83D\uDC31 当时也没稳压老帅呀\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"兄弟们分手了！！ 希望各位评论的时候不要人身攻击。。。。。我们是分手了，我们只是三观不合。可能像我一样，他的三观中我也是个异类。毕竟三观是用来约束自己的而不是绑架他人，我虽然不认同现在的她，但也是尊重的。各位开心的话可以口嗨，但请不要人生攻击。谢谢！ 祝你找到更好的人 成功上岸！\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"科尔：别利察有一次回更衣室路上突然回头 对库里说“我爱你” 我不同意你的说法嘶 你丫搁这秀口技呢\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"忆往昔峥嵘岁月稠：威少生涯30佳球！ 稍微歪一下楼，而且可能有些矫情，希望楼主不要介意，威密看完真的泪目了，想当年初中刚开始篮球的时候，自己偷偷练习威少最爱的几个动作比如shammgod，in and out，crossover等等，初三高一那段时间恰好是威少巅峰的那几年，杜兰特出走，威少mvp，每场都看的热血沸腾也恰好是那几年我自己球技涨得最快，从不会篮球只踢足球到慢慢成长成一个野球场“小腿”。是威少场上拼命的态度影响了我，让我成为了一个球场上求胜欲极强，球球必争，充满激情的人，而且个人的进攻风格逐渐变得像威少一样，记得那时候每次听到别人叫我小威少我都非常非常高兴再后来，雷霆重建，威少离队，实力下滑，慢慢地不敢看威少的比赛，怕看到他一个接一个的打铁失误而伤心，只敢在得知威少这场打的好的时候，看一遍比赛的回放或者集锦，欣赏一下他不知还有多久的巅峰，这时候我上了大学，膝盖脚踝经常受到伤病困扰，打球次数没那么多了，在球场上以组织和防守为主，自主进攻也逐渐变少了，也更佛系了，跟我一起打球的同学说我球场上脾气真好，打球也真无私，一直在传球不进攻，我一直没说出口我当年打球可不是这样的，毫不畏惧任何不利局势，毫不畏惧任何防守者才是曾经的我但是时间终将抹去一切的棱角，我亦如此或许，每个雷密心里都住着一个小威少吧，一个力挽狂澜的英雄，一个热血沸腾的青春至少 这个名为英雄的小人儿永远扎根在了我的心底 和你同一级哈哈，初三毕业杜兰特走了\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"猪八戒打不过李信正常吗?李信的伤害太超标了吧，还刮痧，这特么是刮骨吧 你根本不会玩，上来就把技能交完了，你不脆谁脆？玩猪要吃波伤害再交技能回血 操作这么烂还把李信打得剩一丝血，知道该玩什么了吧\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"刚才看到网评ig fpx edg夺冠位置排名，说说我自己的吧 闪现给中单挡一发打不死的子弹直接封神。\uD83D\uDC36 请严谨一点，是中单的假身\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"辛苦攒了296个皮肤碎片，要不要换电玩（ps:本人不玩鲁班），求解 我也不玩，但是我换了。 我也是\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"伍德发推：无论好坏我都会留在休斯顿 没那么夸张，老詹今年才拿4000w多一点，戴维斯更是3500w，伍德再怎么有自信，也不会估价超过3000w，友情价的话2500w吧（和这赛季15+8的柯林斯一样） 2500w真多了，柯林斯还比伍德年轻呢，我真觉得伍德赶紧换走，趁现在还有一定价值，一个首轮就行，俩首轮不现实\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"好冷清论坛，发个贴玩，GEX某职业选手直播中恶臭PCL战队 你看我帖子就有 那咱们看的不是一场，我看的那场有yanli，他们跟17roll的。虽然他最后说不是说大陆，不过我觉得他就是在说PCL的。。因为他确实说的没错，我们一般就是连打带补，不过这句“恶心国家”。。真给我吐了，妈的lok这逼前几天还跟木子李rank。。。想想恶心\",\"label\":\"生态不良\"}\n" +
                "{\"sentence1\":\"[流言板]卡鲁索：湖人给我的报价少于2年1500万美元 库兹马有代言人长得好看又潮流，头发还多还整各种颜色，可是卡皇你让他代言啥？ 假发套\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"票选lpl今年最受欢迎选手 theshy都比jiejie高？看来他的孩子们还真不少啊 建议你直接致电英雄联盟官方让他们规定不能投the shy，说不定你会收获一句动听的你寄吧谁\",\"label\":\"生态不良\"}\n" +
                "{\"sentence1\":\"教主强势宣布加入38大佐以及NANA中佐伪军部队  38的目的已经达到了，流量赚的盆满钵满\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"S赛冠军就能代表个人水平吗 难道运动比赛成绩就不重要了么？伊戈达拉fmvp，瓜哥没冠军，所以他比瓜哥水平高？可以这么说么 当年只看S赛成绩，其他不管的也是你们。现在不看S塞的也是你们。当年只看S塞，不就是uzi是亚军吗？一群人把lpl冠军和msi贬的一文不值。现在lpl3个S冠，又说不能只看S赛。你们到底以什么为标准啊。\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"[流言板]前往南方！哈登杜兰特登机，准备明日对阵魔术 哈不攻明天16分    7板  8助 5失误\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"AG超玩会打XYG第四局，JR心中的MVP是谁呢？？？ 但是也有亮眼的地方，几次喷大，还有杀公孙离那次，没有他，初晨肯定死 说实话这把几次大还可以 但是整体的那种感觉就不是玩张飞的…这关羽梦游了好几波换s的关羽初晨这把0-4我都不意外…一个张飞顶前面蹲草不给干扰看的真的挺难受的\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"还有人说去年雄鹿是“三巨头”？ 看看去年的比赛吧  关键时刻或者逆风球多少场是米豆站出来终结比赛的。字母很强，但是米豆 朱哥去年表现如果不算巨头的话 那没几个三巨头球队了。米豆去年季后赛的表现甚至比巅峰汤神还好 为什么你会被灭？？？？这个论坛是没有人看过比赛吗？？？字母哥东决都是躺在板凳席进的。雄鹿除了最后一场字母爆发，其他80%以上的关键球全是米德尔顿和霍乐迪打的。。。对方核心布克，特雷杨也是霍乐迪主防的。怎么滴，学勇士那一套，扭曲事实颠倒黑白？？？\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"2020年火箭打湖人的季后赛，几乎就是哈登和威少的缩影。 最骚的是湖人亲眼目睹威少有多坑，结果还4000万收 说是钱个赛季，其实还是在去年下半年\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"有西安的jrs吗 有推荐的公园吗 安静的干嘛 干\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"【深度】虽迟但到，复盘S11决赛，双方有哪些亮点 因为文字太长，觉得读起来太麻烦，并且不够直观 看起来就是每个人习惯不一样吧，我有的时候看视频都去找文字底稿\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"xdm 打版亨利领拉了吗 对 感觉大了 颜色是很灰的灰色吗？\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"同样都是空砍，为什么杜兰特和库里虎扑评价不同 既然可以是其他人为什么不能是库里？不是库里受到不公平待遇，上赛季那表现就该被骂，只不过别双标啊，联盟第一人带个MVP队友这战绩天天被夸是怎么回事？ 上赛季被骂是赛季结束后，这赛季结束了吗？库里那会5060赢不了球的时候说他尽力的帖子一大堆。还有，从哪看出来天天被夸的，从上赛季被喷三巨头二轮出局，到假期被喷热身赛两连败，再到这两天被喷高分不赢球，喷KD的帖子少过？\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"双11 |电脑DIY配置单 主观推荐（可直接照抄）  md，显卡贵死了\",\"label\":\"生态不良\"}\n" +
                "{\"sentence1\":\"【21-22赛季森林狼消息楼】  明天打蔡先生家 奥克罗应该可以复出了 勒夫好像也快了 马卡还没消息\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"心中无女人 出拳自然神。 乔杉啊？ 大侠好眼力\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"现在有多少夫妻是两地分居？  我同事一年回家6次，每次1周\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"iphone 蹲个价 3000\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"xyg秀豆第一视角正在直播 疫情原因 出事算你的? xyg也该在训练室打比赛\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"发个车reart的西服 之前发车确实540当时看了，我刚刚去看算了算，现在好像真是要580了，好像涨了 那他有会员为什么还要算540\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"巴特勒真硬啊 尼哥们就别吹硬了。。。巴特勒感觉和加内特有点一个路子的意思。。。在欧洲这些大白熊面前，真干起来，屁不敢放。。。啊芙拉罗装了一次硬汉，被别理查差点把脖子拧断，今天这大莫差点上担架，还吹呢？ 美国尼哥生的地方好，要是生在CIS那边，估计得让打回黑奴原型\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"这三个男人（及背后团队）互相暗杀，谁最后可以活下来 最近刚好在研究毒。中情局在50年代还是70年代确实用过氯胺酮还是啥的，通过洗脑培养了一批特工。最后好像因为效果不好，计划被叫停了。 大兄弟，什么专业的，要研究毒？\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"摆地摊新项目，10块钱一勺，什么水平  一袋5毛钱，20袋才能回本\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"[流言板]小莫里斯再发推：妈妈说不要再在社交媒体上说话了！  妈妈：养这么大的儿子，不想白白被人打死，你憋说话了\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"亚洲最大社区—天通苑到底住了多少人？  花果园更大吧\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"[流言板]MVP赔率：库里升至第一，杜兰特字母哥并列第二 绝对kd\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"若库里和威少互换，湖人勇士会是什么水平？ 五冠的嘛 留了一个给浓眉 他还年轻！ 好小子\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"如果席梦思来了  上一个这么高的阵容 正是76人逗逼 伙夫 拖把 席梦思 结果你知道了\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"三百万买个什么理财好一点 小卡特、赫尔特、马刺穆雷、小西蒙斯，这几个基本都能稳定涨一段时间，巴恩斯应该也能涨一点，但上限不高，除非加徽章 小西蒙斯和赫尔特选一个的话好兄弟推荐哪个啊\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"[流言板]字母哥打趣：2024年我要参选，我要成为历史上最帅的总统  你还能成为美国历史上第一个不是美国国籍的总统\uD83D\uDC36\",\"label\":\"生态不良\"}\n" +
                "{\"sentence1\":\"这小乔告诉她出错装备了，竟说我有病。 弱弱的说一句，影刃之足不算离谱…可减少司马懿艾琳普攻伤害，守护者确实离了大谱 如果是我玩射手，我可能会出两双鞋，或者优先抵抗鞋，毕竟对面司马懿火舞控制链也很难搞。\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"皮蓬四次总决赛20+，韦德欧文戴维斯四次总决赛25+，乔丹和詹姆斯谁的队友更好。 皮蓬8个一防 18个一防也没用，20年浓眉的防守端表现、防守端数据和高阶数据，限制对手效率，都是皮蓬比不了的。\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"关于杜兰特和库里的特点…… 阿德不是篮网的人吗？ 你再仔细看看\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"看到这赛季库里和杜兰特的表现，更加怀念这个时刻 那时候杜兰特不是已经准备要走了？球队不是禁赛格林了？你觉得库里应该做什么？库里没有让球权？ 恶人先告状，杜兰特跟腱断裂一段时间才确定要走的，合同年所有的事情都会被放大勇士的所作所为根本不是在留阿杜\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"[流言板]布登霍尔泽：我认为扬尼斯今天有额外的好胜心  毕竟MVP\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"Li-Ning 利刃2 勇气之刃 —— 很李宁很传统很扎实，但它一点都不过时 帮顶！我前几天学的拍图\uD83D\uDE00\uD83D\uDE00\uD83D\uDE00 趁着年轻赶紧改行吧\uD83D\uDC36\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"黄志忠的演技是不是爆了靳东？ 黄志忠的杨立仁、海瑞太经典了。 有力人士！\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"【21-22赛季森林狼消息楼】  去年建群时候我的群昵称就是班切罗，看看有没有机会\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"[流言板]伍德：罚球不佳完全是心理问题，很好解决  大言不惭\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"如果交易席梦思，我觉得除了双探花以外，其他人都可以摆上货架。  施罗德换普尔霍福德换围巾:斯马特换奥托波特?\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"模拟百年后中国各大城市的样子  我感觉这种设想不符合当前碳中和.可持续发展的理念，绿化都看不到，楼建的看不清天际线，这就叫发达吗？欧洲很多城市除了cbd也没有高楼大厦啊，人家去工业化都多少年了，我们的大城市反而往高碳排放方向发展？？？咱们国家有文化底蕴的城市多着呢，希望去工业化后能看到更多新中式公建\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"EDG是什么？我百度以后，看遍新闻，心中有一话不吐不快。 观点没错，大肆宣扬电竞，会对游戏的普及产生推波助澜的作用，很多人会沉迷游戏，小一点来说会损害青少年身心健康，大方面来说，对于整个国家不利。游戏本身没错，但是游戏让很多青少年不能自拔。 沉迷游戏不能自拔的人 本身就对社会发展没有任何用处。 能明白我的意思吗？\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"[流言板]何时退役？哈斯勒姆：上帝同意的话，想把数字从19变成20 是的，我知道哈队对于热火的贡献，当年本来有机会去独行侠拿高薪，但选择留在热火。他的精神确实对球队有激励作用，但哈队其实从17赛季开始基本上捞不到出场时间了，等于说做了几年啦啦队队长了，韦德19年退的时候我以为哈队要退役了，没想到又硬撑了几年。现实就是现在他就是除了资历最老，在更衣室骂骂年轻人激励一下，其他真的没什么用了，占着一个名额。卡特42岁还能贡献自己的力量，但他发现自己已经打不了球的时候也选择退役了，现在哈队再硬留着也挺尴尬的，已经四五年没球打占着个名额了 没有合适的新人名额，我们老队长镇守更衣室何乐而不为，一个球队正常轮换才几个人，更衣室文化是球队重中之重，我们热密巴不得他留下。\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"管他长虫短虫，是虫就归鸭吃。 视频这类吃蛇，最后蛇是窒息死的吗？没牙吞进去应该还是活的 胃酸给弄死了\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"理性讨论哈登本赛能得30加吗  改成3次45＋还能讨论讨论\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"突围，不懂就问，石红杏不自杀最多能判多少年 第九章 渎职罪第三百九十七条：国家机关工作人员滥用职权或者玩忽职守，致使公共财产、国家和人民利益遭受重大损失的，处三年以下有期徒刑或者拘役；情节特别严重的，处三年以上七年以下有期徒刑。本法另有规定的，依照规定。 石红杏这种揭发有功，有自首和立功表现的，顶多两三年就出来了。\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"互联网果真赚钱，我弟刚毕业工资就吊打我了。。 有些人不懂政策，原来大厂大家都卷，我也得卷一下，现在要共同富裕了，慢慢的推导到全社会哪个个人和企业都不能过度的占用或消耗资源…… 不加班就共同富裕啦\",\"label\":\"其他有害\"}\n" +
                "{\"sentence1\":\"【600031三一重工】懂的大哥来聊一聊  这是周期股呀，一般8年多，我觉得不行就割了吧\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"美国湖人球迷开不开乌龟的会？纯好奇！ 你喷呗，跟我没关系，我只是为威少感到惋惜 有啥好惋惜的，巅峰已过，人总会老的，幸运的是见证过他的巅峰，他只是老了而已。\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"埃弗拉：遭遇苏亚雷斯种族歧视后，有次遇到他就想揍他 为什么踢比赛不铲他呢？ 你以为没铲过吗\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"【中文字幕】杰伦格林谈明天和状元一起在聚光灯下感受 赛后总结，肯定有一句\\\"手感不佳.....\\\"，不知道是一个还是俩都有！ 你可太懂了\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"第一个球队周边  似系女人嘅手\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"东子iphone13力度出来了，256g的6299，如果加个ac，就是6428 不如一号 1号256G没有便宜500吧\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"[流言板]威少：安东尼是名人堂级别球员，戴维斯面面俱到  运球运到八秒传给队友，队友没有时间再组织了，只能硬着头皮打。\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"[流言板]威少谈第四节技犯：一位裁判冲着我喊，我告诉他别喊 美金哦 100美金都行\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"库里未来在得分榜和助攻榜哪个位次会更靠前 你怎么不说对球权的把控，美其名曰自带体系。 差不多得了\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"更新了新版本  音效太大了\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"赛布尔目前能力是否被高估了？ 兰福德上二休五\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"速报，新贴纸背景刮不掉  15个贴纸出了个金尼公子\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"不吹不黑：你眼中的威斯布鲁克究竟是个怎样的球员？  威少这赛季菜，但是18年以前的威少真的很强，别有用心吧。\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"[流言板]30岁生日快乐！NBA官方晒图为斯内尔庆生  我到现在都记得2k用斯内儿投进了一个3分绝杀，这一晃六七年没玩了吧。\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"不想认真谈恋爱 不能因为一个海后就失去爱一个人的能力 每一个海王/海后都是有这段经历的，然后黑化\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"约老师和字母哥谁更厉害，哈登最有发言权 那最强的肯定是阿泰 哈登:你礼貌呢？\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"到底是弱的会被包夹还是强的被包夹？ 所以就出球能力，视野，对抗来讲，威少比杜兰特强？ 就是因为他不强，所以被包夹吧，而且看准了他就是一包夹就上头就喜欢单挑包夹的人，包夹这种人回报最大呀。有时候不一定说强弱实行包夹，参考因素比较多，利益最大化的设计战术才是好教练，至于强弱是让球迷口嗨的吧？\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"麦克米兰：佩顿儿时跟着爸爸在超音速主场玩耍 他现在站稳了脚跟 我看咪咕，屏幕上是两个T啊 要么你错了，要么咪咕错了，就这样\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"受欢迎男友职业榜，你男朋友上榜了没？ 公务员工资算高吗，只是稳定吧，还有医生，普通医生工资也不高啊 你要看生涯收入\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"羽绒服怎么洗啊 干洗 放屁\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"[流言板]马基夫-莫里斯因伤将缺席明日对阵湖人的比赛 所谓滤镜 挑出个毛病来\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"限定返场是啥规则啊，为啥没吕布那个天魔缭乱啊 抽奖会有的  比如凤求凰 抽奖有保底吗？\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"建议爵士粉在生活中不要效仿爵士（转自然哥微博）  说I told you完说we win and lose as a team，指责博塔斯没守好完说we win and lose as a team，把佩雷兹寄到维修区里面然后说就像那样我给了他太多的空间，赛后说你看佩雷兹都能跟着我了就知道他家车有多快了，回过头来说我超级尊敬佩雷兹……这招在职场用多了就是欠打…很不讨喜。\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"谁教教我怎么舔，不会舔了  中间和他谈了四年的男朋友好像找他复合了，他们今年三月份分手的。他前男友发了个抖音，不知道是不是她\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"和谈了7年的初恋女友分手了 天呀，失恋的我真的觉得自己根本不算失恋，你们这真的太难过了 想开了就好了，她也是走了自己喜欢的路，既然爱她也应该支持她\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"你们要看的团战来了，三獒斗五狼  楼主 别在上面瑟瑟发抖了 冲啊\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"【篮球财富NBA情报】活塞vs火箭，天赋对决，康宁汉姆PK格林！  火箭怕是还得输\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"才发现同学是魔鬼经济学作者的儿子 而且是我们学校的教授，多多少少可以交流一下 和你交流什么啊\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"真的有必要仅仅为了更好的影像系统买iPhone 13 Pro吗？ 不用五年，苹果手机用个三年你就有想扔的冲动了，真想多用几年千万不要升级系统和软件版本。 8p才四年\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"考研路上的狗血事件 说实话，如果你们都考上了，你在她心中还是有一定贡献的，如果她男朋友作死，你想挖角不是没可能 吾非曹孟德，不做苟且事\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"有没有跟我一样是在“库日 天”、“宇宙勇”之后才喜欢库里的人？ 我想告诉你，我现在都不喜欢。 能理解，现在新生代的球员莫兰特，东77，都没有什么想看的欲望，感觉nba没有以前的吸引力大了\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"中大奖后第二天你会继续上班吗  没想清楚怎么花之前，我是要上班的\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"近4轮联赛场场有助攻，穆勒已经成为五大联赛助攻次数最多的球员 科学家去穆勒化绝对是高层授意的。不过如同安胖、屎娘一样反惹一身骚。 穆勒这些年挑战真不少，tk 罗本 蒂亚戈 格策 j罗 酷鸟，包括没买来的丁丁在内一群人\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"近距离感受约基奇哥哥声线体型  高大威猛白衣服是大哥 搞军火的 一旁黄衣服是二哥18年职业ufc选手\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"[流言板]尝尝火锅！76人球员巴锡钉板大帽字母哥的反篮  真正的 西帝！阿西巴\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"我建议ag再一次轮换  不错\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"明年可以顶薪报价艾顿吗 那我觉得我们应该讨论的是手里有顶薪空间，应该签谁。 梭哈唐斯\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"【JRs专访】陈盈骏将接受虎扑专访，评论提出你的问题 英俊哥现在会不会来整两句东北话？？？ 可能现在台语粤语东北话普通话四语精通\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"新冠时期的爱情 有天半夜回家没克制住享受了一盘至尊vip服务，梅开二度之后第二天背消毒桶头晕眼花 什么是至尊VIP服务，详细说明一下\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"为什么渣男从来不缺女人？ 我还是不懂，没有外表连开始都比别人难，是怎么当起海王的 胆子大啊，而且喜欢到处凑，这个不行下一个，一般不挑\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"三甲医院收费员(养老混吃等死看面色工作) 基本上一眼到尽头的了，哈哈哈，做一辈子都是收费的 2000公积金一年下来也有好几万了，还不满足，来体验下我们全年无休月薪不足5000还没一金的日子\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"[流言板]德安德烈-亨特因手腕受伤将缺席今日的比赛  感觉老鹰这些新秀都挺憋屈的，战术都围绕特雷杨打\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"不能说毫不相干 只能说一模一样吧   这个女的我记得之前有个直播视频泄漏了吧\",\"label\":\"阅读\"}\n" +
                "{\"sentence1\":\"忆苦思甜：这场比赛谁还记得？  就伯克斯打出来了\",\"label\":\"阅读\"}"
                )
                ;

        return builder.toString();
    }
}