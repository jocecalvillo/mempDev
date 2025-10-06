package com.mx.feenicia.memphis.commom.config;

import com.mx.feenicia.memphis.commom.interceptor.ControllerInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;



@Configuration
public class ServiceConfig implements WebMvcConfigurer {

    private final ControllerInterceptor controllerInterceptor;


    public ServiceConfig(ControllerInterceptor controllerInterceptor) {
        this.controllerInterceptor = controllerInterceptor;
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(controllerInterceptor);
    }

}
