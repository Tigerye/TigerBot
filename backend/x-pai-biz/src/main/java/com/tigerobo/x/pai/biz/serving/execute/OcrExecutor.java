package com.tigerobo.x.pai.biz.serving.execute;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tigerobo.x.pai.api.dto.ApiDto;
import com.tigerobo.x.pai.api.enums.Style;
import com.tigerobo.x.pai.api.utils.MapUtils;
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
public class OcrExecutor extends UriApiExecutor {

    Style style = Style.OCR;

    public OcrExecutor(ApiDto api) {
        super(api);
    }

    public Object execute(Map<String, Object> params) {

        if (CollectionUtils.isEmpty(params)) {
            return null;
        }

        String imageUrl = MapUtils.getString(params, "image", null);
        if (StringUtils.isEmpty(imageUrl)) {
            throw new IllegalArgumentException("图片参数为空");
        }

        Integer width = MapUtils.get(params, "width",Integer.class, null);
        Integer height = MapUtils.get(params, "height",Integer.class, null);
        String imagTmpFile = DownloadImageUtil.getTmpImgFile(imageUrl);

        String outPut = doExecute(imagTmpFile);

        if (StringUtils.isEmpty(outPut)) {
            throw new IllegalArgumentException("模型处理失败");
        }

        JSONObject execute = JSON.parseObject(outPut);

        Short status = execute.getShort("status");
        if (status != null) {
            if (status == 0) {
                JSONArray resultArray = execute.getJSONArray("result");
                if (resultArray != null) {
                    for (int i = 0; i < resultArray.size(); i++) {
                        JSONObject innerJson = resultArray.getJSONObject(i);
                        BigDecimal score = innerJson.getBigDecimal("confidence");
                        if (score != null) {
                            score = score.setScale(4, RoundingMode.HALF_UP);
                            innerJson.put("confidence",score);
                        }
                    }
                }
            }

            if (width!=null||height!=null){
                JSONObject appendInfo = new JSONObject();
                if (width!=null){
                    appendInfo.put("width",width);
                }
                if(height!=null){
                    appendInfo.put("height",height);
                }
                execute.put("appendInfo",appendInfo);
            }


        }


        return execute;
    }

    private String doExecute(String filePath) {
        String url = this.getApiUri();
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("multipart/form-data");
        // 设置请求的格式类型
        headers.setContentType(type);
        FileSystemResource fileSystemResource = new FileSystemResource(filePath);
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();

        form.add("image", fileSystemResource);
        HttpEntity<MultiValueMap<String, Object>> files = new HttpEntity<>(form, headers);
        RestTemplate restTemplate = RestUtil.getRestTemplate();
        return restTemplate.postForObject(url, files, String.class);
    }

}
