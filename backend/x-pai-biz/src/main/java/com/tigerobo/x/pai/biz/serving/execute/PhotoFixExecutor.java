package com.tigerobo.x.pai.biz.serving.execute;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tigerobo.x.pai.api.ai.req.photo.PhotoFixReq;
import com.tigerobo.x.pai.api.auth.service.UserService;
import com.tigerobo.x.pai.api.dto.ApiDto;
import com.tigerobo.x.pai.api.enums.Style;
import com.tigerobo.x.pai.api.serving.vo.ApiReqVo;
import com.tigerobo.x.pai.biz.ai.photo.PhotoFixAiService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Data
public class PhotoFixExecutor extends UriApiExecutor {

    Style style = Style.PHOTO_FIX;

    PhotoFixAiService photoFixAiService;

    UserService userService;

    public PhotoFixExecutor(ApiDto api, PhotoFixAiService photoFixAiService, UserService userService) {
        super(api);
        this.photoFixAiService = photoFixAiService;
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

        PhotoInput input = JSON.parseObject(JSON.toJSONString(params),PhotoInput.class);


        Integer userId = ThreadLocalHolder.getUserId();

        if (userId == null||userId == 0){
            userId = userService.getIdByUuidCache(reqVo.getAppId());
        }

        PhotoFixReq req = new PhotoFixReq();
        req.setInputPhotoUrl(input.getImage());
        req.setAppendColor(input.isAppendColor());
        req.setReqId(reqId);

        req.setUserId(userId);
        req.setApiKey(getApiKey());

        req.setInferUrl(getApiUri());
        photoFixAiService.userReq(req);
        Map<String,Object> result = new HashMap<>();
        result.put("reqId",reqId);

        JSONObject outMap = new JSONObject();
        outMap.put("status", 0);
        outMap.put("result", result);

        return outMap;
    }


    @Data
    private static class PhotoInput{
        private String image;
        private boolean appendColor;
    }

}
