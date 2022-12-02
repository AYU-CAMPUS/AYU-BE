package com.ay.exchange.filter;

import com.ay.exchange.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final String passRegex="/user/sign-up|/user/sign-in|(/user/sign-up/confirm/verification-code)(\\?)?(email=[a-zA-z\\d-_.]*)|(/user/find-password/confirm/verification-code)(\\?)?(number=[\\d]*)";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        jwtTokenProvider.validateToken(request.getHeader("token"));
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        if (Pattern.matches(passRegex,request.getRequestURI())) {
            return true;
        }
        return false;
    }
}
