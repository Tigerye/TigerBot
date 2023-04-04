package com.tigerobo.x.pai.api.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description:
 * @modified By:
 * @version: $
 */
public final class ConditionConfig {

    public static class XPaiAuthCondition implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return System.getProperty("app.id").equalsIgnoreCase("x-pai-auth");
        }
    }

    public static class XPaiBizCondition implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return System.getProperty("app.id").equalsIgnoreCase("x-pai");
        }
    }

    public static class XPaiEngineCondition implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return System.getProperty("app.id").equalsIgnoreCase("x-pai-engine");
        }
    }

    public static class XPaiRepoCondition implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return System.getProperty("app.id").equalsIgnoreCase("x-pai-repo");
        }
    }

    public static class XPaiServingCondition implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return System.getProperty("app.id").equalsIgnoreCase("x-pai-serving");
        }
    }
}
