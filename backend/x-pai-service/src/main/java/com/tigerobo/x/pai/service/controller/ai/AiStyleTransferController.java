package com.tigerobo.x.pai.service.controller.ai;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tigerobo.x.pai.api.ai.req.AiArtDetailReq;
import com.tigerobo.x.pai.api.ai.req.ArtImageChooseMainReq;
import com.tigerobo.x.pai.api.ai.req.ArtImageOnlineReq;
import com.tigerobo.x.pai.api.ai.req.ArtImagePublicPageReq;
import com.tigerobo.x.pai.api.ai.vo.AiStyleTransferVo;
import com.tigerobo.x.pai.api.auth.aspect.Authorize;
import com.tigerobo.x.pai.api.dto.ai.AiStyleTransferDicDto;
import com.tigerobo.x.pai.api.vo.IdReqVo;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.ResultVO;
import com.tigerobo.x.pai.biz.ai.style.transfer.AiStyleTransferOperateService;
import com.tigerobo.x.pai.biz.ai.style.transfer.StyleTransferService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Api(value = "ai风格迁移", description = "ai风格迁移")
@Slf4j
@RestController
@RequestMapping(value = "/ai/styleTransfer/")
public class AiStyleTransferController {

    @Autowired
    private StyleTransferService styleTransferService;


    @Autowired
    private AiStyleTransferOperateService aiStyleTransferOperateService;


    @Authorize
    @ApiOperation(value = "我生成的图")
    @RequestMapping(value = "/getMyImages", method = POST)
    public PageVo<AiStyleTransferVo> getMyImages(HttpServletRequest request, @Valid @RequestBody ArtImagePublicPageReq req) {
        return styleTransferService.getMyList(req);
    }

    @ApiOperation(value = "公开的图列表")
    @RequestMapping(value = "/getPublishList", method = POST)
    public PageVo<AiStyleTransferVo> getPublishList(HttpServletRequest request, @Valid @RequestBody ArtImagePublicPageReq req) {
        return styleTransferService.getPublishList(req);
    }

    @ApiOperation(value = "reqId查看详情")
    @RequestMapping(value = "/getDetailByReqId", method = POST)
    public AiStyleTransferVo getDetailByReqId(HttpServletRequest request, @Valid @RequestBody AiArtDetailReq req) {

        String appKey = request.getParameter("appKey");
        String appId = request.getParameter("appId");

        return styleTransferService.getByReqId(appId,req.getReqId());
    }

    @ApiOperation(value = "id查看详情")
    @RequestMapping(value = "/getDetailById", method = POST)
    public AiStyleTransferVo getDetailByReqId(HttpServletRequest request, @Valid @RequestBody IdReqVo req) {
        return styleTransferService.getDetail(req);
    }

    @ApiOperation(value = "查看Style列表")
    @RequestMapping(value = "/getStyleSamples", method = POST)
    public List<AiStyleTransferDicDto> getStyleSamples(HttpServletRequest request) {
        return styleTransferService.getBaseStyleList();
    }

    @Authorize
    @ApiOperation(value = "offline")
    @RequestMapping(value = "/offline", method = POST)
    public ResultVO offline(HttpServletRequest request, @Valid @RequestBody IdReqVo reqVo) {

        aiStyleTransferOperateService.offline(reqVo.getId(),null);

        return ResultVO.success();
    }


    @Authorize
    @ApiOperation(value = "failRetry")
    @RequestMapping(value = "/failRetry", method = POST)
    public ResultVO failRetry(HttpServletRequest request, @Valid @RequestBody IdReqVo reqVo) {

        aiStyleTransferOperateService.failRetry(reqVo.getId());

        return ResultVO.success();
    }

