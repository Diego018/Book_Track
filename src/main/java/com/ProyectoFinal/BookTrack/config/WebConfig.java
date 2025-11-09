package com.ProyectoFinal.BookTrack.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final RequestTimingInterceptor timing;
    @Override public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(timing).addPathPatterns("/**");
    }
}
