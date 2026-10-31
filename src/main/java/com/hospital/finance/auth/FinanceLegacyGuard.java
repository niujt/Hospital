package com.hospital.finance.auth;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/** Close the old stock-write bypass and authenticate legacy clinical page families. */
@Configuration
public class FinanceLegacyGuard implements WebMvcConfigurer, HandlerInterceptor {
    private final FinanceAccess access;
    private static final Set<String> CLINICAL_PATHS = new HashSet<>(Arrays.asList(
            "seek", "seekinfo", "zation", "printseek", "medicalhistory", "seekMedicalAdvice", "drug"));
    public FinanceLegacyGuard(FinanceAccess access) { this.access = access; }
    @Override public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this).addPathPatterns("/doctor/**", "/patient/**").order(-95);
    }
    @Override public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        // Match MVC's semicolon path-parameter normalization and optional trailing slash.
        path = path.replaceAll(";[^/]*", "");
        while (path.endsWith("/") && path.length() > 1) path = path.substring(0, path.length() - 1);
        if (path.startsWith("/doctor/")) {
            String segment = path.substring("/doctor/".length()).split("/", 2)[0];
            // The one-segment GET /doctor/{department} is a lookup used by patient forms.
            if ("GET".equals(request.getMethod()) && !CLINICAL_PATHS.contains(segment)
                    && !path.substring("/doctor/".length()).contains("/")) return true;
            access.requireRoles(request.getSession(false), 2);
            if ("drug".equals(segment) && "PUT".equals(request.getMethod()))
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "旧开药直接扣库存入口已停用，请通过财务药品订单和患者购药页 /finance/patient 完成支付与出库");
        } else if (path.startsWith("/patient/")) {
            if (("/patient/search".equals(path) || "/patient/searchinfo".equals(path))
                    && "GET".equals(request.getMethod())) return true;
            access.requireRoles(request.getSession(false), 3);
        }
        return true;
    }
}
