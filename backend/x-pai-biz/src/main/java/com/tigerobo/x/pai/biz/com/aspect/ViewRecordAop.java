package com.tigerobo.x.pai.biz.com.aspect;

import com.tigerobo.x.pai.api.biz.entity.Task;
import com.tigerobo.x.pai.api.entity.Entity;
import com.tigerobo.x.pai.api.vo.biz.DemandDetailVo;
import com.tigerobo.x.pai.api.vo.biz.ModelVo;
import com.tigerobo.x.pai.api.vo.biz.TaskVo;
import com.tigerobo.x.pai.dal.redis.RedisTmpUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description:
 * @modified By:
 * @version: $
 */
@Slf4j
@Aspect
@Component
public class ViewRecordAop {

    @Autowired
    protected RedisTmpUtils redisTmpUtils;

    @AfterReturning(value = "@annotation(com.tigerobo.x.pai.biz.com.aspect.ViewRecord) && @annotation(org.springframework.web.bind.annotation.PostMapping)", returning = "returnVal")
    public void pageView(JoinPoint joinPoint, Object returnVal) throws Throwable {
        if (returnVal instanceof DemandDetailVo) {
            DemandDetailVo responseVo = (DemandDetailVo) returnVal;
//            Entity.Type type = ;
            String uuid = responseVo.getUuid();
            incr(Entity.Type.TASK, uuid);
        }else if (returnVal instanceof ModelVo) {
            ModelVo responseVo = (ModelVo) returnVal;
            Entity.Type type = Entity.Type.MODEL;
            String uuid = responseVo.getUuid();
            incr(type, uuid);
        }else if (returnVal instanceof TaskVo) {
            TaskVo responseVo = (TaskVo) returnVal;
            String uuid = responseVo.getUuid();
            incr(Entity.Type.TASK, uuid);
            Task task = responseVo.getTask();
            if (task!=null&&!StringUtils.isEmpty(task.getModelUuid())){
                String modelUuid = task.getModelUuid();
                incr(Entity.Type.MODEL,modelUuid);
                log.info("aop-view-incr:task2model:taskId:{},modelUuid:{}",uuid,modelUuid);
            }
        }
    }

    private void incr(Entity.Type type, String uuid) {
        if (StringUtils.isEmpty(uuid)){
            return;
        }
        String name = type.getModule().getName();
        String s = type.toString().toLowerCase();
        String key = name + ":" + s + ":stat:" + uuid;
        String item = "view";
        Double cnt = redisTmpUtils.hincr(key, item, 1);

        log.info("stat: {} -> {}:{}", key, item, cnt);
    }
}
