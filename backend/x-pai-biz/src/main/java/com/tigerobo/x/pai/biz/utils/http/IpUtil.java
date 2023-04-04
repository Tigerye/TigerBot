package com.tigerobo.x.pai.biz.utils.http;

import com.google.common.collect.Lists;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class IpUtil {


    private final static List<String> IP_HEAD_LIST = Lists.newArrayList("X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "X-Real-IP");

    public static String getIp(HttpServletRequest request) {
        for (String ipHead : IP_HEAD_LIST) {
            String ip = request.getHeader(ipHead);
            if (!org.springframework.util.StringUtils.isEmpty(ip) && "unknown".equalsIgnoreCase(ip))
                return request.getHeader(ipHead).split(",")[0];
        }
        return "0:0:0:0:0:0:0:1".equals(request.getRemoteAddr()) ? "127.0.0.1" : request.getRemoteAddr();
    }

}
