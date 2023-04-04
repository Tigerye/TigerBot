package com.tigerobo.x.pai.biz.biz;

import com.tigerobo.x.pai.api.biz.service.SummaryService;
import com.tigerobo.x.pai.api.vo.TaskQueryVo;
import com.tigerobo.x.pai.api.vo.biz.TaskVo;
import com.tigerobo.x.pai.api.vo.biz.mine.MineCountVo;
import com.tigerobo.x.pai.api.entity.IndexItem;
import com.tigerobo.x.pai.api.entity.IndexType;
import com.tigerobo.x.pai.api.exception.AuthorizeException;
import com.tigerobo.x.pai.api.exception.ResultCode;
import com.tigerobo.x.pai.api.vo.IndexVo;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.QueryVo;
import com.tigerobo.x.pai.biz.biz.blog.BlogSearchService;
import com.tigerobo.x.pai.biz.biz.blog.BlogService;
import com.tigerobo.x.pai.biz.biz.service.WebTaskService;
import com.tigerobo.x.pai.biz.biz.service.DemandServiceImpl;
import com.tigerobo.x.pai.biz.cache.RedisCacheService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.dal.aml.dao.AmlModelDao;
import com.tigerobo.x.pai.dal.biz.dao.ApiDao;
import com.tigerobo.x.pai.dal.biz.dao.DemandDao;
import com.tigerobo.x.pai.dal.biz.dao.TaskModelDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@Service
public class SummaryServiceImpl implements SummaryService {
    @Autowired
    private DemandServiceImpl demandServiceV2;

    @Autowired
    private WebTaskService webTaskService;

    @Autowired
    private ApiDao apiDao;

    @Autowired
    private TaskModelDao taskModelDao;

    @Autowired
    private DemandDao demandDao;

    @Autowired
    private AmlModelDao amlModelDao;

    @Autowired
    private BlogSearchService  blogSearchService;


    @Autowired
    private RedisCacheService redisCacheService;

    int cacheDemandCount = 0;
    @PostConstruct
    public void init(){
        apiDao.countOnlineList();
    }

    @Override
    public MineCountVo getMineCount() {
        Integer userId = ThreadLocalHolder.getUserId();
        if (userId == null || userId == 0) {
            throw new AuthorizeException(ResultCode.USER_NOT_LOGIN);
        }

        String createBy = String.valueOf(userId);
        int demandCount = demandDao.countMine(createBy);
        int amlModelCount = amlModelDao.countMine(createBy);

        int taskCount = taskModelDao.countMine(createBy);

        MineCountVo mineCountVo = new MineCountVo();
        mineCountVo.setDemandCount(demandCount);
        mineCountVo.setModelCount(amlModelCount);
        mineCountVo.setTaskCount(taskCount);
        return mineCountVo;
    }

    @Override
    public IndexVo homeIndices() {
        Integer userId = ThreadLocalHolder.getUserId();

        long appCount = getAppCount(userId);

        // 应用
        IndexItem appItem = IndexItem.builder().uid("application-total").name("应用总数").type(IndexType.APPLICATION_TOTAL).build();

        appItem.set("count", appCount);
        // 需求
        IndexItem demandItem = IndexItem.builder().uid("demand-total").name("需求总数").type(IndexType.DEMAND_TOTAL).build();

        if (cacheDemandCount==0){
            cacheDemandCount = demandServiceV2.countUserView(userId);
        }
        int demandCount = cacheDemandCount;

        demandItem.set("count", demandCount);
        // 开发者
        IndexItem developItem = IndexItem.builder().uid("developer-total").name("开发者总数").type(IndexType.DEVELOPER_TOTAL).build();
        developItem.set("count", 0);
        // 模型总数
        IndexItem modelItem = IndexItem.builder().uid("model-total").name("模型总数").type(IndexType.MODEL_TOTAL).build();

        modelItem.set("count", appCount);

        IndexItem blogItem = IndexItem.builder().uid("blog-total").name("blog更新总数").type(IndexType.BLOG_TOTAL).build();
        int blogInfoCount = blogSearchService.countNew();
        blogItem.set("count", blogInfoCount);

        IndexVo indexVo = IndexVo.builder().uid("homepage-indices").name("首页展示指标").type(IndexType.UNKNOWN).build();

        indexVo.addIndex(appItem);
        indexVo.addIndex(demandItem);
        indexVo.addIndex(developItem);
        indexVo.addIndex(modelItem);
        indexVo.addIndex(blogItem);
//        indexVo.addIndex(blogAllItem);
        return indexVo;
    }

    private long getAppCount(Integer userId) {

        if (userId == null){
            userId = 0;
        }
        final String appKey = getAppKey(userId);

        final Long aLong = redisCacheService.getLong(appKey);
        if (aLong!=null&&aLong>0){
            return aLong;
        }
        PageVo<TaskVo> taskPage = webTaskService.query(new TaskQueryVo());
        long appCount = taskPage.getTotal();
        redisCacheService.set(appKey,String.valueOf(appCount),3*60);
        return appCount;
    }

    private String getAppKey(Integer userId){
        return "pai:summary:app:count:user:"+userId;
    }
}
