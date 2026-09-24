package ru.drmemex.classifieds.security.jwt.filter;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.drmemex.classifieds.feature.user.entity.User;
import ru.drmemex.classifieds.feature.user.model.UserRole;
import ru.drmemex.classifieds.feature.user.model.UserStatus;
import ru.drmemex.classifieds.feature.user.repository.UserRepository;
import ru.drmemex.classifieds.security.jwt.service.JwtService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_ShouldContinueFilterChain_WhenAuthorizationHeaderIsMissing()
            throws Exception {

        when(request.getHeader("Authorization"))
                .thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(
                request,
                response,
                filterChain
        );

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );

        verify(jwtService, never())
                .extractLogin(org.mockito.ArgumentMatchers.anyString());

        verify(filterChain)
                .doFilter(request, response);
    }

    @Test
    void doFilterInternal_ShouldContinueFilterChain_WhenAuthorizationHeaderIsNotBearer()
            throws Exception {

        when(request.getHeader("Authorization"))
                .thenReturn("Basic abc123");

        jwtAuthenticationFilter.doFilterInternal(
                request,
                response,
                filterChain
        );

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );

        verify(jwtService, never())
                .extractLogin(org.mockito.ArgumentMatchers.anyString());

        verify(filterChain)
                .doFilter(request, response);
    }

    @Test
    void doFilterInternal_ShouldAuthenticateUser_WhenTokenIsValidAndUserIsActive()
            throws Exception {

        User user = new User();
        user.setRole(UserRole.USER);

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer valid-token");

        when(jwtService.extractLogin("valid-token"))
                .thenReturn("user");

        when(userRepository.findByLoginAndStatus(
                "user",
                UserStatus.ACTIVE
        )).thenReturn(Optional.of(user));

        jwtAuthenticationFilter.doFilterInternal(
                request,
                response,
                filterChain
        );

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        assertEquals(
                "user",
                authentication.getName()
        );

        assertEquals(
                "ROLE_USER",
                authentication.getAuthorities()
                        .iterator()
                        .next()
                        .getAuthority()
        );

        verify(userRepository)
                .findByLoginAndStatus(
                        "user",
                        UserStatus.ACTIVE
                );

        verify(filterChain)
                .doFilter(request, response);
    }

    @Test
    void doFilterInternal_ShouldNotAuthenticate_WhenActiveUserNotFound()
            throws Exception {

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer valid-token");

        when(jwtService.extractLogin("valid-token"))
                .thenReturn("user");

        when(userRepository.findByLoginAndStatus(
                "user",
                UserStatus.ACTIVE
        )).thenReturn(Optional.empty());

        jwtAuthenticationFilter.doFilterInternal(
                request,
                response,
                filterChain
        );

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );

        verify(filterChain)
                .doFilter(request, response);
    }

    @Test
    void doFilterInternal_ShouldKeepExistingAuthentication_WhenAlreadyAuthenticated()
            throws Exception {

        Authentication existingAuthentication =
                org.mockito.Mockito.mock(Authentication.class);

        SecurityContextHolder
                .getContext()
                .setAuthentication(existingAuthentication);

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer valid-token");

        when(jwtService.extractLogin("valid-token"))
                .thenReturn("user");

        jwtAuthenticationFilter.doFilterInternal(
                request,
                response,
                filterChain
        );

        assertSame(
                existingAuthentication,
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );

        verify(userRepository, never())
                .findByLoginAndStatus(
                        org.mockito.ArgumentMatchers.anyString(),
                        org.mockito.ArgumentMatchers.any(UserStatus.class)
                );

        verify(filterChain)
                .doFilter(request, response);
    }

    @Test
    void doFilterInternal_ShouldContinueFilterChain_WhenTokenIsInvalid()
            throws Exception {

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer invalid-token");

        when(jwtService.extractLogin("invalid-token"))
                .thenThrow(
                        new JwtException("Invalid token")
                );

        jwtAuthenticationFilter.doFilterInternal(
                request,
                response,
                filterChain
        );

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );

        verify(userRepository, never())
                .findByLoginAndStatus(
                        org.mockito.ArgumentMatchers.anyString(),
                        org.mockito.ArgumentMatchers.any(UserStatus.class)
                );

        verify(filterChain)
                .doFilter(request, response);
    }
}