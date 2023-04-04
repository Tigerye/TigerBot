package com.tigerobo.pai.biz.test.service.test.lake;

import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.biz.lake.LakeAbstractService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TextAbstractServiceTest extends BaseTest {

    @Autowired
    private LakeAbstractService lakeAbstractService;


    @Test
    public void test(){

        String content = "11月8日0—24时，31个省（自治区、直辖市）和新疆生产建设兵团报告新增确诊病例62例。其中境外输入病例19例（广东4例，广西4例，天津2例，上海2例，四川2例，云南2例，北京1例，福建1例，陕西1例），含4例由无症状感染者转为确诊病例（四川2例，广东1例，广西1例）；本土病例43例（河北12例，其中辛集市11例，石家庄市1例；黑龙江8例，其中黑河市7例、哈尔滨市1例；四川7例，均在成都市；辽宁5例，均在大连市；甘肃4例，其中兰州市3例、天水市1例；江西3例，均在上饶市；河南2例，均在郑州市；云南2例，均在德宏傣族景颇族自治州），含8例由无症状感染者转为确诊病例（甘肃3例，河南2例，四川2例，黑龙江1例）。无新增死亡病例。无新增疑似病例。";
        String anAbstract = lakeAbstractService.getAbstract(content);
        System.out.println(anAbstract);
    }


}
