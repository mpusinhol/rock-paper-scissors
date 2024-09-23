package com.mpusinhol.rockpaperscissors.controller.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpusinhol.rockpaperscissors.model.dto.ExceptionDTO;
import com.mpusinhol.rockpaperscissors.service.impl.JWTServiceImpl;
import com.mpusinhol.rockpaperscissors.service.impl.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
@Component
public class AuthenticationFilter extends OncePerRequestFilter {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String ANONYMOUS_USER = "anonymousUser";

    private final JWTServiceImpl jwtService;
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader(AUTHORIZATION_HEADER);

        //No token means guest user
        if (isNull(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!jwtService.isTokenValid(token)) {
            ExceptionDTO exceptionDTO =
                    new ExceptionDTO(HttpStatus.UNAUTHORIZED.value(), Instant.now(), "Invalid token");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(objectMapper.writeValueAsString(exceptionDTO));
        }

        String username = jwtService.getUsername(token);
        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);
        SecurityContextHolder
                .getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()));
        filterChain.doFilter(request, response);
    }
}
