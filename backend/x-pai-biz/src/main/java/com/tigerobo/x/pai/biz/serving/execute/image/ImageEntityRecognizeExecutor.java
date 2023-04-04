package com.tigerobo.x.pai.biz.serving.execute.image;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tigerobo.x.pai.api.dto.ApiDto;
import com.tigerobo.x.pai.api.enums.Style;
import com.tigerobo.x.pai.api.utils.MapUtils;
import com.tigerobo.x.pai.biz.serving.execute.UriApiExecutor;
import com.tigerobo.x.pai.biz.utils.DownloadImageUtil;
import com.tigerobo.x.pai.biz.utils.RestUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Slf4j
@Data
public class ImageEntityRecognizeExecutor extends UriApiExecutor {

    Style style = Style.IMAGE_ENTITY_RECOGNIZE;

    public ImageEntityRecognizeExecutor(ApiDto api) {
        super(api);
    }

    public Object execute(Map<String, Object> params) {

        if (CollectionUtils.isEmpty(params)) {
            return null;
        }

        String imageUrl = MapUtils.getString(params, "imageUrl", null);
        if (StringUtils.isEmpty(imageUrl)) {
            imageUrl = MapUtils.getString(params, "image", null);
            if (StringUtils.isEmpty(imageUrl)){
                throw new IllegalArgumentException("图片参数为空");
            }

        }
//
//        Integer width = MapUtils.get(params, "width",Integer.class, null);
//        Integer height = MapUtils.get(params, "height",Integer.class, null);
        String imagTmpFile = DownloadImageUtil.getTmpImgFile(imageUrl);

        int retry = 0;
        String outPut = null;
        while (retry<3){
            try {
                outPut = doExecute(imagTmpFile);
                break;
            }catch (Exception ex){
                retry++;
                log.error("imgUrl:{},retry:{}",imageUrl,retry,ex);
            }
        }


        if (StringUtils.isEmpty(outPut)) {
            throw new IllegalArgumentException("模型处理失败");
        }

        final JSONArray objects = JSON.parseArray(outPut);

        if (objects!=null&&objects.size()>0){
            for (int i = 0; i < objects.size(); i++) {
                JSONObject innerJson = objects.getJSONObject(i);
                BigDecimal score = innerJson.getBigDecimal("score");
                if (score != null) {
                    score = score.setScale(4, RoundingMode.HALF_UP);
                    innerJson.put("score",score);
                }
                final JSONObject bbox = innerJson.getJSONObject("bbox");

                if (bbox!=null&&bbox.size()>0){

                    for (Map.Entry<String, Object> entry : bbox.entrySet()) {
                        final String key = entry.getKey();
                        BigDecimal pos = new BigDecimal(entry.getValue().toString());
                        pos = pos.setScale(4, RoundingMode.HALF_UP);
                        bbox.put(key,pos);

                    }

                }
            }
        }
        JSONObject result = new JSONObject();

        result.put("result",objects);


        return result;
    }

    private String doExecute(String filePath) {
        String url = this.getApiUri();
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("multipart/form-data");
        // 设置请求的格式类型
        headers.setContentType(type);
        FileSystemResource fileSystemResource = new FileSystemResource(filePath);
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();

        form.add("data", fileSystemResource);
        HttpEntity<MultiValueMap<String, Object>> files = new HttpEntity<>(form, headers);
        RestTemplate restTemplate = RestUtil.getRestTemplate();
        return restTemplate.postForObject(url, files, String.class);
    }

}
