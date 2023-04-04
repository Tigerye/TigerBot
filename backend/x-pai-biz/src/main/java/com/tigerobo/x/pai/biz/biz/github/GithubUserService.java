package com.tigerobo.x.pai.biz.biz.github;

import com.tigerobo.x.pai.dal.biz.dao.github.GithubUserDao;
import com.tigerobo.x.pai.dal.biz.entity.github.GithubUserPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GithubUserService {
    @Autowired
    private GithubUserDao githubUserDao;
    public void crawlerUser2db(List<GithubUserPo> pos){
        if (CollectionUtils.isEmpty(pos)){
            return;
        }

        List<Integer> thirdIds = pos.stream().map(po -> po.getThirdId()).collect(Collectors.toList());

        List<GithubUserPo> existList = githubUserDao.getByThirdIds(thirdIds);
        Map<Integer, GithubUserPo> existMap = new HashMap<>();
        if (existList!=null){

            for (GithubUserPo userCrawlerPo : existList) {
                existMap.put(userCrawlerPo.getThirdId(),userCrawlerPo);
            }

        }
        for (GithubUserPo po : pos) {

            GithubUserPo exist = existMap.get(po.getThirdId());
            if (exist!=null){
                po.setId(exist.getId());
                int update = githubUserDao.update(po);
                if (update == 0){
                    log.error("update fail:id:{}",po.getThirdId());
                }
            }else {
                int add=githubUserDao.add(po);
                if (add == 0){
                    log.error("add fail:id:{}",po.getThirdId());
                }
            }
        }
    }


}
