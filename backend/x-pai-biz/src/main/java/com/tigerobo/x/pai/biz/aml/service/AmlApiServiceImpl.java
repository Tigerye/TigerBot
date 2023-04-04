package com.tigerobo.x.pai.biz.aml.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.base.Preconditions;
import com.tigerobo.x.pai.api.auth.service.UserService;
import com.tigerobo.x.pai.api.enums.Style;
import com.tigerobo.x.pai.api.enums.ModelCallSourceEnum;
import com.tigerobo.x.pai.api.enums.ModelCallTypeEnum;
import com.tigerobo.x.pai.api.es.EsModelCall;
import com.tigerobo.x.pai.api.exception.APIException;
import com.tigerobo.x.pai.api.exception.AuthorizeException;
import com.tigerobo.x.pai.api.exception.ResultCode;
import com.tigerobo.x.pai.api.serving.vo.ApiReqVo;
import com.tigerobo.x.pai.api.serving.vo.ApiResultVo;
import com.tigerobo.x.pai.biz.aml.AmlModelExecutor;
import com.tigerobo.x.pai.biz.biz.OssService;
import com.tigerobo.x.pai.biz.data.es.EsService;
import com.tigerobo.x.pai.biz.io.IOService;
import com.tigerobo.x.pai.biz.lake.LakeInferService;
import com.tigerobo.x.pai.biz.serving.ApiCountService;
import com.tigerobo.x.pai.biz.batch.offline.TextEvaluator;
import com.tigerobo.x.pai.biz.utils.AccessUtil;
import com.tigerobo.x.pai.biz.utils.AmlDemoUtil;
import com.tigerobo.x.pai.biz.utils.MdUtil;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.dal.aml.dao.AmlModelDao;
import com.tigerobo.x.pai.dal.aml.entity.AmlModelDo;
import com.tigerobo.x.pai.dal.auth.dao.UserDao;
import com.tigerobo.x.pai.dal.auth.entity.UserDo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.File;
import java.nio.file.Files;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AmlApiServiceImpl {

    @Autowired
    private AmlModelDao amlModelDao;

    //    @Value("${pai.env.aml.prefix}")
//    private String envPrefix;
    @Value("${xpai.serving.invoke-url:}")
    private String serverUrl;
    @Resource
    Environment environment;
    @Autowired
    private LakeInferService lakeInferService;

    @Autowired
    private OssService ossService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private IOService ioService;

    @Autowired
    private EsService esService;

    @Autowired
    private ApiCountService apiCountService;

    @Autowired
    private UserService userService;

    Cache<Integer, AmlModelDo> userCache = Caffeine.newBuilder()
            .expireAfterWrite(3, TimeUnit.MINUTES)
            .maximumSize(300)
            .build();

    public AmlModelDo getModelFromCache(Integer modelId){
        if (modelId == null){
            return null;
        }
        try {
            return userCache.get(modelId, k -> amlModelDao.getById(modelId));
        }catch (Exception ex){
            log.error("modelId:{}",modelId,ex);
        }
        return null;
    }

    //    @Override
//    @ApiRecord
    public ApiResultVo apiExecute(ApiReqVo request, Integer userId) {
        Preconditions.checkArgument(userId != null, "用户未登录");

        String apiKey = request.getApiKey();
        Preconditions.checkArgument(!StringUtils.isEmpty(apiKey) && apiKey.matches("\\d+"), "模型不存在");

        int modelId = Integer.parseInt(apiKey);
        AmlModelDo model = getModelFromCache(modelId);

        Preconditions.checkArgument(model != null, "模型不存在");

        Preconditions.checkArgument(model.getServiceStatus() == 1, "模型服务不可以使用");


//        String callModelName = envPrefix +modelId;

        long start = System.currentTimeMillis();
        Map<String, Object> params = request.getParams();
        String infer = lakeInferService.infer(String.valueOf(modelId), params,model.getModelUrl());
        ApiResultVo apiResultVo = parseInferResult(infer);

        apiCountService.incrAml(String.valueOf(modelId),1);

        long deltaTime = System.currentTimeMillis() - start;
        add2es(String.valueOf(modelId),deltaTime,userId, ModelCallSourceEnum.PAGE_EXECUTE.getType(),JSON.toJSONString(params),JSON.toJSONString(apiResultVo));

        return apiResultVo;
    }

    private ApiResultVo parseInferResult(String infer) {
        if (StringUtils.isEmpty(infer)) {
            throw new APIException(ResultCode.AML_SERVING_ACCESS_FAIL);
        }

        JSONObject jsonObject = JSON.parseObject(infer);
        return ApiResultVo.builder()
                .status(jsonObject.getInteger("status"))
                .msg(jsonObject.getString("msg"))
                .result(jsonObject.get("result"))
                .build();
    }


    public ApiResultVo serverExecute(ApiReqVo apiReqVo) {
        Preconditions.checkArgument(apiReqVo != null, "参数不正确");
        String appId = apiReqVo.getAppId();

        String apiKey = apiReqVo.getApiKey();
        String accessToken = apiReqVo.getAccessToken();


        Preconditions.checkArgument(!StringUtils.isEmpty(appId)
                        && !StringUtils.isEmpty(apiKey)
                        && !StringUtils.isEmpty(accessToken)
                , "参数不正确");

        boolean effectToken = AccessUtil.isEffectMdToken(appId, accessToken);
        if (!effectToken){
            log.warn("aml-serving:token不正确,userId:{},modelId:{},token:{}",appId,apiKey,accessToken);
        }
        Integer userId = userService.getIdByUuidCache(appId);
        Preconditions.checkArgument(userId!=null&&userId>0,"用户不存在");

        Preconditions.checkArgument(apiKey.matches("\\d+"), "模型不存在");

        int modelId = Integer.parseInt(apiKey);
        AmlModelDo model =getModelFromCache(modelId);
        Preconditions.checkArgument(model != null, "模型不存在");

        Map<String, Object> params = apiReqVo.getParams();

//        String modelName = envPrefix+ modelId;

        long startTime = System.currentTimeMillis();
        String infer = lakeInferService.infer(String.valueOf(modelId), params,model.getModelUrl());

        long deltaTime = System.currentTimeMillis() - startTime;
        ApiResultVo apiResultVo = parseInferResult(infer);

        apiCountService.incrAml(String.valueOf(modelId),1);

        add2es(String.valueOf(modelId),deltaTime,userId, ModelCallSourceEnum.INVOKE.getType(),JSON.toJSONString(params),JSON.toJSONString(apiResultVo));
        return apiResultVo;
    }



    private void add2es(String apiKey, long deltaTime, Integer userId, Integer source, String content,
                        String apiResult) {
        EsModelCall call = new EsModelCall();
        call.setModelId(apiKey);
        call.setUserId(userId);
        call.setContent(content);
        call.setSource(source);
        call.setType( ModelCallTypeEnum.AML.getType());

        call.setResult("");
        String ip = ThreadLocalHolder.getIp();
        call.setIp(ip);
        call.setDealTime(deltaTime);

        esService.add(call);
    }

    //    @Override
    public ApiResultVo getApiDoc(ApiReqVo apiReqVo, Integer userId) {

        UserDo user = userDao.getById(userId);
        if (user == null) {
            throw new AuthorizeException(ResultCode.USER_NOT_LOGIN);
        }

        String apiKey = apiReqVo.getApiKey();

        Preconditions.checkArgument(!StringUtils.isEmpty(apiKey) && apiKey.matches("\\d+"), "模型不存在");

        int modelId = Integer.parseInt(apiKey);

        AmlModelDo amlModel = amlModelDao.getById(modelId);

        Preconditions.checkArgument(amlModel != null, "模型不存在");
        Preconditions.checkArgument(amlModel.getServiceStatus() == 1, "模型服务不可用");

        if (!user.getRoleType().equals(1)){
            Preconditions.checkArgument(amlModel.getCreateBy().equals(String.valueOf(userId)), "没有权限查看模型");
        }


        ApiResultVo apiResultVo = this.access(apiReqVo, user);


        JSONObject jsonObject = (JSONObject) apiResultVo.getResult();
        String style = amlModel.getStyle();
        String demo = amlModel.getDemo();

        String appId = user.getUuid();
        String accessToken = jsonObject.getString("accessToken");

        JSONObject result = new JSONObject();
        result.put("appId", appId);
        result.put("accessToken", accessToken);
        result.put("apiKey", apiKey);
        Map<String, Object> demoData = AmlDemoUtil.buildTextLabelApiDemo(demo);
        result.put("apiDemo", demoData);
        result.put("apiStyle", style);
        String apolloMeta = environment.getProperty("apollo.meta");
//        boolean test = StringUtils.isEmpty(apolloMeta)||apolloMeta.contains("test");

        String pre = serverUrl;

        String servingInvokeUrl = pre+"aml/invoke";
        String uri = servingInvokeUrl + "?appId=" + appId + "&apiKey=" + apiKey + "&accessToken=" + accessToken;
        result.put("apiUri", uri);

        String data = amlModel.getDemo();
        if (data == null) {
            data = "";
        } else {
            data = JSON.toJSONString(demoData);
        }

        String curl = MdUtil.getCurl(uri, data);
        ;
        result.put("curl", curl);

        String restReq = MdUtil.getRestReq(data, curl);
        result.put("mdRest", restReq);

        String pythonReq = MdUtil.getPythonReq(uri, data);
        result.put("mdPython", pythonReq);

        return ApiResultVo.builder().status(0).result(result).build();
    }


    public ApiResultVo access(ApiReqVo reqVo, UserDo user) {

        JSONObject object = new JSONObject();
        object.put("accessToken", AccessUtil.toAccessToken(user.getUuid()));
        return ApiResultVo.builder().status(0).result(object).build();
    }

    public ApiResultVo batchEvaluate(ApiReqVo apiReqVo, Integer userId) throws Exception {
        Preconditions.checkArgument(apiReqVo != null, "参数不正确");


        String apiKey = apiReqVo.getApiKey();

        Preconditions.checkArgument(!StringUtils.isEmpty(apiKey), "参数不正确");
        Map<String, Object> params = apiReqVo.getParams();
        String filePath = (String) params.getOrDefault("filePath", null);
        Preconditions.checkArgument(!StringUtils.isEmpty(filePath), "文件不存在");

        Preconditions.checkArgument(apiKey.matches("\\d+"), "模型不存在");
        //todo 校验appId与模型用户相同
        int modelId = Integer.parseInt(apiKey);
        AmlModelDo model = amlModelDao.getById(modelId);
        Preconditions.checkArgument(model != null, "模型不存在");
        Preconditions.checkArgument(model.getServiceStatus() == 1, "模型服务不可用");

        Preconditions.checkArgument(!StringUtils.isEmpty(model.getStyle()), "模型服务不可用");
        AmlModelExecutor executor = new AmlModelExecutor();

        executor.setModelUrl(model.getModelUrl());
        executor.setName(model.getName());
        executor.setApiKey(String.valueOf(modelId));
        Style apiStyle = Style.valueOf(model.getStyle());
        Preconditions.checkArgument(apiStyle != Style.UNKNOWN, "模型服务不可用");
        executor.setApiStyle(model.getStyle());
        executor.setLakeInferService(lakeInferService);

        TextEvaluator evaluator = TextEvaluator.create(executor);

        // 获取文件并评估
        long t1 = System.currentTimeMillis();
        String fileName = executor.getApiKey() + "-" + System.currentTimeMillis() + ".xlsx";
        File outputFile = new File("/tmp/aml/evaluation/result/" + fileName);
        Object pivot = null;
        if (filePath.startsWith("http")) {

            String inputPath = null;
            try {
                inputPath = ioService.writeXlsxFile(filePath);
            } catch (Exception e) {

                log.error("path:{}",filePath,e);
                throw new IllegalArgumentException("处理输入文件异常");
            }

            pivot = evaluator.evaluate(new File(inputPath), outputFile);
        } else if (filePath.startsWith("/") && new File(filePath).exists()) {
            pivot = evaluator.evaluate(new File(filePath), outputFile);
        }

        long t2 = System.currentTimeMillis();
        log.info("evaluate: execute cost {} ms", t2 - t1);
        // 上传结果文件至OSS
        String ossPath = "aml/evaluation/result/" + fileName;
        String uri = ossService.uploadXls(Files.readAllBytes(outputFile.toPath()), ossPath);

        long t3 = System.currentTimeMillis();
//        log.info("evaluate: upload cost {} ms", t3 - t2);
        // 清楚本地文件
        if (outputFile.exists()) {
            outputFile.delete();
        }

        // 构建返回结果

        JSONObject result = new JSONObject();
        result.put("pivot", pivot);

//        uri = uri.replaceAll("oss-cn-shanghai-internal", "oss-cn-shanghai");
        result.put("filePath", uri);
        result.put("style", executor.getApiStyle());
        result.put("pivot", evaluator.getResultPivot());

        return ApiResultVo.builder().status(0).result(result).build();

    }

}
