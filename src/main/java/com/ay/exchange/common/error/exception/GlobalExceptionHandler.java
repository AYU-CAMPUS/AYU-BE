package com.ay.exchange.common.error.exception;

import com.ay.exchange.common.error.dto.ErrorDto;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ErrorException.class)
    public ResponseEntity<ErrorDto> handleErrorException(
            final ErrorException e,
            final HttpServletRequest request
    ) {
        return ResponseEntity
                .status(e.getErrorMessage().getStatus())
                .body(
                        new ErrorDto(
                                e.getErrorMessage().name(),
                                e.getErrorMessage().getDescription()
                        )
                );
    }

    @ExceptionHandler(RequestRejectedException.class)
    public ResponseEntity<ErrorDto> requestRejectedError(
            final RequestRejectedException e
    ){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        new ErrorDto(e.getMessage(),e.getMessage())
                );
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorDto> handleError(
            final JwtException e
    ){
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        new ErrorDto(e.getMessage(),e.getMessage())
                );
    }

}
