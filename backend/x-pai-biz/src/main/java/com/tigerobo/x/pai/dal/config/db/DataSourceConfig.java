package com.tigerobo.x.pai.dal.config.db;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tk.mybatis.spring.annotation.MapperScan;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;


/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description:
 * @modified By:
 * @version: $
 */
@Slf4j
@Configuration
@MapperScan(basePackages = {
        "com.tigerobo.x.pai.dal"
},
        sqlSessionFactoryRef = "paiSqlSessionFactory")
//@MapperScan(basePackages = {
//        "com.tigerobo.x.pai.dal.aml.mapper.*"
//        ,"com.tigerobo.x.pai.dal.auth.mapper.*"
//        ,"com.tigerobo.x.pai.dal.biz.mapper.*"
//        ,"com.tigerobo.x.pai.dal.admin.mapper.*"
//        ,"com.tigerobo.x.pai.dal.pay.mapper.*"
//        ,"com.tigerobo.x.pai.dal.ai.mapper.*"
//},
//        sqlSessionFactoryRef = "paiSqlSessionFactory")
@EnableTransactionManagement
public class DataSourceConfig {
    @Bean(name = "paiDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.x-pai")
    public DataSource defaultDataSource() {
        final DataSource build = DataSourceBuilder.create().build();
        return build;
    }
//
//    @Bean(name = "dynamicDataSource")
//    public DynamicDataSource DataSource(@Qualifier("primaryDataSource") DataSource primaryDataSource) {
//
//        Map<Object, Object> targetDataSource = new HashMap<>();
//        log.info("X_PAI: {}", ((HikariDataSource) primaryDataSource).getJdbcUrl());
//        targetDataSource.put(DataSourceType.DataBaseType.X_PAI, primaryDataSource);
//
//        DynamicDataSource dataSource = new DynamicDataSource();
//        dataSource.setTargetDataSources(targetDataSource);
//        dataSource.setDefaultTargetDataSource(primaryDataSource);
//        return dataSource;
//    }

    @Bean(name = "paiTm")
    public DataSourceTransactionManager paiTm(@Qualifier("paiDataSource") DataSource adsDataSource) {
        return new DataSourceTransactionManager(adsDataSource);
    }

    @Bean(name = "paiSqlSessionFactory")
    public SqlSessionFactory paiSqlSessionFactory(@Qualifier("paiDataSource") DataSource dataSource)
            throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(getMapperLocations("classpath*:mapper/*.xml"
                ,"classpath*:mapper/ai/*.xml"
                ,"classpath*:mapper/admin/*.xml"
                ,"classpath*:mapper/pay/*.xml"));
        final SqlSessionFactory object = bean.getObject();
        return object;
    }

    private Resource[] getMapperLocations(String... locationPatterns) throws Exception {
        List<Resource> mapperLocations = new ArrayList<>();
        for (String locationPattern : locationPatterns)
            mapperLocations.addAll(Lists.newArrayList(new PathMatchingResourcePatternResolver().getResources(locationPattern)));
        Resource[] resources = new Resource[mapperLocations.size()];
        mapperLocations.toArray(resources);
        return resources;
    }
}
