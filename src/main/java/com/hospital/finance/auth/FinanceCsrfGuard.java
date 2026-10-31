package com.hospital.finance.auth;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

@Configuration
public class FinanceCsrfGuard implements WebMvcConfigurer, HandlerInterceptor {
    public static final String SESSION_KEY = "financeCsrfToken";
    private static final SecureRandom RANDOM = new SecureRandom();
    public static String token(HttpSession session) {
        synchronized (session) {
            String current = (String) session.getAttribute(SESSION_KEY);
            if (current == null) {
                byte[] bytes = new byte[32]; RANDOM.nextBytes(bytes);
                current = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
                session.setAttribute(SESSION_KEY, current);
            }
            return current;
        }
    }
    @Override public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this).addPathPatterns("/api/finance/**").order(-90);
    }
    @Override public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String method = request.getMethod();
        if ("GET".equals(method) || "HEAD".equals(method) || "OPTIONS".equals(method)) return true;
        String origin = request.getHeader("Origin");
        if (origin != null && !sameOrigin(origin, request)) deny();
        HttpSession session = request.getSession(false);
        Object stored = session == null ? null : session.getAttribute(SESSION_KEY);
        String supplied = request.getHeader("X-CSRF-Token");
        if (!(stored instanceof String) || supplied == null || !MessageDigest.isEqual(
                ((String) stored).getBytes(StandardCharsets.UTF_8), supplied.getBytes(StandardCharsets.UTF_8))) deny();
        return true;
    }
    private boolean sameOrigin(String origin, HttpServletRequest request) {
        try {
            URI uri = URI.create(origin);
            int port = uri.getPort() == -1 ? ("https".equalsIgnoreCase(uri.getScheme()) ? 443 : 80) : uri.getPort();
            return uri.getUserInfo() == null && uri.getHost() != null
                    && uri.getScheme().equalsIgnoreCase(request.getScheme())
                    && uri.getHost().equalsIgnoreCase(request.getServerName()) && port == request.getServerPort()
                    && (uri.getPath() == null || uri.getPath().isEmpty()) && uri.getQuery() == null && uri.getFragment() == null;
        } catch (IllegalArgumentException ex) { return false; }
    }
    private void deny() { throw new ResponseStatusException(HttpStatus.FORBIDDEN, "请求安全校验失败，请刷新页面重试"); }
}
