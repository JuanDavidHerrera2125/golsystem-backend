package com.GolsystemV2.Backend.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RequestLoggingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String method = httpRequest.getMethod();
        String uri = httpRequest.getRequestURI();
        String queryString = httpRequest.getQueryString();
        String origin = httpRequest.getHeader("Origin");

        logger.info("[REQUEST-LOG] {} {} | Query: {} | Origin: {}",
                method, uri, queryString, origin);

        long startTime = System.currentTimeMillis();

        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            logger.error("[REQUEST-LOG] Exception procesando request: {}", e.getMessage(), e);
            throw e;
        }

        long duration = System.currentTimeMillis() - startTime;
        int status = httpResponse.getStatus();

        logger.info("[REQUEST-LOG] {} {} | Status: {} | Duration: {}ms",
                method, uri, status, duration);
    }
}
