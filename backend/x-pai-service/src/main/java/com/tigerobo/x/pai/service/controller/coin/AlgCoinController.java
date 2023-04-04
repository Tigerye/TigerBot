package com.tigerobo.x.pai.service.controller.coin;

import com.algolet.common.bean.vo.PageVo;
import com.algolet.pay.api.vo.AlgCoinRecordReq;
import com.algolet.pay.api.vo.AlgCoinRecordVo;
import com.algolet.pay.biz.service.AlgCoinRecordService;
import com.algolet.pay.biz.service.AlgCoinService;
import com.tigerobo.x.pai.api.auth.aspect.Authorize;
import com.tigerobo.x.pai.api.vo.ResultVO;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "alg/coin")
@Api(value = "alg-coin",  tags = "算法币")
public class AlgCoinController {

    @Autowired
    private AlgCoinService algCoinService;
    @Autowired
    private AlgCoinRecordService algCoinRecordService;

    @ApiOperation(value = "用户算法币-信息")
    @PostMapping(path = "getUserAlgCoinInfo", produces = "application/json")
    public Map<String,Object> signIn() {
        final Integer userId = ThreadLocalHolder.getUserId();
        final int userTotalCoin = algCoinService.getUserTotalCoin(userId);
        Map<String,Object> data = new HashMap<>();
        data.put("totalNum",userTotalCoin);
        return data;
    }

    @ApiOperation(value = "用户算法币-明细记录列表")
    @PostMapping(path = "getUserCoinRecordPage", produces = "application/json")
    public PageVo<AlgCoinRecordVo> getUserCoinRecordPage(@RequestBody AlgCoinRecordReq req) {
        final Integer userId = ThreadLocalHolder.getUserId();
        req.setUserId(userId);
        return algCoinRecordService.getUserCoinRecordPage(req);
    }


}
