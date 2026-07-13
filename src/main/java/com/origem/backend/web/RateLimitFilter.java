package com.origem.backend.web;

import com.origem.backend.config.AppProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.origem.backend.dto.ApiError;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Limite simples de requisições por IP, em memória (janela fixa de 1 minuto).
 * Aplica-se apenas a POST /api/signups — o endpoint público sem exigência de token,
 * mais suscetível a spam. Os demais endpoints já exigem um token de acesso válido.
 */
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final long WINDOW_MILLIS = 60_000;

    private final int maxRequestsPerWindow;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, Window> windows = new ConcurrentHashMap<>();

    public RateLimitFilter(AppProperties appProperties) {
        this.maxRequestsPerWindow = appProperties.rateLimit().signupsPerMinute();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!isSignupCreation(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        Window window = windows.computeIfAbsent(request.getRemoteAddr(), key -> new Window());

        if (window.registerAndCheckLimit(maxRequestsPerWindow)) {
            filterChain.doFilter(request, response);
            return;
        }

        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(
                ApiError.of("Muitas tentativas. Aguarde um minuto e tente novamente.")));
    }

    private boolean isSignupCreation(HttpServletRequest request) {
        return "POST".equalsIgnoreCase(request.getMethod()) && "/api/signups".equals(request.getRequestURI());
    }

    private static final class Window {
        private volatile long startMillis = System.currentTimeMillis();
        private final AtomicInteger count = new AtomicInteger(0);

        synchronized boolean registerAndCheckLimit(int max) {
            long now = System.currentTimeMillis();
            if (now - startMillis > WINDOW_MILLIS) {
                startMillis = now;
                count.set(0);
            }
            return count.incrementAndGet() <= max;
        }
    }
}
