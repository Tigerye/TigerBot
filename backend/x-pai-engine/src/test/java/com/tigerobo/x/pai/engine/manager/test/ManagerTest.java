package com.tigerobo.x.pai.engine.manager.test;

import com.tigerobo.x.pai.dal.aml.dao.AmlModelDao;
import com.tigerobo.x.pai.dal.aml.entity.AmlModelDo;
import com.tigerobo.x.pai.engine.EngineBaseTest;
import com.tigerobo.x.pai.engine.auto.ml.manager.DatasetPrepareManager;
import com.tigerobo.x.pai.engine.auto.ml.manager.StartTrainManager;
import com.tigerobo.x.pai.engine.auto.ml.manager.TrainProcessManager;
import com.tigerobo.x.pai.engine.auto.ml.manager.TrainServiceOnlineManager;
import com.tigerobo.x.pai.engine.manager.github.CrawlerGithubRepoManger;
import com.tigerobo.x.pai.engine.manager.github.CrawlerGithubUserManger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ManagerTest extends EngineBaseTest {

    @Autowired
    private DatasetPrepareManager datasetPrepareManager;

    @Autowired
    private StartTrainManager startTrainManager;

    @Autowired
    private TrainProcessManager trainProcessManager;


    @Autowired
    private TrainServiceOnlineManager trainServiceOnlineManager;


    @Autowired
    private CrawlerGithubRepoManger crawlerGithubRepoManger;

    @Autowired
    private CrawlerGithubUserManger crawlerGithubUserManger;
    @Autowired
    private AmlModelDao amlModelDao;

    @Test
    public void onlineTest(){

//        trainServiceOnlineManager.runOnlineTask();

        trainServiceOnlineManager.runOfflineTask();
    }
    @Test
    public void onProcessTest(){

        trainProcessManager.run();
    }

    @Test
    public void processOneTEST(){
        final AmlModelDo po = amlModelDao.getById(610835);
        trainProcessManager.deal(po);
    }
    @Test
    public void startTrainTest(){

        startTrainManager.run();
    }

    @Test
    public void datasetPrepareManagerTest(){


        datasetPrepareManager.dealUnhandled(true);
    }


    @Test
    public  void crawlerRepoTest(){
        crawlerGithubRepoManger.dealCrawlerRepoData();
    }

    @Test
    public  void crawlerUserTest(){
        crawlerGithubUserManger.dealCrawlerRepoData();
    }
}