    @Authorize
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = POST)
    public ResultVO delete(HttpServletRequest request, @Valid @RequestBody IdReqVo reqVo) {

        aiStyleTransferOperateService.delete(reqVo.getId(),null);

        return ResultVO.success();
    }

    @Authorize
    @ApiOperation(value = "online")
    @RequestMapping(value = "/online", method = POST)
    public ResultVO online(HttpServletRequest request, @Valid @RequestBody ArtImageOnlineReq reqVo) {
        aiStyleTransferOperateService.online(reqVo,null);
        return ResultVO.success();
    }


    @Authorize
    @ApiOperation(value = "选择主图")
    @RequestMapping(value = "/chooseProcessAsMain", method = POST)
    public ResultVO chooseProcessAsMain(@Valid @RequestBody ArtImageChooseMainReq reqVo) {
        aiStyleTransferOperateService.chooseMain(reqVo);
        return ResultVO.success();
    }



    private JSONArray getList(){

        String text = "[{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/6UUDpMJELZ48LqeRABv8.jpg\",\"id\":2},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/8M91dfgcXMhpbiLjkWIX.jpg\",\"id\":3},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/8z1WDADviKuQFqx48MTh.jpg\",\"id\":4},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/Bitcoin Goddess - Beeple.jpg\",\"id\":5},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/C0ijouVOs3H8hD3wFxZQ.jpg\",\"id\":6},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/C6Hd3uE9HjFmUNJUjpf4.jpg\",\"id\":7},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/Emdt9Zc0yhVNGV8oaHS7.jpg\",\"id\":8},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/EvRzUNtkH1Qh1RDqoz8W.jpg\",\"id\":9},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/FA7TROMFQ6xLwbKFFxb9.jpg\",\"id\":10},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/FZOKFdHjYWL4oKUkQaOZ.jpg\",\"id\":11},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/GuokulRk6bCVZ9XekgaS.jpg\",\"id\":12},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/H3QpyJnBclPidG2Opquz.jpg\",\"id\":13},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/H45axtJUugvTdQNniufn.jpg\",\"id\":14},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/HZoWjUD4xUbS68VwxKfZ.jpg\",\"id\":15},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/J4GVtHU54IQyeTNDsOAT.jpg\",\"id\":16},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/JBlYta3TzInEnFSCKTYc.jpg\",\"id\":17},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/JoZt6DOoGVvvjrM8d4WF.jpg\",\"id\":18},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/Jxqdm5RFbs0ULVfj3vNm.jpg\",\"id\":19},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/MEgo7e1oYtTLmt5JOHeo.jpg\",\"id\":20},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/NBkI6TjfJJbx0jaVQ6S5.jpg\",\"id\":21},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/OMidk43gw2QS04jxfePU.jpg\",\"id\":22},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/PaoF0K2iy9uXD8Y3ipGE.jpg\",\"id\":23},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/Shipwreck.jpg\",\"id\":24},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/SsCmIuFKa6XFjowyDchR.jpg\",\"id\":25},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/SykF1mcn73B3u3Qrcg7Y.jpg\",\"id\":26},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/T1UtsLiTVhHAPLNCCkYx.jpg\",\"id\":27},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/VhyEEV3BLu8eeWxewOcu.jpg\",\"id\":28},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/WH7PyDDuQ3QdsR84rXZ7.jpg\",\"id\":29},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/YUHf3xhqehzNcPsDVTbL.jpg\",\"id\":30},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/YwsT1MSrLyLYoOvdbnGB.jpg\",\"id\":31},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/ZyhOMPOCDpdw8HlMYgtF.jpg\",\"id\":32},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/aQzZtREIzHcxmrBuWLyo.jpg\",\"id\":33},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/ah0esJJJUbaBAKUzTutj.jpg\",\"id\":34},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/dnskDrGYa7iuLRE9M8zk.jpg\",\"id\":35},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/dolomiten.jpg\",\"id\":36},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/duwlFySwghCw6shsp9JZ.jpg\",\"id\":37},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/e2LMOV2MslRldLPRKgML.jpg\",\"id\":38},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/gIzWDnMSH5dVL3NiJzsN.jpg\",\"id\":39},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/gojkrZGN3IhVjhaeRfu1.jpg\",\"id\":40},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/h43maxx0spSBU9k1Q2VR.jpg\",\"id\":41},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/i4YUzR8KQ8NtqNpPnNj2.jpg\",\"id\":42},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/jH0Nko52LslvV4I0fAtH.jpg\",\"id\":43},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/jcDYw8dCcjtoTuI03DPX.jpg\",\"id\":44},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/jlicrHq7Ui5wCFHdJZ4t.jpg\",\"id\":45},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/kandinsky.jpg\",\"id\":46},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/marliyn_1967_andy-warhol.jpeg\",\"id\":47},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/meywyyiBScaqpJqQHej9.jpg\",\"id\":48},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/mulberry tree.jpg\",\"id\":49},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/n2ephCUTghISlEoStf4Y.jpg\",\"id\":50},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/njJWZMYte8qFuwEckB9F.jpg\",\"id\":51},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/orchidae.jpg\",\"id\":52},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/oznKWrWcbQNVwmmLDjRm.jpg\",\"id\":53},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/rwUtXziVyEdbFETdYSCT.jpg\",\"id\":54},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/the great wave.jpg\",\"id\":55},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/the night cafe.jpg\",\"id\":56},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/ttNZ4ptMnoc1RgpqQVHb.jpg\",\"id\":57},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/vnqO5hDWWrKfqYYwowWh.jpg\",\"id\":58},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/w8ODkhUND3Gz1HBrMySv.jpg\",\"id\":59},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/wwUee71xirvCw8p8fpOg.jpg\",\"id\":60},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/xRBL8q9MoEtUkQtlgSwZ.jpeg\",\"id\":61},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/ysJgE6rCIpgr9gx6zyiH.jpg\",\"id\":62}]";

        return JSON.parseArray(text);
    }

}
