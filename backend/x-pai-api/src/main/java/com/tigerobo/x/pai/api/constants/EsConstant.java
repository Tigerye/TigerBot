package com.tigerobo.x.pai.api.constants;

public class EsConstant {
    public static String ES_HOST = "ms-es-logs-1.aigauss.com";

    public static final Integer ES_PORT = 9200;

    public static final String ES_HOST_URL = "http://"+ES_HOST+":"+ES_PORT+"/";
    public static final String INDEX_MODEL_CALL_ALIAS = "pai_model_call";

    public static final String INDEX_MODEL_CALL_INDEX = "pai_model_call_";

    public static final String TEST_INDEX_MODEL_CALL_ALIAS = "test_pai_model_call";
    public static final String TEST_INDEX_MODEL_CALL_INDEX = "test_pai_model_call_";

    public static final String INDEX_TYPE = "type";

    public static final String INDEX_USER_ACCESS_INDEX = "pai_user_access";
}
