package com.tigerobo.x.pai.service.aspect;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@Order(3)
public class BizServiceAspect {
//    @Pointcut("execution(public * com.tigerobo.x.pai.service.controller..*.*(..))")
    @Pointcut("execution(public * com.tigerobo.x.pai.biz..*Service*.*(..))")
//    @Pointcut("@annotation(org.springframework.stereotype.Service)")
    public void allLogic() {
    }
    @Pointcut("execution(public * com.tigerobo.x.pai.dal.biz.dao.blog.BlogInfoDao.*(..))")
//    @Pointcut("@annotation(org.springframework.stereotype.Service)")
    public void daoLogic() {
    }

    @Around("allLogic()")
    public Object doAroundLogic(ProceedingJoinPoint pjp) throws Throwable {
        final String biz = "Service";
        return addCat(pjp, biz);
    }

    @Around("daoLogic()")
    public Object daoAroundLogic(ProceedingJoinPoint pjp) throws Throwable {
        final String biz = "Dao";
        return addCat(pjp, biz);
    }

    private Object addCat(ProceedingJoinPoint pjp, String biz) throws Throwable {
        final String name = pjp.getSignature().toShortString();

        Transaction t = Cat.newTransaction(biz, name);
        try {
            final Object proceed = pjp.proceed();
            t.setStatus(Transaction.SUCCESS);

            return proceed;
        }finally {
            t.complete();
        }
    }
}
