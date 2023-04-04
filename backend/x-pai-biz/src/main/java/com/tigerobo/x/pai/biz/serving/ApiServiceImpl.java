package com.tigerobo.x.pai.biz.serving;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.tigerobo.x.pai.api.auth.service.UserService;
import com.tigerobo.x.pai.api.dto.ApiDto;
import com.tigerobo.x.pai.api.enums.ModelCallSourceEnum;
import com.tigerobo.x.pai.api.enums.ModelCallTypeEnum;
import com.tigerobo.x.pai.api.enums.Style;
import com.tigerobo.x.pai.api.es.EsModelCall;
import com.tigerobo.x.pai.api.exception.APIException;
import com.tigerobo.x.pai.api.exception.AuthorizeException;
import com.tigerobo.x.pai.api.exception.ResultCode;
import com.tigerobo.x.pai.api.serving.service.ApiService;
import com.tigerobo.x.pai.api.serving.vo.ApiReqVo;
import com.tigerobo.x.pai.api.serving.vo.ApiResultVo;
import com.tigerobo.x.pai.api.utils.MapUtils;
import com.tigerobo.x.pai.api.vo.api.AgreementVo;
import com.tigerobo.x.pai.biz.base.EnvService;
import com.tigerobo.x.pai.biz.biz.service.ModelCallLogService;
import com.tigerobo.x.pai.biz.cache.RedisCacheService;
import com.tigerobo.x.pai.biz.cache.biz.MachineUtil;
import com.tigerobo.x.pai.biz.converter.APIConvert;
import com.tigerobo.x.pai.biz.data.es.EsService;
import com.tigerobo.x.pai.biz.pay.api.ApiAgreementService;
import com.tigerobo.x.pai.biz.pay.api.ApiBalanceCardService;
import com.tigerobo.x.pai.biz.pay.api.ApiUserConfigService;
import com.tigerobo.x.pai.biz.utils.*;
import com.tigerobo.x.pai.dal.auth.dao.UserDao;
import com.tigerobo.x.pai.dal.auth.entity.UserDo;
import com.tigerobo.x.pai.dal.pay.entity.ApiBalanceCardPo;
import lombok.extern.slf4j.Slf4j;
import java.lang.IllegalArgumentException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description:
 * @modified By:
 * @version: $
 */
@Slf4j
@Component
@Lazy
public class ApiServiceImpl implements ApiService {

    @Value("${xpai.serving.invoke-url:}")
    private String servingInvokeUrl;

    @Autowired
    private ExecutorFactory executorFactory;

    @Autowired
    private UserDao userDao;


    @Autowired
    private UserService userService;

    @Autowired
    private ModelCallLogService modelCallLogService;

    @Autowired
    private EsService esService;

    @Autowired
    private RedisCacheService redisCacheService;


    @Autowired
    private ApiCountService apiCountService;

    @Autowired
    private MachineUtil machineUtil;

    @Autowired
    private EnvService envService;

    @Autowired
    private ApiUserConfigService apiUserConfigService;

    @Autowired
    private ApiAgreementService apiAgreementService;

    @Autowired
    private ApiBalanceCardService apiBalanceCardService;

    @Value("${pai.api.cache.switch:false}")
    boolean supportCache;
    @Override
    public ApiResultVo execute(ApiReqVo request) {
        ApiResultVo apiResultVo = null;

        Executable executable = executorFactory.get(request.getApiKey());

        boolean supportCacheResult = executable.supportCacheResult();

        if (supportCache&&supportCacheResult) {

            String uniKey = "api:model:" + Md5Util.getMd5(request.getApiKey() + JSON.toJSONString(request.getParams()));
            try {
//            ApiResultVo ifPresent = localCache.getIfPresent(uniKey);
                String s = redisCacheService.get(uniKey);

                if (!StringUtils.isEmpty(s)) {
                    apiResultVo = JSON.parseObject(s, ApiResultVo.class);
                }
            } catch (Exception ex) {
                log.error("", ex);
            }

        }

        if (apiResultVo == null) {
            apiResultVo = executeInner(request);
            if (supportCache&&supportCacheResult && apiResultVo.getStatus() != null && (apiResultVo.getStatus().equals(200) || apiResultVo.getStatus().equals(0)) && apiResultVo.getResult() != null) {
                try {
                    String uniKey = "api:model:" + Md5Util.getMd5(request.getApiKey() + JSON.toJSONString(request.getParams()));
                    redisCacheService.set(uniKey, JSON.toJSONString(apiResultVo), 60 * 2);
                } catch (Exception ex) {
                    log.error("", ex);
                }
            }else {

            }
        } else {
            Integer userId = ThreadLocalHolder.getUserId();
            if (userId == null) {
                userId = 0;
            }
            String apiResult = getResultStr(apiResultVo.getResult());
            Long reqId = add2es(request.getApiKey(), 0, userId, ModelCallSourceEnum.PAGE_EXECUTE.getType()
                    , JSON.toJSONString(request.getParams()), apiResult);
            apiResultVo.setReqId(reqId);
        }
        apiCountService.incrApiCall(request);
        return apiResultVo;
    }

