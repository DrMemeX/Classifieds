package ru.drmemex.classifieds.common.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HttpLoggingFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private final HttpLoggingFilter httpLoggingFilter =
            new HttpLoggingFilter();

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void doFilterInternal_ShouldSetAndRemoveRequestId()
            throws ServletException, IOException {

        doAnswer(invocation -> {

            assertNotNull(
                    MDC.get("requestId")
            );

            return null;
        }).when(filterChain)
                .doFilter(request, response);

        httpLoggingFilter.doFilterInternal(
                request,
                response,
                filterChain
        );

        assertNull(
                MDC.get("requestId")
        );

        verify(filterChain)
                .doFilter(request, response);
    }

    @Test
    void doFilterInternal_ShouldRemoveRequestId_WhenFilterChainThrowsException()
            throws ServletException, IOException {

        doThrow(
                new ServletException("Test exception")
        ).when(filterChain)
                .doFilter(request, response);

        assertThrows(
                ServletException.class,
                () -> httpLoggingFilter.doFilterInternal(
                        request,
                        response,
                        filterChain
                )
        );

        assertNull(
                MDC.get("requestId")
        );

        verify(filterChain)
                .doFilter(request, response);
    }
}