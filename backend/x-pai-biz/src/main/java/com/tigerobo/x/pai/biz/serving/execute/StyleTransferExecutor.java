package com.tigerobo.x.pai.biz.serving.execute;

import com.alibaba.fastjson.JSONObject;
import com.tigerobo.x.pai.api.ai.req.style.transfer.AiStyleTransferGenerateReq;
import com.tigerobo.x.pai.api.auth.service.UserService;
import com.tigerobo.x.pai.api.dto.ApiDto;
import com.tigerobo.x.pai.api.enums.Style;
import com.tigerobo.x.pai.api.serving.vo.ApiReqVo;
import com.tigerobo.x.pai.api.utils.MapUtils;
import com.tigerobo.x.pai.biz.ai.style.transfer.AiStyleTransferService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Data
public class StyleTransferExecutor extends UriApiExecutor {

    Style style = Style.ART_IMAGE;

    AiStyleTransferService aiStyleTransferService;

    UserService userService;

    public StyleTransferExecutor(ApiDto api, AiStyleTransferService aiStyleTransferService
            , UserService userService) {
        super(api);
        this.aiStyleTransferService = aiStyleTransferService;
        this.userService = userService;
    }
    public boolean supportCacheResult(){
        return false;
    }

    public Object execute(Map<String, Object> params) {
        return execute(params,null,null);
    }

    public Object execute(Map<String, Object> params, Long reqId, ApiReqVo reqVo) {

        if (CollectionUtils.isEmpty(params)) {
            return null;
        }

        String contentImage = MapUtils.getString(params, "contentImage", "");

        String styleImage = MapUtils.getString(params, "styleImage", "");
        Integer styleImageId = MapUtils.get(params, "styleImage",Integer.class, null);

        Integer userId = ThreadLocalHolder.getUserId();

        if (userId == null||userId == 0){
            userId = userService.getIdByUuidCache(reqVo.getAppId());
        }

        String apiUri = getApiUri();
        AiStyleTransferGenerateReq req = new AiStyleTransferGenerateReq();

        req.setContentImage(contentImage);
        req.setStyleImage(styleImage);

        req.setStyleImageId(styleImageId);

        Integer iterations = MapUtils.get(params, "iterations", Integer.class, null);

        req.setTotalProgress(iterations);

        req.setReqId(reqId);

        req.setUserId(userId);
        req.setApiKey(reqVo.getApiKey());

        reqId = aiStyleTransferService.userReqProduceImage(req, apiUri);


        Map<String,Object> result = new HashMap<>();
        result.put("reqId",reqId);

        JSONObject outMap = new JSONObject();
        outMap.put("status", 0);
        outMap.put("result", result);

        return outMap;
    }

}