    @Override
    public ApiResultVo executeServing(ApiReqVo request) {

        String apiKey = request.getApiKey();
        String appId = request.getAppId();
        String accessToken = request.getAccessToken();
        Preconditions.checkArgument(org.apache.commons.lang3.StringUtils.isNotBlank(apiKey), "apiKey为空");
        Preconditions.checkArgument(org.apache.commons.lang3.StringUtils.isNotBlank(appId), "appId参数为空");
        Preconditions.checkArgument(org.apache.commons.lang3.StringUtils.isNotBlank(accessToken)&&accessToken.length()>11, "token没有权限");
        Integer userId = userService.getIdByUuidCache(request.getAppId());

        Preconditions.checkArgument(userId != null && userId > 0, "用户不存在");
        boolean effectToken = AccessUtil.isEffectMdToken(appId, accessToken);

        final String warnMsg = apiUserConfigService.checkApi(userId,apiKey);
        request.setUserId(userId);
        if (!effectToken) {
            log.error("api-serving:token不正确-userId:{},token:{}", appId, accessToken);
        }

        ApiResultVo apiResultVo = executeInner(request);
        apiCountService.incrApiCall(request);

        if (apiResultVo!=null&&StringUtils.isNotBlank(warnMsg)){
            apiResultVo.setMsg(warnMsg);
        }
        return apiResultVo;
    }

    public ApiResultVo executeInner(ApiReqVo request) {
        long reqId = IdGenerator.getId(machineUtil.getMachineId());

        String apiKey = request.getApiKey();
        Executable executable = executorFactory.get(apiKey);
        long start = System.currentTimeMillis();
        Object object = executeApi(request,request.getParams(), executable, reqId);
        if (object == null) {
            throw new APIException(ResultCode.FAILED);
        }

        long deltaTime = System.currentTimeMillis() - start;

        JSONObject jsonObject = (JSONObject) object;
        Object result = jsonObject.get("result");
        if (result == null) {
            JSONObject data = jsonObject.getJSONObject("data");
            if (data != null) {
                result = data.get("result");
            }
        }
        if (result == null) {
            result = object;
        }

        final JSONObject appendInfo = jsonObject.getJSONObject("appendInfo");

        Integer userId = request.getUserId();
        Integer source = request.getSource();
        String content = getLogString(request);
        fixScoreLength(result);

        String apiResult = getResultStr(result);
        modelCallLogService.add(userId, apiKey, content, source, apiResult);

        add2es(apiKey, deltaTime, userId, source, content, apiResult);

        return ApiResultVo.builder()
                .status(jsonObject.getInteger("status"))
                .msg(jsonObject.getString("msg"))
                .result(result)
                .reqId(reqId)
                .appendInfo(appendInfo)
                .build();
    }


    private String getResultStr(Object result) {
        String apiResult = "";
        try {
            if (result instanceof String) {
                apiResult = result.toString();
            } else {
                apiResult = JSON.toJSONString(result);
            }
        } catch (Exception ex) {
            log.error("apiResult-parseJSON:", ex);
        }
        return apiResult;
    }

