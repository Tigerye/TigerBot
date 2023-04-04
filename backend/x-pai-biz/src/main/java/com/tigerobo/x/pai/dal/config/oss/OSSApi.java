package com.tigerobo.x.pai.dal.config.oss;

import com.alibaba.fastjson.JSON;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.internal.OSSUtils;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import java.lang.IllegalArgumentException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.activation.MimetypesFileTypeMap;
import javax.annotation.PostConstruct;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description:
 * @modified By:
 * @version: $
 */
@Slf4j
@Data
@Configuration
@Component
public class OSSApi {
    public static String domainUrl = OssConstant.domainUrl;
    String innerUrl = "https://x-pai.oss-cn-shanghai-internal.aliyuncs.com/";
    private final static char PATH_SEP = '/';
//    @Value("${xpai.oss.x-pai.domain:''}")
    private String domain = domainUrl;
    @Value("${xpai.oss.x-pai.access-key.id:''}")
    private String accessKeyId;
    @Value("${xpai.oss.x-pai.access-key.secret:''}")
    private String accessKeySecret;
    @Value("${xpai.oss.x-pai.bucket-name:''}")
    private String bucketName;
    @Value("${xpai.oss.x-pai.endpoint:''}")
    private String endpoint;
    @Value("${xpai.oss.x-pai.role-arn:''}")
    private String roleArn;

    private volatile OssToken ossToken;
    private volatile OSS ossClient;
    private boolean initialized = false;

    @Value("${task.support.win:false}")
    boolean supportWin;

