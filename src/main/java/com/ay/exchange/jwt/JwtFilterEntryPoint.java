package com.ay.exchange.jwt;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtFilterEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String exception= (String)request.getAttribute("exception");

        if(exception==null)return;
        if(exception.equals("AuthorizeError")){
            response.sendError(HttpServletResponse.SC_FORBIDDEN); //권한 리다이렉트
        }else{
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED); //인증 리다이렉트
        }
    }
}
