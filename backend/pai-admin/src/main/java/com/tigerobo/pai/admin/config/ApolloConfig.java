package com.tigerobo.pai.admin.config;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description:
 * @modified By:
 * @version: $
 */
@Slf4j
@Component
public class ApolloConfig {
    // Apollo 配置获取 并 监听更新
    private final static Config config = ConfigService.getAppConfig();

    static {
        config.addChangeListener(new ConfigChangeListener() {
            @Override
            public void onChange(ConfigChangeEvent changeEvent) {
                log.info("Changes for namespace: {}", changeEvent.getNamespace());
                for (String key : changeEvent.changedKeys()) {
                    ConfigChange change = changeEvent.getChange(key);
                    log.info("Found change - key: {}, oldValue: {}, newValue: {}, changeType: {}",
                            change.getPropertyName(), change.getOldValue(), change.getNewValue(), change.getChangeType());
                }
            }
        });
    }
}