    private Long add2es(String apiKey, long deltaTime, Integer userId, Integer source, String content, String apiResult){
        return add2es(apiKey,deltaTime,userId,source,content,apiResult,null);
    }
    public Long add2es(String apiKey, long deltaTime, Integer userId, Integer source, String content, String apiResult,String thirdId) {

        if (userId == null) {
            userId = 0;
        }
        EsModelCall call = new EsModelCall();
        call.setModelId(apiKey);
        call.setUserId(userId);
        call.setContent(content);
        call.setSource(source);
        call.setType(ModelCallTypeEnum.APP.getType());

        if (apiResult == null) {
            apiResult = "";
        }
//        call.setResult(apiResult);
        String ip = ThreadLocalHolder.getIp();
        call.setIp(ip);
        call.setDealTime(deltaTime);
        call.setBizId(thirdId);

        esService.add(call);

        return call.getId();
    }

    private Object executeApi(ApiReqVo request,Map<String, Object> params, Executable executor, long reqId) {
        if (executor == null) {
            throw new IllegalArgumentException("模型api不存在");
        }

        Style style = Style.getByValue(executor.getApiStyle());
        if (style.isUseDefaultProcess() && params.containsKey("text") && !params.containsKey("text_list")) {
            String text = MapUtils.getString(params, "text", null);
            params.put("text_list", Lists.newArrayList(text));
            JSONObject object = (JSONObject) executor.execute(params,reqId,request);

            Short status = object.getShort("status");
            if (status != null) {
                if (status == 0) {
                    JSONArray array = object.getJSONArray("result");
                    JSONObject rs = new JSONObject();
                    rs.put("status", 0);

                    rs.put("result", array.size() > 0 ? array.get(0) : Lists.newArrayList());
                    return rs;
                } else {
                    return object;
                }
            } else {
                Short code = object.getShort("code");
                JSONObject data = new JSONObject();
                if (code != null && code == 200) {
                    data.put("status", code);
                    JSONObject inData = object.getJSONObject("data");
                    if (inData != null) {
                        Object result = inData.get("result");
                        data.put("result", result);
                    }
                    data.put("msg", object.get("msg"));
                } else {
                    data.put("status", -1);
                    data.put("msg", object.get("msg"));

                }
                return data;
            }
        } else if (style == Style.ART_IMAGE) {
            return executor.execute(params, reqId,request);
        } else {
            return executor.execute(params, reqId,request);
        }

    }

    private String getLogString(ApiReqVo request) {
        String content = "";
        Map<String, Object> params = request.getParams();
        if (!CollectionUtils.isEmpty(params)) {
            Object text_list = params.get("text_list");
            if (text_list != null) {
                if (text_list instanceof ArrayList) {
                    ArrayList<String> textList = (ArrayList) text_list;
                    if (textList.size() > 0) {
                        content = textList.get(0);
                    }
                }
            }
            if (StringUtils.isEmpty(content)) {
                content = JSON.toJSONString(params);
            }
        }
        return content;
    }

    private void fixScoreLength(Object result) {
        if (result == null) {
            return;
        }
        if (!(result instanceof JSONArray)) {
            return;
        }
        JSONArray resultArray = (JSONArray) result;
        if (resultArray.size() <= 0) {
            return;
        }

        Object o = resultArray.get(0);
        if (!(o instanceof JSONArray)) {
            return;
        }
        JSONArray jsA = (JSONArray) o;
        if (jsA.size() <= 0) {
            return;
        }
        for (int i = 0; i < jsA.size(); i++) {
            Object so = jsA.get(i);
            if (so instanceof JSONObject) {
                JSONObject jso = (JSONObject) so;
                Object score = jso.get("score");
                if (score != null && score instanceof BigDecimal) {
                    BigDecimal val = (BigDecimal) score;
                    val = val.add(new BigDecimal("0.0000000000001"));
                    val = val.setScale(5, RoundingMode.HALF_UP);
                    jso.put("score", val);
                }
            } else {
                break;
            }
        }
    }


    @Override
    public ApiResultVo getApiDoc(ApiReqVo apiReqVo) {
        Integer userId = ThreadLocalHolder.getUserId();

        UserDo userDo = userDao.getById(userId);
        if (userDo == null) {
            throw new AuthorizeException(ResultCode.USER_NOT_LOGIN);
        }
        String userUUid = userDo.getUuid();
        String apiKey = apiReqVo.getApiKey();

        String appId = userUUid;
        String accessToken = AccessUtil.toAccessToken(userUUid);

        JSONObject result = new JSONObject();
        result.put("appId", appId);
        result.put("accessToken", accessToken);
        result.put("apiKey", apiKey);

        Executable api = executorFactory.get(apiReqVo.getApiKey());
        if (api == null) {
            throw new IllegalArgumentException("模型不存在");
        }
        if (apiKey.equalsIgnoreCase(envService.getSearchApiUuid())){
            return getSearchApiDoc(apiReqVo);
        }
        return getObject(userId,apiKey, appId, accessToken, result, api);
    }

