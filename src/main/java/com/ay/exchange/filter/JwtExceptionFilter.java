package com.ay.exchange.filter;

import com.ay.exchange.common.error.dto.ErrorDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@NoArgsConstructor
public class JwtExceptionFilter extends OncePerRequestFilter{

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        try{
            chain.doFilter(request,response);
        }catch(Exception e){
            e.printStackTrace();
            setErrorResponse(HttpStatus.UNAUTHORIZED,response,e);
        }
    }

    private void setErrorResponse(HttpStatus status, HttpServletResponse response, Exception e) {
        response.setStatus(status.value());
        response.setContentType("application/json; charset=UTF-8");
        System.out.println("JwtExceptionFIlter: "+e.getMessage());
        //common.util에 리팩토링
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            response.getWriter()
                    .write(objectMapper.writeValueAsString(
                            new ErrorDto(status.getReasonPhrase(),e.getMessage())));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
