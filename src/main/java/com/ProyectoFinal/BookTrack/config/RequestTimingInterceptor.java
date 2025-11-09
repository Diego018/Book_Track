package com.ProyectoFinal.BookTrack.config;

import jakarta.servlet.http.*; 
import org.slf4j.*; 
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RequestTimingInterceptor implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(RequestTimingInterceptor.class);
    private static final String START = "rt.start";

    @Override public boolean preHandle(HttpServletRequest r, HttpServletResponse s, Object h){
        r.setAttribute(START, System.nanoTime()); return true;
    }
    @Override public void afterCompletion(HttpServletRequest r, HttpServletResponse s, Object h, Exception ex){
        Object v=r.getAttribute(START);
        if(v instanceof Long start){
            long ms=(System.nanoTime()-start)/1_000_000;
            log.info("{} {} => {} ({} ms)", r.getMethod(), r.getRequestURI(), s.getStatus(), ms);
        }
    }
}
