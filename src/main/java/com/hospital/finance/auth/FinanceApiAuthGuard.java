package com.hospital.finance.auth;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Runs before the legacy HTML-login interceptor so finance APIs fail with HTTP auth errors. */
@Configuration
public class FinanceApiAuthGuard implements WebMvcConfigurer, HandlerInterceptor {
    private final FinanceAccess access;
    public FinanceApiAuthGuard(FinanceAccess access){this.access=access;}
    @Override public void addInterceptors(InterceptorRegistry registry){registry.addInterceptor(this).addPathPatterns("/api/finance/**").order(-100);}
    @Override public boolean preHandle(HttpServletRequest request,HttpServletResponse response,Object handler){access.require(request.getSession(false));return true;}
}
