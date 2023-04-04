package com.tigerobo.x.pai.engine.manager.github;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tigerobo.x.pai.api.crawler.CrawlerGithubRepo;
import com.tigerobo.x.pai.biz.biz.github.convert.GithubConvert;
import com.tigerobo.x.pai.biz.biz.github.GithubRepoService;
import com.tigerobo.x.pai.biz.utils.RestUtil;
import com.tigerobo.x.pai.dal.biz.entity.github.GithubRepoPo;
import com.tigerobo.x.pai.dal.biz.mapper.github.GithubMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Slf4j
@Component
public class CrawlerGithubRepoManger {
    @Autowired
    private GithubMapper githubMapper;

    @Autowired
    private GithubRepoService githubRepoService;

    private String url="http://10.0.19.103:8002/export/v1/roll";
    private Integer size=100;
    public void dealCrawlerRepoData(){

        Integer maxThirdId = githubMapper.getRepoMaxThirdId();
        if (maxThirdId == null){
            maxThirdId = 0;
        }
         CrawlerGithubRepo repoData = getRepoData(maxThirdId);

        int i = 0;
        while (!CollectionUtils.isEmpty(repoData.getList())){
            if (i>20){
                break;
            }
            i++;

            doProcess(repoData);
            Integer maxId = repoData.getMaxId();
            if (maxId==null){
                break;
            }

            repoData = getRepoData(maxId);
        }

    }

    private void doProcess(CrawlerGithubRepo repoData) {
        final List<GithubRepoPo> pos = GithubConvert.repo2po(repoData);
        if (CollectionUtils.isEmpty(pos)){
            return;
        }
        githubRepoService.crawlerRepo2db(pos);
    }

    private CrawlerGithubRepo getRepoData(Integer thirdId){
        Map<String, Object> params = new HashMap<>();
        params.put("type", "news.aigithub.repo");
        params.put("app", "c01");
        params.put("sign", "e1fe4fc470b694994eba0f691c56a033");
        params.put("offset", thirdId);
        params.put("size", size);
        String response = RestUtil.get(url, params);
        if (StringUtils.isEmpty(response)) {
            return null;
        }
        JSONObject jsonObject = JSON.parseObject(response);
        return jsonObject.getObject("data", CrawlerGithubRepo.class);
    }

}
