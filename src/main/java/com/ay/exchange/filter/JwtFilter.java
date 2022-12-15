package com.ay.exchange.filter;

import com.ay.exchange.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private static final Set<String> passUri = new HashSet<>(List.of("/user/existence-nickname", "/management/request-board", "/oauth2/temp/login", "/management/request-board/approval"));
    private static final String regexUri = "/board/content/\\d+|/board/\\d+|/comment/\\d+";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("JWTFILTER");
        jwtTokenProvider.validateToken(request.getHeader("token"));
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        System.out.println(request.getRequestURI());
        if (passUri.contains(request.getRequestURI())) {
            return true;
        }
        if (Pattern.matches(regexUri, request.getRequestURI())) {
            return true;
        }
        return false;
    }
}
