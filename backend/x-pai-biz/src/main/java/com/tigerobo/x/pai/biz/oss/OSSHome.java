package com.tigerobo.x.pai.biz.oss;


import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.comm.ResponseMessage;
import com.aliyun.oss.common.utils.IOUtils;
import com.aliyun.oss.model.*;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.aliyuncs.sts.model.v20150401.AssumeRoleRequest;
import com.aliyuncs.sts.model.v20150401.AssumeRoleResponse;
import com.google.common.collect.Maps;
import com.tigerobo.x.pai.api.constants.OssConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.lang.IllegalArgumentException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.Date;
import java.util.List;

@Data
public class OSSHome {
    private static final char PATH_SEP = '/';

    private static final Logger LOGGER = LoggerFactory.getLogger(OSSHome.class);

    // 阿里云API授权密钥ACCESS_ID和ACCESS_KEY
    private String domain;
    private String bucketName;
    private String accessId;
    private String accessKey;
    private String endpoint = "http://oss.aliyuncs.com";
    private String prefix;

    private OSSClient ossClient;
    private boolean initialized = false;
    String roleArn;

    public void init() {
        if (!initialized) {
            if (StringUtils.isEmpty(domain) || StringUtils.isEmpty(accessId) || StringUtils.isEmpty(accessKey)) {
                LOGGER.error("Invalid parameters to initialize Ali OSS Api");
                return;
            }
            try {
                ossClient = new OSSClient(endpoint, accessId, accessKey);
                LOGGER.info("Create OSS client successfully.");

                LOGGER.info("Initialize Ali OSS Api successfully.");
                initialized = true;
            } catch (Exception e) {
                LOGGER.error("Failed to initialize Ali OSS Api.", e);
            }
        }
    }

    public OSSObject get(String objectName) {
        return this.ossClient.getObject(this.bucketName, objectName);
    }


    public String getBaseUrl(String key){
        return OssConstant.domainUrl +key;
    }

    public OSSObject get(URL url) {
        return ossClient.getObject(url, Maps.newHashMap());
    }

    public InputStream download(URL url) {

        OSSObject ossObject = ossClient.getObject(url, Maps.newHashMap());
        return ossObject.getObjectContent();
    }

    public void download(String key, OutputStream outputStream) throws IOException {

        OSSObject ossObject = ossClient.getObject(bucketName,key);

        ossObject.getObjectContent();
        org.apache.commons.io.IOUtils.copy(ossObject.getObjectContent(),outputStream);
    }



    public String upload(String key, InputStream inputStream, String contentType) throws Exception {
        LOGGER.debug("Uploading url {} with original name {}", key);

        ossClient.putObject(bucketName,key,inputStream);
        String domain = this.getDomain();
        String path = new StringBuilder()
                .append(domain).append(PATH_SEP)
                .append(key).toString();
        LOGGER.debug("Done uploading the file to {}", path);
        return path;
    }
    public String upload(String originalFileName, byte[] content, String contentType) throws Exception {
//        LOGGER.debug("Uploading {} ", originalFileName);

        this.uploadByContent(bucketName, originalFileName, content, contentType);
        String domain = this.getDomain();
        String path = new StringBuilder()
                .append(domain).append(PATH_SEP)
                .append(originalFileName).toString();
//        LOGGER.debug("Done uploading the file to {}", path);
        return path;
    }


    public String returnValidUrlUpload(String originalFileName, byte[] content, String contentType) throws Exception {
        LOGGER.debug("Uploading {} with original name {}", originalFileName);

        String url = this.returnUrlUploadByContent(bucketName, originalFileName, content, contentType);
        LOGGER.debug("Done uploading the file to {}", url);
        return url;
    }


    /**
     * 上传文件
     *
     * @param key
     * @param content
     */
    private void uploadByContent(String bucketName, String key, byte[] content, String contentType) throws OSSException, ClientException {
        if (StringUtils.isEmpty(bucketName)) {
            throw new IllegalArgumentException("Invalid bucket name.");
        }
        if (ossClient == null) {
            throw new IllegalStateException("OSS client is not initialized successfully.");
        }
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        metadata.setContentLength(content.length);
        InputStream input = new ByteArrayInputStream(content);
        PutObjectResult result = ossClient.putObject(bucketName, key, input, metadata);

//        System.out.println();
    }

    /**
     * 上传文件 返回有效期的链接
     *
     * @param key
     * @param content
     */
    private String returnUrlUploadByContent(String bucketName, String key, byte[] content, String contentType) throws OSSException, ClientException {
        if (StringUtils.isEmpty(bucketName)) {
            throw new IllegalArgumentException("Invalid bucket name.");
        }
        if (ossClient == null) {
            throw new IllegalStateException("OSS client is not initialized successfully.");
        }
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        metadata.setContentLength(content.length);
        InputStream input = new ByteArrayInputStream(content);
        PutObjectResult result = ossClient.putObject(bucketName, key, input, metadata);

        //解析结果
        String url = getUrl(key);

        return url;
    }

