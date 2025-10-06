package com.mx.feenicia.memphis.commom.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class ControllerInterceptor  implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(ControllerInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        log.info("[{}] Request for {}", request.getMethod(), request.getRequestURI());
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler ,ModelAndView modelAndView) throws Exception {
        log.info("[{}] Request for {} with status {}", request.getMethod(), request.getRequestURI(), response.getStatus());
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }
}
