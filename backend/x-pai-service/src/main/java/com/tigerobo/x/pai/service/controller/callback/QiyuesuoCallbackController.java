package com.tigerobo.x.pai.service.controller.callback;

import com.tigerobo.x.pai.api.vo.ResultVO;
import com.tigerobo.x.pai.biz.auth.OrgInfoService;
import com.tigerobo.x.pai.biz.biz.service.DemandContractService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/qys/callback")
@Api(value = "契约锁回调", position = 1100, tags = "企业信息")
public class QiyuesuoCallbackController {

    @Autowired
    private DemandContractService demandContractService;

    @Autowired
    private OrgInfoService orgInfoService;

    @RequestMapping(path = "/contract", method = RequestMethod.POST)
    @ResponseBody
    public ResultVO contractCallback(HttpServletRequest request, String signature, Long timestamp,String content)  {
        ResultVO resultVO = new ResultVO<>();
        try {
            log.info("contract-Callback success. signature: {}, timestamp: {}", signature, timestamp);

            log.info("qiyuesuo-contract-Callback success. content: {}", content);
            //....业务逻辑编写
            //返回json格式的结果数据

            try{
                demandContractService.dealContractCallBack(content);
            }catch (Exception ex){
                log.error("qiyuesuo-call-back-error,content{}",content,ex);
            }
            resultVO.setCode(0);
            return resultVO;
        }catch (Exception e) {
            log.error("",e);
            resultVO.setCode(1001);
        }
        return resultVO;
    }

    @RequestMapping(path = "/company/{id}", method = RequestMethod.POST)
    @ResponseBody
    public ResultVO companyCallback(@PathVariable Integer id,int status, String requestId, String authInfo)  {
        ResultVO resultVO = new ResultVO<>();
        try {
            log.info("qiyuesuo-company-Callback success. id:{},status: {}, requestId: {}, authInfo: {}", id,status, requestId, authInfo);
            //....业务逻辑编写
            //返回json格式的结果数据

            orgInfoService.callback(id,status,authInfo);
            resultVO.setCode(0);
            return resultVO;
        }catch (Exception e) {
            log.error("{},{},{}",status,resultVO,authInfo,e);
            resultVO.setCode(0);
        }
        return resultVO;
    }


}
