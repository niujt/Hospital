package com.hospital.finance.auth;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Protect legacy account-changing admin routes as well as new finance administration. */
@Configuration
public class FinanceAdminGuard implements WebMvcConfigurer, HandlerInterceptor {
    private final FinanceAccess access;
    public FinanceAdminGuard(FinanceAccess access) { this.access = access; }
    @Override public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this).addPathPatterns("/admin/**").order(-100);
    }
    @Override public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        access.requireRoles(request.getSession(false), 1);
        return true;
    }
}
