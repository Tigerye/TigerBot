package com.tigerobo.x.pai.biz.batch.service;

import com.tigerobo.x.pai.api.enums.BatchTaskEnum;
import com.tigerobo.x.pai.api.vo.IdReqVo;
import com.tigerobo.x.pai.api.vo.IdVo;
import com.tigerobo.x.pai.biz.cache.RedisCacheService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.dal.biz.dao.offline.ModelBatchTaskDao;
import com.tigerobo.x.pai.dal.biz.entity.offline.ModelBatchTaskPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Slf4j
@Service
public class BatchProcessService {

    @Autowired
    private ModelBatchTaskDao modelBatchTaskDao;

    @Autowired
    private RedisCacheService redisCacheService;


    public void cacheProcessRatio(Integer id, Integer ratio){
        if (id==null||ratio==null){
            return;
        }
        final String key = getProcessNumKey(id);

        redisCacheService.set(key,String.valueOf(ratio),3600);
    }

    public Integer getProcessRatio(Integer id){
        final String key = getProcessNumKey(id);
        return redisCacheService.getInteger(key);
    }

    private String getProcessNumKey(Integer id){

        return "pai:batch:process:num:"+id;
    }
    public void suspend(IdReqVo idVo){

        final Integer userId = ThreadLocalHolder.getUserId();

        final Integer id = idVo.getId();
        Validate.isTrue(id!=null,"参数不正确");
        final ModelBatchTaskPo po = modelBatchTaskDao.load(id);
        Validate.isTrue(po!=null,"任务不存在");

        Validate.isTrue(po.getUserId().equals(userId),"没有权限");

        doSuspend(po);
    }

    public void update2waitDeal(IdReqVo idVo){

        final Integer userId = ThreadLocalHolder.getUserId();

        final Integer id = idVo.getId();
        final ModelBatchTaskPo po = modelBatchTaskDao.load(id);
        Validate.isTrue(po!=null,"任务不存在");

        Validate.isTrue(po.getUserId().equals(userId),"没有权限");

        final Integer status = po.getStatus();
        final BatchTaskEnum taskEnum = BatchTaskEnum.getByStatus(status);
        Validate.isTrue(BatchTaskEnum.SUSPEND == taskEnum,"当前状态不能继续");
        ModelBatchTaskPo update = new ModelBatchTaskPo();
        update.setId(po.getId());
        update.setStatus(BatchTaskEnum.WAIT_DEAL.getStatus());

        final int updateNum = modelBatchTaskDao.update(update);
        Validate.isTrue(updateNum==1,"操作失败，请刷新后重试");

        final String key = getKey(id);
        if (redisCacheService.exist(key)){
            redisCacheService.del(key);
        }
    }

    public void suspend(Integer id){
        final ModelBatchTaskPo po = modelBatchTaskDao.load(id);
        doSuspend(po);
    }

    private void doSuspend(ModelBatchTaskPo po) {
        final Integer id = po.getId();
        final Integer status = po.getStatus();
        final BatchTaskEnum taskEnum = BatchTaskEnum.getByStatus(status);
        Validate.isTrue(BatchTaskEnum.WAIT_DEAL == taskEnum,"当前状态不能中止");
        ModelBatchTaskPo update = new ModelBatchTaskPo();
        update.setId(po.getId());
        update.setStatus(BatchTaskEnum.SUSPEND.getStatus());

        final int updateNum = modelBatchTaskDao.update(update);
        Validate.isTrue(updateNum==1,"操作失败，请刷新后重试");

        addTaskSuspend2cache(id);
    }


    public boolean isSuspend(Integer id){

        if (id == null){
            return false;
        }
        final String key = getKey(id);

        final boolean exist = redisCacheService.exist(key);
        if (!exist){
            return false;
        }

        int retry=0;

        boolean isDbSuspend = false;
        while (retry<2&&!isDbSuspend){
            if (retry>0){
                try {
                    Thread.sleep(80);
                } catch (InterruptedException e) {
                    log.error("retry:{}",retry,e);
                }
            }
            isDbSuspend = isDbRecordSuspend(id);
            retry++;
        }

        if (isDbSuspend){
            return true;
        }else {
            redisCacheService.del(key);
        }
        return false;
    }

    private boolean isDbRecordSuspend(Integer id){
        final ModelBatchTaskPo load = modelBatchTaskDao.loadWithNoCache(id);
        return load != null && BatchTaskEnum.SUSPEND.getStatus().equals(load.getStatus());
    }

    private void addTaskSuspend2cache(Integer id){

        final String key = getKey(id);
        redisCacheService.set(key,"1",3600);
    }

    private String getKey(Integer id){
        return "pai:batch:supend:"+id;
    }
}
