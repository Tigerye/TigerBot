package com.tigerobo.x.pai.biz.serving.execute;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tigerobo.pai.common.util.DownloadUtil;
import com.tigerobo.x.pai.api.biz.entity.API;
import com.tigerobo.x.pai.api.dto.ApiDto;
import com.tigerobo.x.pai.api.dto.model.ModelLabel;
import com.tigerobo.x.pai.api.enums.Style;
import com.tigerobo.x.pai.api.utils.MapUtils;
import com.tigerobo.x.pai.biz.utils.DownloadImageUtil;
import com.tigerobo.x.pai.biz.utils.IdGenerator;
import com.tigerobo.x.pai.biz.utils.RestUtil;
import com.tigerobo.x.pai.biz.utils.TimeUtil;
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

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Data
public class ImageLabelExecutor extends UriApiExecutor {

    Style style = Style.IMAGE_LABEL;

    public ImageLabelExecutor(ApiDto api) {
        super(api);
    }

    public Object execute(Map<String, Object> params) {

        if (CollectionUtils.isEmpty(params)) {
            return null;
        }

        String imageUrl = MapUtils.getString(params, "imageUrl", null);
        if (StringUtils.isEmpty(imageUrl)) {
            throw new IllegalArgumentException("图片参数为空");
        }
        String imagTmpFile = DownloadImageUtil.getTmpImgFile(imageUrl);

        String outPut = doExecute(imagTmpFile);

        if (StringUtils.isEmpty(outPut)) {
            throw new IllegalArgumentException("模型解析失败");
        }

        JSONObject execute = JSON.parseObject(outPut);
        List<List<ModelLabel>> allLabels = new ArrayList<>();

        Short status = execute.getShort("status");

        String msg = "";
        if (status != null) {
            if (status == 0) {
                JSONArray resultArray = execute.getJSONArray("result");
                if (resultArray != null) {
                    for (int i = 0; i < resultArray.size(); i++) {
                        JSONArray jsonArray = resultArray.getJSONArray(i);
                        List<ModelLabel> labels = JSONObject.parseArray(jsonArray.toJSONString(), ModelLabel.class);

                        for (ModelLabel label : labels) {
                            BigDecimal score = label.getScore();

                            if (score != null) {
                                score = score.setScale(4, RoundingMode.HALF_UP);
                                label.setScore(score);
                            }
                        }

                        allLabels.add(labels);
                    }
                }
            } else {
                msg = execute.getString("msg");
            }
        } else {
            msg = "服务异常";
        }
        JSONObject outMap = new JSONObject();

        if (!CollectionUtils.isEmpty(allLabels)) {
            outMap.put("status", 0);
            outMap.put("result", allLabels);
        } else {
            outMap.put("status", -1);
            outMap.put("msg", msg);
        }

        return outMap;
    }

    private String doExecute(String filePath) {
        String url = this.getApiUri();
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("multipart/form-data");
        // 设置请求的格式类型
        headers.setContentType(type);
        FileSystemResource fileSystemResource = new FileSystemResource(filePath);
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();

        form.add("images", fileSystemResource);
        HttpEntity<MultiValueMap<String, Object>> files = new HttpEntity<>(form, headers);
        RestTemplate restTemplate = RestUtil.getRestTemplate();
        return restTemplate.postForObject(url, files, String.class);
    }

}
