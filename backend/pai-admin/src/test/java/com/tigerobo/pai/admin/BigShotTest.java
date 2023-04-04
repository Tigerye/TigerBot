package com.tigerobo.pai.admin;

import com.tigerobo.x.pai.api.admin.req.BigShotAdminReq;
import com.tigerobo.x.pai.api.dto.admin.BigShotDto;
import com.tigerobo.x.pai.api.vo.IdReqVo;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.biz.admin.BigShotAdminService;
import com.tigerobo.x.pai.biz.admin.PubSiteAdminService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class BigShotTest extends AdminBaseTest{
    @Autowired
    private BigShotAdminService bigShotAdminService;
    @Autowired
    private PubSiteAdminService pubSiteAdminService;

    @Test
    public void queryTest(){
        BigShotAdminReq req=new BigShotAdminReq();
        req.setIsDeleted(0);
        req.setKeyword("a");
        final PageVo<BigShotDto> bigShotVoPageVo = bigShotAdminService.bigShotQuery(req);
        final List<BigShotDto> list = bigShotVoPageVo.getList();
        list.stream().forEach(po->{
            System.out.println(po);
        });
    }

    @Test
    public void bigShotDelTest(){
        IdReqVo req=new IdReqVo();
        req.setId(1);
//        bigShotAdminService.online(req);
        //bigShotAdminService.bigShotDelete(1);
//        pubSiteAdminService.opeOnlineStatus(req);
//        pubSiteAdminService.online(req);
    }
}
