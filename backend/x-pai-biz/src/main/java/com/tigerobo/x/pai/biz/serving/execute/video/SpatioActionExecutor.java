package com.tigerobo.x.pai.biz.serving.execute.video;

import com.alibaba.fastjson.JSONObject;
import com.tigerobo.x.pai.api.ai.req.AiArtImageGenerateReq;
import com.tigerobo.x.pai.api.ai.req.spatio.action.AiSpatioActionGenerateReq;
import com.tigerobo.x.pai.api.auth.service.UserService;
import com.tigerobo.x.pai.api.dto.ApiDto;
import com.tigerobo.x.pai.api.enums.Style;
import com.tigerobo.x.pai.api.serving.vo.ApiReqVo;
import com.tigerobo.x.pai.api.utils.MapUtils;
import com.tigerobo.x.pai.biz.ai.art.image.ArtImageAiService;
import com.tigerobo.x.pai.biz.ai.spatio.action.AiSpatioActionService;
import com.tigerobo.x.pai.biz.serving.execute.UriApiExecutor;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Data
public class SpatioActionExecutor extends UriApiExecutor {

    Style style = Style.SPATIO_ACTION;

    AiSpatioActionService aiSpatioActionService;

    UserService userService;

    public SpatioActionExecutor(ApiDto api, AiSpatioActionService aiSpatioActionService, UserService userService) {
        super(api);
        this.aiSpatioActionService = aiSpatioActionService;
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

        String url = MapUtils.getString(params, "url", "");


        Integer userId = ThreadLocalHolder.getUserId();

        if (userId == null||userId == 0){
            userId = userService.getIdByUuidCache(reqVo.getAppId());
        }

        String apiUri = getApiUri();
        AiSpatioActionGenerateReq req = new AiSpatioActionGenerateReq();
        req.setInputVideo(url);
        req.setReqId(reqId);

        req.setUserId(userId);
        req.setApiKey(reqVo.getApiKey());

        reqId = aiSpatioActionService.userReqProduceImage(req, apiUri);


        Map<String,Object> result = new HashMap<>();
        result.put("reqId",reqId);

        JSONObject outMap = new JSONObject();
        outMap.put("status", 0);
        outMap.put("result", result);

        return outMap;
    }

}
