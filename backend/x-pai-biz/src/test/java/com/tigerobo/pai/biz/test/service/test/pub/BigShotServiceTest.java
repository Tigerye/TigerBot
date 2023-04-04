package com.tigerobo.pai.biz.test.service.test.pub;

import com.alibaba.fastjson.JSON;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.biz.pub.BigShotQueryReq;
import com.tigerobo.x.pai.api.vo.user.follow.FollowVo;
import com.tigerobo.x.pai.biz.biz.pub.PubBigShotService;
import com.tigerobo.x.pai.biz.utils.RestUtil;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
@Slf4j
public class BigShotServiceTest extends BaseTest {

    @Autowired
    private PubBigShotService pubBigShotService;


    @Test
    public void initImgTest() throws Exception {

//        pubBigShotService.initImg();
    }


    @Test
    public void queryTest() {

        BigShotQueryReq req = new BigShotQueryReq();
//        req.setKeyword("sale");
        ThreadLocalHolder.setUserId(3);
        PageVo<FollowVo> bigShotPage = pubBigShotService.getBigShotPage(req);

        System.out.println(bigShotPage.getTotal());
        for (FollowVo pubBigShotVo : bigShotPage.getList()) {
            System.out.println(pubBigShotVo.getName() + "\t" + pubBigShotVo.isFollow());
        }
    }

    @Test
    public void getTest() {

        List<FollowVo> bigShotList = pubBigShotService.getTopBigShotList();

        for (FollowVo pubBigShotVo : bigShotList) {
            System.out.println(pubBigShotVo.getName());
        }
    }

    @Test
    public void traverseTest() {

        BigShotQueryReq req = new BigShotQueryReq();

        int pageNum = 1;
        int pageSize = 1000;
        req.setPageSize(1000);
        PageVo<FollowVo> bigShotPage = pubBigShotService.getBigShotPage(req);

        List<Integer> errIds = new ArrayList<>();
        int count = 0;
        int total = (int) bigShotPage.getTotal();
        List<Integer> ids = new ArrayList<>();
        while (bigShotPage != null && !CollectionUtils.isEmpty(bigShotPage.getList())) {

            System.out.println("page:"+pageNum);
            List<FollowVo> list = bigShotPage.getList();

            deal(list,errIds);
            if (pageNum*pageSize>=total){
                break;
            }
            pageNum++;
            req.setPageNum(pageNum);

            bigShotPage = pubBigShotService.getBigShotPage(req);
        }

        System.out.println(JSON.toJSONString(ids));
    }

    private void deal(List<FollowVo> vos,List<Integer> ids){

        vos.parallelStream().forEach(vo->{
            Integer id = vo.getId();
            String logoOss = vo.getLogoOss();
            try {
                String s = RestUtil.get(logoOss,null);
                if (StringUtils.isEmpty(s)){
                    log.error("id:{},url:{}",id,logoOss);
                    ids.add(id);
                }
            }catch (Exception ex){
                ids.add(id);
                log.error("id:{},url:{},e:{}",id,logoOss,ex.getMessage());
            }
        });
    }
}