    @PostConstruct
    public void init() {
        if (!supportWin) {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("windows")) {
                return;
            }
        }
        dealInit();
    }

    private void dealInit() {
        if (ossClient == null) {
            synchronized (OSSApi.class) {
                if (ossClient == null) {
                    doInit();
                }
            }
        }
    }

    private void doInit() {

        if (!this.initialized) {
            if (StringUtils.isEmpty(this.endpoint) || StringUtils.isEmpty(this.accessKeyId) || StringUtils.isEmpty(this.accessKeySecret)) {
                log.error("Invalid parameters to initialize Ali OSS Api");
                return;
            }
            try {
//                OssToken ossToken = this.ossToken("oss-init");
//                this.ossClient = new OSSClientBuilder().build(ossToken.getEndPoint(), ossToken.getAccessKeyId(), ossToken.getAccessKeySecret());
                this.ossClient = new OSSClientBuilder().build(this.endpoint, this.accessKeyId, this.accessKeySecret);
                if (this.ossClient.doesBucketExist(this.bucketName)) {
                    log.info("Create OSS client successfully.");
                    log.info("Initialize Ali OSS Api successfully.");
                    this.initialized = true;
                } else
                    log.error("Failed to initialize Ali OSS Api.");
            } catch (Exception e) {
                log.error("Failed to initialize Ali OSS Api.", e);
            }
        }
    }


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
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

    public OssToken ossToken(String roleSessionName) throws ClientException, com.aliyuncs.exceptions.ClientException {
        // 构造default profile。
        IClientProfile profile = DefaultProfile.getProfile("cn-shanghai", this.accessKeyId, this.accessKeySecret);
        // 构造client。
        DefaultAcsClient client = new DefaultAcsClient(profile);
        final AssumeRoleRequest request = new AssumeRoleRequest();
        request.setRoleArn(this.roleArn);
        request.setRoleSessionName(roleSessionName);
        //        request.setPolicy(policy); // 如果policy为空，则用户将获得该角色下所有权限。
        request.setDurationSeconds(3600L); // 设置临时访问凭证的有效时间为3600秒。
        final AssumeRoleResponse response = client.getAcsResponse(request);
        if (response != null && response.getCredentials() != null)
            return OssToken.builder()
                    .region("oss-cn-shanghai")
                    .accessKeyId(response.getCredentials().getAccessKeyId())
                    .accessKeySecret(response.getCredentials().getAccessKeySecret())
                    .stsToken(response.getCredentials().getSecurityToken())
                    .bucket(this.bucketName)
                    .endPoint(this.domain)
                    .build();
        else
            throw new IllegalArgumentException("response/credentials");
    }

    /**
     * 获取OSS对象
     *
     * @param objectName
     * @return
     */
    public OSSObject get(String objectName) {
        return this.ossClient.getObject(this.bucketName, objectName);
    }

    public List<OSSObjectSummary> getList(String keyPrefix) {
        if (ossClient==null){
            doInit();
        }
        ObjectListing objectListing = ossClient.listObjects(bucketName, keyPrefix);
        return objectListing.getObjectSummaries();
    }

    /**
     * 获取对象访问地址
     *
     * @param objectName
     * @return
     */
    public URL getUrl(String objectName) {
        Date expiration = new Date((new Date()).getTime() + 3153600000000L);
        return this.ossClient.generatePresignedUrl(this.bucketName, objectName, expiration);
    }

    public String getUrl2(String objectName) {
        if (!StringUtils.isEmpty(objectName))
            return (this.domain + PATH_SEP + objectName).replaceAll("//", "/").replaceAll(":/", "://");
        return null;
    }

    /**
     * 获取对象信息
     *
     * @param objectName
     * @return
     */
    public OSSObject getObject(String objectName) {
        if (this.ossClient == null)
            throw new IllegalStateException("OSS client is not initialized successfully.");
        return this.ossClient.getObject(this.bucketName, objectName);
    }

    /**
     * 对象是否存在
     *
     * @param objectName
     * @return
     */
    public boolean exists(String objectName) {
        return this.ossClient.doesObjectExist(this.bucketName, objectName);
    }

    /**
     * 上传字符串
     *
     * @param objectName
     * @param content
     */
    public OSSObject upload(String objectName, String content) {
        if (ossClient == null){
            dealInit();
        }
        if (this.ossClient == null)
            throw new IllegalStateException("OSS client is not initialized successfully.");
        return this.upload(objectName, content.getBytes(), "text/plain");
    }

    /**
     * 上传Byte数组
     *
     * @param objectName
     * @param content
     * @param contentType
     */
    public OSSObject upload(String objectName, byte[] content, String contentType) throws OSSException, ClientException {

        if (ossClient == null){
            dealInit();
        }
        if (this.ossClient == null)
            throw new IllegalStateException("OSS client is not initialized successfully.");

        // 如果需要上传时设置存储类型和访问权限，请参考以下示例代码。
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        metadata.setContentMD5(BinaryUtil.toBase64String(BinaryUtil.calculateMd5(content)));
        metadata.setContentLength((long) content.length);
        // metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
        // metadata.setObjectAcl(CannedAccessControlList.Private);

        // 创建PutObjectRequest对象。
        // 依次填写Bucket名称（例如example-bucket）和Object完整路径（例如example-dir/example-object.txt）。Object完整路径中不能包含Bucket名称。
        PutObjectRequest putObjectRequest = new PutObjectRequest(this.bucketName, objectName, new ByteArrayInputStream(content), metadata);
        // 上传Byte数组。
        PutObjectResult putObjectResult = ossClient.putObject(putObjectRequest);
        return this.ossClient.getObject(bucketName, objectName);
    }

    /**
     * 上传网络流/文件流
     *
     * @param objectName
     * @param inputStream
     * @param contentType
     */
    public OSSObject upload(String objectName, InputStream inputStream, String contentType) throws OSSException, ClientException {
        if (this.ossClient == null) {
            dealInit();
        }
        if (this.ossClient == null) {
            throw new IllegalStateException("OSS client is not initialized successfully.");
        }
        // 如果需要上传时设置存储类型和访问权限，请参考以下示例代码。
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        // metadata.setContentLength((long) content.length);
        // metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
        // metadata.setObjectAcl(CannedAccessControlList.Private);

        // 创建PutObjectRequest对象。
        // 填写网络流地址。
        // InputStream inputStream = new URL("https://www.aliyun.com/").openStream();
        // 填写本地文件的完整路径。如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件流。
        // InputStream inputStream = new FileInputStream("D:\\local-path\\example-file.txt");
        // 依次填写Bucket名称（例如example-bucket）和Object完整路径（例如example-dir/example-object.txt）。Object完整路径中不能包含Bucket名称。
        PutObjectRequest putObjectRequest = new PutObjectRequest(this.bucketName, objectName, inputStream, metadata);
        // 上传网络流/文件流。
        PutObjectResult putObjectResult = this.ossClient.putObject(putObjectRequest);
        return this.ossClient.getObject(this.bucketName, objectName);
    }

    /**
     * 上传文件
     *
     * @param objectName
     * @param uploadFile
     * @param contentType
     */
    public OSSObject upload(String objectName, File uploadFile, String contentType) throws OSSException, ClientException {
        if (this.ossClient == null){
            dealInit();
        }
        if (this.ossClient == null){
            throw new IllegalStateException("OSS client is not initialized successfully.");
        }

        // 如果需要上传时设置存储类型和访问权限，请参考以下示例代码。
        ObjectMetadata metadata = new ObjectMetadata();
        if (!StringUtils.isEmpty(contentType))
            metadata.setContentType(contentType);
        // metadata.setContentLength((long) content.length);
        // metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
        // metadata.setObjectAcl(CannedAccessControlList.Private);

        // 创建PutObjectRequest对象。
        // 依次填写Bucket名称（例如example-bucket）和Object完整路径（例如example-dir/example-object.txt）。Object完整路径中不能包含Bucket名称。
        // 如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件。
        PutObjectRequest putObjectRequest = new PutObjectRequest(this.bucketName, objectName, uploadFile, metadata);
        // 上传文件
        PutObjectResult putObjectResult = this.ossClient.putObject(putObjectRequest);
        return this.ossClient.getObject(this.bucketName, objectName);
    }

    /**
     * 断点续传
     *
     * @param objectName
     * @param uploadFile
     * @param taskNum
     * @param partSize
     * @param enableCheckpoint
     * @param checkPointFile
     * @param callback
     * @throws Throwable
     */
    public OSSObject upload(String objectName, String uploadFile, long partSize, int taskNum, boolean enableCheckpoint, String checkPointFile, Callback callback) throws Throwable {
        if (this.ossClient == null)
            throw new IllegalStateException("OSS client is not initialized successfully.");
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            // 指定上传的内容类型。
            metadata.setContentType("text/plain");
            // 文件上传时设置访问权限ACL。
            // metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
            // metadata.setObjectAcl(CannedAccessControlList.Private);

            // 通过UploadFileRequest设置多个参数。
            // 填写Bucket名称和Object完整路径。Object完整路径中不能包含Bucket名称。
            UploadFileRequest uploadFileRequest = new UploadFileRequest(this.bucketName, objectName, uploadFile, (partSize > 1024L ? partSize : 1024 * 1024L),
                    (taskNum > 0 ? taskNum : 1), enableCheckpoint, checkPointFile);
            // 文件的元数据。
            uploadFileRequest.setObjectMetadata(metadata);
            // 设置上传成功回调，参数为Callback类型。
            uploadFileRequest.setCallback(callback);

            // 断点续传上传。
            UploadFileResult uploadFileResult = ossClient.uploadFile(uploadFileRequest);
            return this.ossClient.getObject(this.bucketName, objectName);
        } catch (Throwable t) {
            log.error("Upload breakpoint resume exception: ", t);
            throw t;
        }
    }

    public OSSObject copy(String objectName, String targetBucketName, String targetObjectName) {
        if (this.ossClient == null)
            throw new IllegalStateException("OSS client is not initialized successfully.");
        if (!this.ossClient.doesBucketExist(bucketName))
            throw new IllegalStateException("OSS targetBucketName does not Exist.");
        if (!this.ossClient.doesObjectExist(this.bucketName, objectName))
            throw new IllegalStateException("OSS sourceObjectName does not Exist.");
        this.ossClient.copyObject(this.bucketName, objectName, targetBucketName, targetObjectName);
        return this.ossClient.getObject(targetBucketName, targetObjectName);
    }

    public OSSObject copy(String objectName, String targetObjectName) {
        return this.copy(objectName, this.bucketName, targetObjectName);
    }

    /**
     * 上传表单
     *
     * @param objectName
     * @param uploadFile
     * @param callback
     * @throws Exception
     */
    public String upload(String objectName, String uploadFile, Callback callback) throws Exception {
        if (this.ossClient == null)
            throw new IllegalStateException("OSS client is not initialized successfully.");
        // 在URL中添加存储空间名称，添加后URL如下：http://yourBucketName.oss-cn-hangzhou.aliyuncs.com。
        String urlStr = !StringUtils.isEmpty(this.domain) ? this.domain : this.endpoint.replace("https://", "https://" + this.bucketName + ".");
        // 设置表单Map。
        Map<String, String> formFields = new LinkedHashMap<String, String>();
        // 设置文件名称。
        formFields.put("key", objectName);
        // 设置Content-Disposition。
        formFields.put("Content-Disposition", "attachment;filename=" + uploadFile);
        /**
         // 设置回调参数。
         Callback callback = new Callback();
         // 设置回调服务器地址，如http://oss-demo.aliyuncs.com:23450或http://127.0.0.1:9090。
         callback.setCallbackUrl("<yourCallbackServerUrl>");
         // 设置回调请求消息头中Host的值，如oss - cn - hangzhou.aliyuncs.com。
         callback.setCallbackHost("<yourCallbackServerHost>");
         // 设置发起回调时请求body的值。
         callback.setCallbackBody("{\\\"mimeType\\\":${mimeType},\\\"size\\\":${size}}");
         // 设置发起回调请求的Content - Type。
         callback.setCallbackBodyType(CallbackBodyType.JSON);
         // 设置发起回调请求的自定义参数，由Key和Value组成，Key必须以x: 开始，且必须小写。
         callback.addCallbackVar("x:var1", "value1");
         callback.addCallbackVar("x:var2", "value2");
         */
        // 在表单Map中设置回调参数。
        setCallBack(formFields, callback);
        // 设置OSSAccessKeyId。
        formFields.put("OSSAccessKeyId", this.accessKeyId);
        String policy = "{\"expiration\": \"2120-01-01T12:00:00.000Z\",\"conditions\": [[\"content-length-range\", 0, 104857600]]}";
        String encodePolicy = new String(Base64.encodeBase64(policy.getBytes()));
        // 设置policy。
        formFields.put("policy", encodePolicy);
        // 生成签名。
        String signature = com.aliyun.oss.common.auth.ServiceSignature.create().computeSignature(accessKeySecret, encodePolicy);
        // 设置签名。
        formFields.put("Signature", signature);
        String ret = uploadForm(urlStr, formFields, uploadFile);
//        System.out.println("Post Object [" + objectName + "] to bucket [" + this.bucketName + "]");
//        System.out.println("Post Response:" + ret);
        return ret;
    }

    private static String uploadForm(String urlStr, Map<String, String> formFields, String uploadFile) throws Exception {
        String res = "";
        HttpURLConnection conn = null;
        String boundary = "9431149156168";
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(30000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
            // 设置MD5值。MD5值由整个body计算得出。
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            OutputStream out = new DataOutputStream(conn.getOutputStream());
            // 遍历读取表单Map中的数据，将数据写入到输出流中。
            if (formFields != null) {
                StringBuffer strBuf = new StringBuffer();
                Iterator<Map.Entry<String, String>> iter = formFields.entrySet().iterator();
                int i = 0;
                while (iter.hasNext()) {
                    Map.Entry<String, String> entry = iter.next();
                    String inputName = entry.getKey();
                    String inputValue = entry.getValue();
                    if (inputValue == null) {
                        continue;
                    }
                    if (i == 0) {
                        strBuf.append("--").append(boundary).append("\r\n");
                        strBuf.append("Content-Disposition: form-data; name=\"" + inputName + "\"\r\n\r\n");
                        strBuf.append(inputValue);
                    } else {
                        strBuf.append("\r\n").append("--").append(boundary).append("\r\n");
                        strBuf.append("Content-Disposition: form-data; name=\"" + inputName + "\"\r\n\r\n");
                        strBuf.append(inputValue);
                    }
                    i++;
                }
                out.write(strBuf.toString().getBytes());
            }
            conn.setRequestProperty("Content-MD5", DigestUtils.md5Hex(formFields.toString()));
            // 读取文件信息，将要上传的文件写入到输出流中。
            File file = new File(uploadFile);
            String filename = file.getName();
            String contentType = new MimetypesFileTypeMap().getContentType(file);
            if (contentType == null || contentType.equals("")) {
                contentType = "application/octet-stream";
            }
            StringBuffer strBuf = new StringBuffer();
            strBuf.append("\r\n").append("--").append(boundary)
                    .append("\r\n");
            strBuf.append("Content-Disposition: form-data; name=\"file\"; "
                    + "filename=\"" + filename + "\"\r\n");
            strBuf.append("Content-Type: " + contentType + "\r\n\r\n");
            out.write(strBuf.toString().getBytes());
            DataInputStream in = new DataInputStream(new FileInputStream(file));
            int bytes = 0;
            byte[] bufferOut = new byte[1024];
            while ((bytes = in.read(bufferOut)) != -1) {
                out.write(bufferOut, 0, bytes);
            }
            in.close();
            byte[] endData = ("\r\n--" + boundary + "--\r\n").getBytes();
            out.write(endData);
            out.flush();
            out.close();
            // 读取返回数据。
            strBuf = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                strBuf.append(line).append("\n");
            }
            res = strBuf.toString();
            reader.close();
            reader = null;
        } catch (Exception e) {
            log.error("Send post request exception: ", e);
            throw e;
        } finally {
            if (conn != null) {
                conn.disconnect();
                conn = null;
            }
        }
        return res;
    }

    private static void setCallBack(Map<String, String> formFields, Callback callback) {
        if (callback != null) {
            String jsonCb = OSSUtils.jsonizeCallback(callback);
            String base64Cb = BinaryUtil.toBase64String(jsonCb.getBytes());
            formFields.put("callback", base64Cb);
            if (callback.hasCallbackVar()) {
                Map<String, String> varMap = callback.getCallbackVar();
                for (Map.Entry<String, String> entry : varMap.entrySet()) {
                    formFields.put(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    private static Callback callback(String url, Map<String, Object> body, Callback.CalbackBodyType contentType, Map<String, String> varMap) {
        // 上传回调参数。
        Callback callback = new Callback();
        callback.setCallbackUrl(url);
        //（可选）设置回调请求消息头中Host的值，即您的服务器配置Host的值。
        // callback.setCallbackHost("yourCallbackHost");
        // 设置发起回调时请求body的值。
        callback.setCallbackBody(JSON.toJSONString(body));
        // 设置发起回调请求的Content-Type。
        callback.setCalbackBodyType(contentType != null ? contentType : Callback.CalbackBodyType.JSON);
        // 设置发起回调请求的自定义参数，由Key和Value组成，Key必须以x:开始。
        for (Map.Entry<String, String> entry : varMap.entrySet()) {
            if (entry.getKey().startsWith("x:"))
                callback.addCallbackVar(entry.getKey(), entry.getValue());
        }
        return callback;
    }

    /**
     * 下载到本地文件
     *
     * @param objectName
     * @param localFile
     * @return
     */
    public File download(String objectName, String localFile) {
        // 下载Object到本地文件，并保存到指定的本地路径中。如果指定的本地文件存在会覆盖，不存在则新建。
        // 如果未指定本地路径，则下载后的文件默认保存到示例程序所属项目对应本地路径中。
        try {
            this.ossClient.getObject(new GetObjectRequest(this.bucketName, objectName), new File(localFile));
            return new File(localFile);
        } finally {
            // 关闭OSSClient。
            this.ossClient.shutdown();
        }
    }

    /**
     * 流式下载
     *
     * @param objectName
     * @return
     */
    public InputStream download(String objectName) {
        // 下载Object到本地文件，并保存到指定的本地路径中。如果指定的本地文件存在会覆盖，不存在则新建。
        OSSObject ossObject = ossClient.getObject(bucketName, objectName);
        return ossObject.getObjectContent();
    }

    public InputStream download(URL url) {
        if(ossClient == null){
            doInit();
        }
        OSSObject ossObject = ossClient.getObject(url, Maps.newHashMap());
        return ossObject.getObjectContent();
    }
}