    private ApiResultVo getObject(Integer userId,String apiKey, String appId, String accessToken, JSONObject result, Executable api) {
        final ApiDto apiDto = api.getApiDto();
        Map<String, Object> demo = apiDto.getDemo();
//        result.put("apiDemo", demo);
//        result.put("apiStyle", api.getApiStyle());
        String preservingInvokeUrl = servingInvokeUrl + "invoke";
        String uri = preservingInvokeUrl + "" + "?appId=" + appId + "&apiKey=" + apiKey + "&accessToken=" + accessToken;
//        result.put("apiUri", uri);

        String data = JSON.toJSONString(demo);

        String curl = MdUtil.getCurl(uri, data);
//        result.put("curl", curl);

        String restReq = MdUtil.getRestReq(data, curl);
        result.put("mdRest", restReq);

        String pythonReq = MdUtil.getPythonReq(uri, data);
        result.put("mdPython", pythonReq);

        initCall(userId,result, apiDto);

        return ApiResultVo.builder().status(0).result(result).build();
    }

    private void initCall(Integer userId,JSONObject result, ApiDto apiDto) {
        final Integer originCallCount = apiDto.getOriginCallCount();

        if (originCallCount!=null&&originCallCount>0){
            final ApiBalanceCardPo userModelRemainNum = apiBalanceCardService.getUserModelRemainNum(userId, apiDto.getModelUuid(), originCallCount.longValue());
            if (userModelRemainNum!=null){
                final String countString = APIConvert.getCountString(userModelRemainNum.getTotalNum().intValue());
                result.put("originCallCountValue", countString);

                final String remainCallNumValue = APIConvert.getCountString(userModelRemainNum.getRemainNum().intValue());
                result.put("remainCallNumValue", remainCallNumValue);

            }
            final AgreementVo userAgreement = apiAgreementService.getUserAgreement(userId, apiDto.getUuid());
            if (userAgreement!=null){
                result.put("agreementId",userAgreement.getId());
            }
        }
    }

    private ApiResultVo getSearchApiDoc(ApiReqVo apiReqVo) {
        Integer userId = ThreadLocalHolder.getUserId();
        final Integer indexId = apiReqVo.getSearchIndexId();
        if (indexId ==null){
            return ApiResultVo.builder().status(0).result(new HashMap<>(0)).build();
        }


        UserDo userDo = userDao.getById(userId);
        if (userDo == null) {
            throw new AuthorizeException(ResultCode.USER_NOT_LOGIN);
        }
        String userUUid = userDo.getUuid();
        String apiKey = apiReqVo.getApiKey();

        String appId = userUUid;
        String accessToken = AccessUtil.toAccessToken(userUUid);

        JSONObject result = new JSONObject();
        result.put("appId", appId);
        result.put("accessToken", accessToken);
        result.put("apiKey", apiKey);

        Map<String,Object> demo = new LinkedHashMap<>();
        demo.put("query","");
        demo.put("pageNo",1);
        demo.put("pageSize",10);
//        result.put("apiDemo", demo);
//        result.put("apiStyle", api.getApiStyle());
        String preservingInvokeUrl = servingInvokeUrl + "indexApi/search";
        String uri = preservingInvokeUrl + "" + "?appId=" + appId+ "&accessToken=" + accessToken + "&indexId=" + indexId ;
//        result.put("apiUri", uri);

        String data = JSON.toJSONString(demo);

        String curl = MdUtil.getCurl(uri, data);
//        result.put("curl", curl);

        String restReq = MdUtil.getRestReq(data, curl);
        result.put("mdRest", restReq);

        String pythonReq = MdUtil.getPythonReq(uri, data);
        result.put("mdPython", pythonReq);
//
//        result.put("originCallCountValue", APIConvert.getCountString(apiDto.getOriginCallCount()));
//        result.put("originCallCountValue", APIConvert.getCountString(apiDto.getOriginCallCount()));
        return ApiResultVo.builder().status(0).result(result).build();
    }
}
