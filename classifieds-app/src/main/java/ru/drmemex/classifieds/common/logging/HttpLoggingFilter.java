package ru.drmemex.classifieds.common.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class HttpLoggingFilter extends OncePerRequestFilter {

    private static final String REQUEST_ID = "requestId";

    @Override
    protected void doFilterInternal(
            @NonNull
            HttpServletRequest request,

            @NonNull
            HttpServletResponse response,

            @NonNull
            FilterChain filterChain
    ) throws ServletException, IOException {

        String requestId = UUID.randomUUID().toString();

        MDC.put(
                REQUEST_ID,
                requestId
        );

        long startTime = System.currentTimeMillis();

        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;

            log.info(
                    "HTTP {} {} -> {} ({} ms)",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    duration
            );

            MDC.remove(REQUEST_ID);
        }
    }
}
