package com.tigerobo.pai.biz.test.common.test;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.xm.Similarity;
import org.xm.similarity.text.CosineSimilarity;
import org.xm.similarity.text.TextSimilarity;
import org.xm.tendency.word.HownetWordTendency;

import java.util.*;

public class SimHashTest {


    @Test
    public void initMap() {

        Map<String, String> qaMap = getQaMap();

        System.out.println(JSON.toJSONString(qaMap));
    }

    private Map<String, String> getQaMap() {
        Map<String, String> qaMap = new LinkedHashMap<>();


        StringBuilder a1 = new StringBuilder();
        a1.append("甘肃省中小企业特色产业集群（以下简称集群）认定须满足以下八方面指标。同时，集群企业近三年未发生较大及以上安全、质量和环境污染等事故，重大及以上网络安全事件和数据安全事件，以及偷税漏税、违法违规、严重失信和其它重大问题的行为。" +
                "一、具有较强核心竞争力" +
                "二、优质中小企业梯度培育成效显著" +
                "三、产业链供应链协作高效" +
                "四、具有较强协同创新能力" +
                "五、数字化转型效果明显" +
                "六、具有较高绿色化发展水平" +
                "七、积极参与产业开放合作" +
                "八、具有较强治理和服务能力");
        qaMap.put("甘肃省中小企业特色产业集群认定标准是什么？", a1.toString());

        StringBuilder a2 = new StringBuilder();
        a2.append("第十二条 甘肃省中小企业特色产业集群认定坚持申报自愿、公开透明、以评促建、持续提升、跟踪监测、动态调整的原则，省工业和信息化厅与市（州）、县（市、区）工信部门分工负责，统筹开展，有序推进。" +
                "第十三条  申报认定的集群应在县级区划范围内，由所在地县（市、区）工信部门作为申报主体。" +
                "第十四条  市（州）工信部门负责对集群申报进行受理、初审和实地抽查，在符合认定标准（见附件）的基础上，择优推荐至省工业和信息化厅。" +
                "第十五条  省工业和信息化厅组织对市（州）工信部门推荐的集群申报材料进行复审（包括实地抽查），择优形成集群名单，并在省工业和信息化厅门户网站公示５个工作日，经公示无异议的，确定为“甘肃省中小企业特色产业集群”。");
        qaMap.put("它的认定程序是什么？", a2.toString());
        StringBuilder a3 = new StringBuilder();
        a3.append("本细则所称甘肃省中小企业特色产业集群（以下简称集群）是指主要定位在县级区划范围内，以新发展理念为引领，以中小企业为主体，主导产业聚焦、优势特色突出、资源要素汇聚、协作网络高效、治理服务完善，拥有产业关联的服务、管理和科研等支撑机构，具有区域优势和较强核心竞争力的中小企业产业集群。");

        qaMap.put("它的范围是什么？", a3.toString());

        StringBuilder a4 = new StringBuilder();

        a4.append("（一）提升集群主导产业优势。（二）加强集群优质企业培育。（三）激发集群创业创新活力。（四）推进集群数字化升级。（五）加快集群绿色低碳转型。（六）深化集群开放合作。（七）提高集群治理和服务能力。");

        qaMap.put("它的培育要求有哪些？", a4.toString());
        String a5 = "集群有效期为三年。有效期满后，由省工业和信息化厅组织开展复核工作，并考核集群三年发展规划目标完成情况，复核通过的有效期延长三年。集群认定后每年进行考核，对考核优秀的集群，参照国家集群支持方式，视当年省级专项资金情况予以奖励支持。";

        qaMap.put("它的有效期是多久？",a5);

        qaMap.put("甘肃省中小企业特色产业集群的认定程序是什么？", a2.toString());
        qaMap.put("甘肃省中小企业特色产业集群的范围是什么？", a3.toString());
        qaMap.put("甘肃省中小企业特色产业集群的培育要求有哪些？", a4.toString());
        qaMap.put("甘肃省中小企业特色产业集群的有效期是多久？",a5);

        return qaMap;
    }

