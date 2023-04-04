//package com.tigerobo.x.pai.biz.serving.execute.image;
//
//import com.alibaba.fastjson.JSONObject;
//import com.tigerobo.x.pai.api.ai.req.AiArtImageGenerateReq;
//import com.tigerobo.x.pai.api.auth.service.UserService;
//import com.tigerobo.x.pai.api.dto.ApiDto;
//import com.tigerobo.x.pai.api.enums.Style;
//import com.tigerobo.x.pai.api.serving.vo.ApiReqVo;
//import com.tigerobo.x.pai.api.utils.MapUtils;
//import com.tigerobo.x.pai.biz.ai.art.image.ArtImageAiService;
//import com.tigerobo.x.pai.biz.serving.execute.UriApiExecutor;
//import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
//import lombok.Data;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.util.CollectionUtils;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Slf4j
//@Data
//public class ArtImageExecutorBak extends UriApiExecutor {
//
//    Style style = Style.ART_IMAGE;
//
//    ArtImageAiService artImageAiService;
//
//    UserService userService;
//
//    public ArtImageExecutorBak(ApiDto api, ArtImageAiService aiArtImageService, UserService userService) {
//        super(api);
//        this.artImageAiService = aiArtImageService;
//        this.userService = userService;
//    }
//    public boolean supportCacheResult(){
//        return false;
//    }
//
//    public Object execute(Map<String, Object> params) {
//        return execute(params,null,null);
//    }
//
//    public Object execute(Map<String, Object> params, Long reqId, ApiReqVo reqVo) {
//
//        if (CollectionUtils.isEmpty(params)) {
//            return null;
//        }
//
//        String text = MapUtils.getString(params, "text", "");
//
//        List<String> modifiers = MapUtils.getList(params, "modifiers", String.class);
//
//        String image = MapUtils.getString(params, "image", "");
//
//        Integer userId = ThreadLocalHolder.getUserId();
//
//        if (userId == null||userId == 0){
//            userId = userService.getIdByUuidCache(reqVo.getAppId());
//        }
//
//        String apiUri = getApiUri();
//        AiArtImageGenerateReq req = new AiArtImageGenerateReq();
//        req.setText(text);
//        req.setImageUrl(image);
//
//        req.setModifiers(modifiers);
//
//        Integer iterations = MapUtils.get(params, "iterations", Integer.class, null);
//
//        req.setTotalProgress(iterations);
//
//        req.setReqId(reqId);
//
//        req.setUserId(userId);
//        req.setApiKey(reqVo.getApiKey());
//
//        reqId = artImageAiService.userReqProduceImage(req, apiUri);
//
//
//        Map<String,Object> result = new HashMap<>();
//        result.put("reqId",reqId);
//
//        JSONObject outMap = new JSONObject();
//        outMap.put("status", 0);
//        outMap.put("result", result);
//
//        return outMap;
//    }
//
//}
