package com.ProyectoFinal.BookTrack.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final RequestTimingInterceptor timing;
    @Override public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(timing).addPathPatterns("/**").excludePathPatterns("/h2/**");
    }
}