    public String getUrl(String key) {
        // 设置URL过期时间为5年
        int delta = 3600 * 24 * 365 * 5;
        return getUrlWithTime(key,delta);
    }
    public String getUrlShortTime(String key) {
        // 设置URL过期时间为1H
        int delta = 3600;
        return getUrlWithTime(key,delta);
    }

    public String getUrlWithTime(String key, Integer second) {
        if (StringUtils.isBlank(key)){
            return null;
        }

        if (second == null) {
            second = 3600;
        }
        // 设置URL过期时间为1H
        Date expiration = DateUtils.addSeconds(new Date(),second);
        // 生成URL
        URL url = ossClient.generatePresignedUrl(this.bucketName, key, expiration);
        return url.toString();
    }

    /**
     * 获取文件内容
     *
     * @param bucketName
     * @param key
     * @return
     * @throws OSSException
     * @throws ClientException
     * @throws IOException
     */
    public byte[] getFile(String bucketName, String key) throws OSSException, ClientException, IOException {
        if (StringUtils.isEmpty(bucketName)) {
            throw new IllegalArgumentException("Invalid bucket name.");
        }
        if (ossClient == null) {
            throw new IllegalStateException("OSS client is not initialized successfully.");
        }

        OSSObject ossObject = ossClient.getObject(bucketName, key);

        // 获取Object的输入流
        InputStream is = ossObject.getObjectContent();
        byte[] data = null;
        try {
            data = IOUtils.readStreamAsByteArray(ossObject.getObjectContent());
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return data;
    }


    public void setRoleArn(String roleArn) {
        this.roleArn = roleArn;
    }

    public void setAclPrivate(String key) {
        this.ossClient.setObjectAcl(bucketName, key, CannedAccessControlList.Private);
    }

    public void setAclPublic(String key) {
        this.ossClient.setObjectAcl(bucketName, key, CannedAccessControlList.PublicRead);
    }


    public List<OSSObjectSummary> getList(String keyPrefix) {
//        ObjectListing objectListing = ossClient.listObjects(bucketName, keyPrefix);

        ListObjectsRequest request = new ListObjectsRequest();

        request.setBucketName(bucketName);
        request.setPrefix(keyPrefix);
        request.setMaxKeys(1000);
        ObjectListing objectListing = ossClient.listObjects(request);
        return objectListing.getObjectSummaries();
    }


    public OssToken ossToken(String roleSessionName) throws ClientException, com.aliyuncs.exceptions.ClientException {
        // 构造default profile。
        IClientProfile profile = DefaultProfile.getProfile("cn-shanghai", this.accessId, accessKey);
        // 构造client。
        DefaultAcsClient client = new DefaultAcsClient(profile);
        final AssumeRoleRequest request = new AssumeRoleRequest();
        request.setRoleArn(this.roleArn);
        request.setRoleSessionName(roleSessionName);
        //        request.setPolicy(policy); // 如果policy为空，则用户将获得该角色下所有权限。
        request.setDurationSeconds(3600L); // 设置临时访问凭证的有效时间为3600秒。
        final AssumeRoleResponse response = client.getAcsResponse(request);
        if (response != null && response.getCredentials() != null) {
            return OssToken.builder()
                    .region("oss-cn-shanghai")
                    .accessKeyId(response.getCredentials().getAccessKeyId())
                    .accessKeySecret(response.getCredentials().getAccessKeySecret())
                    .stsToken(response.getCredentials().getSecurityToken())
                    .bucket(this.bucketName)
                    .endPoint(this.domain)
                    .build();
        } else {
            throw new IllegalArgumentException("response/credentials");
        }

    }


    public void resizeImg(String key,File outputFile,int width,int height){
        String style = "image/resize,m_fixed,w_"+width+",h_"+height;

        GetObjectRequest request = new GetObjectRequest(bucketName, key);
        request.setProcess(style);

        ossClient.getObject(request,outputFile);
    }
    public OSSObject getImgResizeUrl(String key,int width,int height){

        String style = "image/resize,m_fixed,w_"+width+",h_"+height;
        GetObjectRequest request = new GetObjectRequest(bucketName, key);
        request.setProcess(style);

        return ossClient.getObject(request);
    }

    @Data
    @Builder
    public static class OssToken {
        private String region;
        private String accessKeyId;
        private String accessKeySecret;
        private String stsToken;
        private String bucket;
        private String objectName;
        private String endPoint;
        private String objectDir;
    }
}
