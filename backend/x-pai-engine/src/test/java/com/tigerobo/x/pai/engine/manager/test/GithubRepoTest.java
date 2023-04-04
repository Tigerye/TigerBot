package com.tigerobo.x.pai.engine.manager.test;

import com.tigerobo.x.pai.engine.EngineBaseTest;
import com.tigerobo.x.pai.engine.manager.github.CrawlerGithubRepoManger;
import com.tigerobo.x.pai.engine.manager.github.CrawlerGithubUserManger;
import com.tigerobo.x.pai.engine.task.Github2dbTask;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class GithubRepoTest extends EngineBaseTest {

    @Autowired
    private CrawlerGithubRepoManger crawlerGithubRepoManger;
    @Autowired
    private CrawlerGithubUserManger crawlerGithubUserManger;

    @Test
    public void repoTest(){
        crawlerGithubRepoManger.dealCrawlerRepoData();
    }


    @Test
    public void userTest(){
        crawlerGithubUserManger.dealCrawlerRepoData();
    }
}
