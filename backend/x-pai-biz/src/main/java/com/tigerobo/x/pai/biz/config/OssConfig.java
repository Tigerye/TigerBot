package com.tigerobo.x.pai.biz.config;

import com.tigerobo.x.pai.api.constants.OssConstant;
import com.tigerobo.x.pai.biz.oss.OSSHome;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author:Wsen
 * @time: 2021/1/18
 **/
@Component
@Slf4j
public class OssConfig {
    private String ossDomain = OssConstant.domainUrlClean;

    @Value("${xpai.oss.x-pai.access-key.id:''}")
    private String accessId;
    @Value("${xpai.oss.x-pai.access-key.secret:''}")
    private String accessKey;
    @Value("${xpai.oss.x-pai.bucket-name:''}")
    private String bucketName;
    @Value("${xpai.oss.x-pai.endpoint:''}")
    private String endpoint;

    @Value("${xpai.oss.x-pai.role-arn:''}")
    private String roleArn;

    @Bean(name = "ossHome",initMethod = "init")
    public OSSHome getBaseOSSApi(){
        OSSHome baseOssApi = new OSSHome();
        baseOssApi.setDomain(ossDomain);
        baseOssApi.setAccessId(accessId);
        baseOssApi.setAccessKey(accessKey);
        baseOssApi.setBucketName(bucketName);
        baseOssApi.setEndpoint(endpoint);
        baseOssApi.setRoleArn(roleArn);
        baseOssApi.init();
        return baseOssApi;
    }
}
