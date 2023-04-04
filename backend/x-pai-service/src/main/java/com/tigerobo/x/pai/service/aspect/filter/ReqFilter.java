package com.tigerobo.x.pai.service.aspect.filter;

import com.tigerobo.x.pai.service.aspect.wrapper.CustomHttpRequestWrapper;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;


@Component
public class ReqFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        ServletRequest request = null;

        final String requestURI = httpRequest.getRequestURI();

        boolean wrapper = false;
        if (requestURI!=null&&requestURI.endsWith("upload")){

        }else {
            wrapper = Arrays.asList("POST").contains(httpRequest.getMethod());
        }

        if(wrapper) {
            request = new CustomHttpRequestWrapper(httpRequest);
        }else {
            request = httpRequest;
        }
        filterChain.doFilter(request, servletResponse);
    }
}
