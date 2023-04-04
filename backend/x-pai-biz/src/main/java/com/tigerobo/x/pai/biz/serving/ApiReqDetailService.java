package com.tigerobo.x.pai.biz.serving;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Preconditions;
import com.tigerobo.x.pai.api.ai.vo.AiArtImageVo;
import com.tigerobo.x.pai.api.serving.vo.ApiDetailVo;
import com.tigerobo.x.pai.biz.ai.art.image.ArtImageService;
import com.tigerobo.x.pai.biz.auth.UserServiceImpl;
import com.tigerobo.x.pai.biz.utils.AccessUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
public class ApiReqDetailService {

    @Autowired
    private ArtImageService artImageService;

    @Autowired
    private ApiKeyService apiKeyService;
    @Autowired
    private UserServiceImpl userService;

    public ApiDetailVo getByReqId(String appId, String apiKey, String accessToken, Long reqId){
        Integer userId = userService.getIdByUuidCache(appId);
        Preconditions.checkArgument(userId != null && userId > 0, "用户不存在");
        boolean effectToken = AccessUtil.isEffectMdToken(appId, accessToken);
        final String artImageApiKey = apiKeyService.getArtImageApiKey();
        ApiDetailVo detailVo = new ApiDetailVo();
        if (Objects.equals(artImageApiKey,apiKey)){

            final AiArtImageVo vo = artImageService.getByReqId(reqId);
            if (vo == null){
                return null;
            }
            if (userId.equals(vo.getUserId())){
                detailVo.setDetail(vo);
            }
            return detailVo;
        }
        return detailVo;
    }
}
