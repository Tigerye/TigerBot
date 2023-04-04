package com.tigerobo.x.pai.engine.manager.github;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tigerobo.x.pai.api.crawler.CrawlerGithubUser;
import com.tigerobo.x.pai.biz.biz.github.convert.GithubConvert;
import com.tigerobo.x.pai.biz.biz.github.GithubUserService;
import com.tigerobo.x.pai.biz.utils.RestUtil;
import com.tigerobo.x.pai.dal.biz.entity.github.GithubUserPo;
import com.tigerobo.x.pai.dal.biz.mapper.github.GithubMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CrawlerGithubUserManger {
    @Autowired
    private GithubMapper githubMapper;

    @Autowired
    private GithubUserService githubUserService;

    private String url="http://10.0.19.103:8002/export/v1/roll";
    private Integer size=100;
    public void dealCrawlerRepoData(){

        Integer maxThirdId = githubMapper.getUserMaxThirdId();
        if (maxThirdId == null){
            maxThirdId = 0;
        }
         CrawlerGithubUser userData = getUserData(maxThirdId);

        int i = 0;
        while (!CollectionUtils.isEmpty(userData.getList())){
            if (i>20){
                break;
            }
            i++;
//            log.info("twitter,i={}",i++);
            doProcess(userData);
            Integer maxId = userData.getMaxId();
            if (maxId==null){
                break;
            }
            userData =getUserData(maxId);
        }

    }

    private void doProcess(CrawlerGithubUser repoData) {
        final List<GithubUserPo> pos = GithubConvert.repo2po(repoData);
        if (CollectionUtils.isEmpty(pos)){
            return;
        }
        githubUserService.crawlerUser2db(pos);
    }

    private CrawlerGithubUser getUserData(Integer thirdId){
        Map<String, Object> params = new HashMap<>();
        params.put("type", "news.aigithub.user");
        params.put("app", "c01");
        params.put("sign", "e1fe4fc470b694994eba0f691c56a033");
        params.put("offset", thirdId);
        params.put("size", size);
        String response = RestUtil.get(url, params);
        if (StringUtils.isEmpty(response)) {
            return null;
        }
        JSONObject jsonObject = JSON.parseObject(response);
        return jsonObject.getObject("data", CrawlerGithubUser.class);
    }

}
