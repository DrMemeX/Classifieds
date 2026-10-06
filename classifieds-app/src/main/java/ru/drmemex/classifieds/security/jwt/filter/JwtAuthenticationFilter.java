package ru.drmemex.classifieds.security.jwt.filter;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.drmemex.classifieds.feature.user.model.UserStatus;
import ru.drmemex.classifieds.feature.user.repository.UserRepository;
import ru.drmemex.classifieds.security.jwt.service.JwtService;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            String login = jwtService.extractLogin(token);

            if (SecurityContextHolder.getContext().getAuthentication() == null) {

                userRepository.findByLoginAndStatus(login, UserStatus.ACTIVE)
                        .ifPresent(user -> {

                            UsernamePasswordAuthenticationToken authenticationToken =
                                    new UsernamePasswordAuthenticationToken(
                                            login,
                                            null,
                                            List.of(
                                                    new SimpleGrantedAuthority(
                                                    "ROLE_" + user.getRole().name()
                                                    )
                                            )
                                    );
                            SecurityContextHolder.getContext()
                                    .setAuthentication(authenticationToken);
                        });
            }
        } catch (JwtException exception) {
            // Invalid JWT — запрос остается неавторизованным
        }
        filterChain.doFilter(request, response);
    }
}
