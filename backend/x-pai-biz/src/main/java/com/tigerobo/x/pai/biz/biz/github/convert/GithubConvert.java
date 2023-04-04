package com.tigerobo.x.pai.biz.biz.github.convert;

import com.tigerobo.x.pai.api.crawler.CrawlerGithubRepo;
import com.tigerobo.x.pai.api.crawler.CrawlerGithubUser;
import com.tigerobo.x.pai.dal.biz.entity.github.GithubRepoPo;
import com.tigerobo.x.pai.dal.biz.entity.github.GithubUserPo;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

public class GithubConvert {
    public static List<GithubRepoPo> repo2po(CrawlerGithubRepo crawler){
        if (crawler == null){
            return null;
        }
        List<CrawlerGithubRepo.CrawlerGithubRepoItem> list = crawler.getList();
        if (CollectionUtils.isEmpty(list)){
            return null;
        }
        return list.stream().map(c->crawler2po(c)).collect(Collectors.toList());
    }
    public static List<GithubUserPo> repo2po(CrawlerGithubUser crawler){
        if (crawler == null){
            return null;
        }
        List<CrawlerGithubUser.CrawlerGithubUserItem> list = crawler.getList();
        if (CollectionUtils.isEmpty(list)){
            return null;
        }
        return list.stream().map(c->crawler2po(c)).collect(Collectors.toList());
    }

    private static GithubRepoPo crawler2po(CrawlerGithubRepo.CrawlerGithubRepoItem item) {
        GithubRepoPo po=new GithubRepoPo();
        po.setThirdId(item.getId());
        po.setUid(item.getUid());
        po.setUserId(item.getUser_id());
        po.setUpdateDt(item.getUpdate_dt());
        po.setReposName(item.getRepos_name());
        po.setUrl(item.getUrl());
        po.setDescription(item.getDescription());
        po.setLanguage(item.getLanguage());
        po.setTags(item.getTags());
        po.setLicense(item.getLicense());
        po.setForksCount(item.getForks_count());
        po.setStarCount(item.getStar_count());
        po.setOpenIssues(item.getOpen_issues());
        po.setNeedHelpIssues(item.getNeed_help_issues());
        po.setOpenPulls(item.getOpen_pulls());
        po.setReposUpdate(item.getRepos_update_at());
        po.setSrcId(item.getSrc_id());
        po.setCreateTime(item.getCreate_tim());
        return po;
    }
    private static GithubUserPo crawler2po(CrawlerGithubUser.CrawlerGithubUserItem item) {
        GithubUserPo po=new GithubUserPo();
        po.setThirdId(item.getId());
        po.setUserId(item.getUser_id());
        po.setUserName(item.getUser_name());
        po.setUserType(item.getUser_type());
        po.setHtmlUrl(item.getHtml_url());
        po.setAvatarUrl(item.getAvatar_url());
        po.setUserCreatedTime(item.getUser_created_at());
        po.setCompany(item.getCompany());
        po.setBlog(item.getBlog());
        po.setLocation(item.getLocation());
        po.setEmail(item.getEmail());
        po.setSubTile(item.getSub_tile());

        po.setSrcId(item.getSrc_id());
        po.setCreateTime(item.getCreate_time());
        return po;
    }
}