    private Map<String, String> getQaMapV1() {
        Map<String, String> qaMap = new LinkedHashMap<>();

        qaMap.put("甘肃省中小企业特色产业集群认定标准？", "甘肃省中小企业特色产业集群（以下简称集群）认定须满足以下八方面指标。同时，集群企业近三年未发生较大及以上安全、质量和环境污染等事故，重大及以上网络安全事件和数据安全事件，以及偷税漏税、违法违规、严重失信和其它重大问题的行为。\n" +
                "一、具有较强核心竞争力\n" +
                "二、优质中小企业梯度培育成效显著\n" +
                "三、产业链供应链协作高效\n" +
                "四、具有较强协同创新能力\n" +
                "五、数字化转型效果明显\n" +
                "六、具有较高绿色化发展水平\n" +
                "七、积极参与产业开放合作\n" +
                "八、具有较强治理和服务能力");

        qaMap.put("甘肃省小型微型企业创业创新示范基地认定有限期是几年？", "示范基地每次认定有效期三年,期满后可自愿重新申报。");

        qaMap.put("甘肃省小型微型企业创业创新示范基地的认定程序如何？", "（一）现场核查。市州工信局、兰州新区经发局对申报示范基地进行实地核查,提出核查意见。\n" +
                "（二）专家评审。省工信厅组织专家进行评审,对申报基地的基本条件、入驻企业情况、服务能力、服务业绩、社会效益、基地发展目标等情况进行审核,提出示范基地候选名单。\n" +
                "（三）公示认定。根据现场核查意见和专家评审建议,确定省级示范基地名单,并在省工信厅网站公示5个工作日。公示期间,对公示平台有异议的个人或单位,可以实名反馈至省工信厅,并提供佐证材料和联系方式;未实名反馈或未提供佐证材料、联系方式的,不予受理。");
        qaMap.put("4、甘肃省中小企业公共服务示范平台认定的对象范围是什么？", "具有法人资格、运营３年以上，资产总额不低于200万元，服务中小企业数量占服务客户数量的70％以上，为中小企业提供政策、咨询、培训、技术等公共服务的非营利法人和企业法人，其中企业法人承担政府委托或政府购买的服务收入比例三年平均在20％以上。须获得市州级中小企业公共服务示范平台认定。");


        qaMap.put("省级中小企业公共服务示范平台和省级小型微型企业创业创新示范基地的申报要求是什么？", "各市州工信局、兰州｜新区经发局要按照《示范平台管理办法》要求，组织做好中小企业公共服务示范平台申报工作，严格对照《示范平台管理办法》第十一条提交申报材料。兰州市推荐申报示范平台不超过5家，其他各市州及兰州新区推荐申报示范平台不超过3家。没有符合申报条件示范平台的市州，要做好培育工作，引导示范平台能力提升。");
        return qaMap;
    }

    @Test
    public void simTest() {

        String query = "省级中小企业公共服务示范平台和省级小型微型企业创业创新示范基地申报要求是什么";
        query = "2、3、甘肃省小型微型企业创业创新示范基地的认定程序如何？";
        query = "5、省级中小企业公共服务示范平台和省级小型微型企业创业创新示范基地的申报要求是什么？？";
        query= "2、甘肃省中小企业特色产业集群认定标准是什么？";
        query = "5、集群认定程序是什么？";

        final Map<String, String> qaMap = getQaMap();

        LinkedHashMap<String, Double> keyScore = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : qaMap.entrySet()) {

            TextSimilarity cosSimilarity = new CosineSimilarity();

            double score = cosSimilarity.getSimilarity(entry.getKey(), query);
            keyScore.put(entry.getKey(), score);
        }


        final Map.Entry<String, Double> entry = keyScore.entrySet().stream().filter(e -> e.getValue() != null && e.getValue() > 0.55d).max(Map.Entry.<String, Double>comparingByValue()).orElse(null);


        if (entry != null) {
            System.out.println(entry.getKey() + "\t" + entry.getValue());
            System.out.println(qaMap.get(entry.getKey()));
        } else {
            System.out.println("没够");
        }
//        samleTest();
    }

    private void samleTest() {
        double result = Similarity.cilinSimilarity("电动车", "自行车");
        System.out.println(result);

        String word = "混蛋";
        HownetWordTendency hownetWordTendency = new HownetWordTendency();
        result = hownetWordTendency.getTendency(word);
        System.out.println(word + "  词语情感趋势值：" + result);
    }

    private List<String> getSamples() {


        List<String> list = new ArrayList<>();
        list.add("甘肃省中小企业特色产业集群认定标准？");
        list.add("甘肃省小型微型企业创业创新示范基地认定有限期是几年？");
        list.add("甘肃省小型微型企业创业创新示范基地的认定程序如何？");
        list.add("甘肃省中小企业公共服务示范平台认定的对象范围是什么？");
        list.add("省级中小企业公共服务示范平台和省级小型微型企业创业创新示范基地的申报要求是什么？");
        return list;
    }
}
