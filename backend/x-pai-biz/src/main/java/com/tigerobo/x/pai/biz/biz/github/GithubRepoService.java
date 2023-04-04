package com.tigerobo.x.pai.biz.biz.github;

import com.tigerobo.x.pai.dal.biz.dao.github.GithubRepoDao;
import com.tigerobo.x.pai.dal.biz.dao.github.GithubRepoInfoDao;
import com.tigerobo.x.pai.dal.biz.entity.github.GithubRepoInfoPo;
import com.tigerobo.x.pai.dal.biz.entity.github.GithubRepoPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GithubRepoService {
    @Autowired
    private GithubRepoDao githubRepoDao;

    @Autowired
    private GithubRepoInfoDao githubRepoInfoDao;
    public void crawlerRepo2db(List<GithubRepoPo> pos){
        if (CollectionUtils.isEmpty(pos)){
            return;
        }

        List<Integer> thirdIds = pos.stream().map(po -> po.getThirdId()).collect(Collectors.toList());

        List<GithubRepoPo> existList = githubRepoDao.getByThirdIds(thirdIds);
        Map<Integer, GithubRepoPo> existMap = new HashMap<>();
        if (existList!=null){

            for (GithubRepoPo repoCrawlerPo : existList) {
                existMap.put(repoCrawlerPo.getThirdId(),repoCrawlerPo);
            }

        }
        for (GithubRepoPo po : pos) {

            log.info("github repo,thirdId:{}",po.getThirdId());
            GithubRepoPo exist = existMap.get(po.getThirdId());
            if (exist!=null){
//                po.setId(exist.getId());
//                int update = githubRepoDao.update(po);
//                if (update == 0){
//                    log.error("update fail:id:{}",po.getThirdId());
//                }
            }else {
                int add = githubRepoDao.add(po);
                if (add == 0){
                    log.error("add fail:id:{}",po.getThirdId());
                }
            }
            addOrUpdateRepoInfo(po);
        }
    }

    private void addOrUpdateRepoInfo(GithubRepoPo repoPo){

        if (repoPo==null||repoPo.getUid()==null){
            return;
        }
        final String uid = repoPo.getUid();
        final GithubRepoInfoPo infoDbPo = githubRepoInfoDao.loadByRepoId(uid);
        GithubRepoInfoPo infoPo = new GithubRepoInfoPo();

        BeanUtils.copyProperties(repoPo,infoPo);
        infoPo.setRepoUid(repoPo.getUid());
        if (infoDbPo==null){
            infoPo.setId(null);
            githubRepoInfoDao.add(infoPo);
        }else {

            final Integer updateDt = repoPo.getUpdateDt();

            final Integer updateDtDb = infoDbPo.getUpdateDt();

            if (updateDt.compareTo(updateDtDb)<=0){
                return;
            }

            infoPo.setId(infoDbPo.getId());
            infoPo.setCreateTime(null);
            final int update = githubRepoInfoDao.update(infoPo);
        }
    }

}
