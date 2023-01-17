package com.ay.exchange.common.error.exception;


import com.ay.exchange.common.error.dto.ErrorDto;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.validation.ConstraintViolationException;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ErrorException.class)
    public ResponseEntity<ErrorDto> handleErrorException(
            final ErrorException e
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

    @ExceptionHandler({ConstraintViolationException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorDto> handleValidateException(
            final Exception e
    ) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        new ErrorDto(HttpStatus.BAD_REQUEST.name(), "잘못된 형식의 입력입니다.")
                );
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorDto> handleJwtException(
            final JwtException e
    ) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        new ErrorDto(e.getMessage(), e.getMessage())
                );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorDto> handle405Error(
            final HttpRequestMethodNotSupportedException e
    ) {
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(
                        new ErrorDto(e.getMessage(), "잘못된 메소드 요청입니다.")
                );
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorDto> handleMaxUpLoadSizeEexception(
            final MaxUploadSizeExceededException e
    ) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        new ErrorDto(e.getMessage(), "파일 용량이 초과되었습니다.")
                );
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorDto> handleJwtEx(
//            final Exception e
//    ){
//        System.out.println(e);
//        return ResponseEntity
//                .status(HttpStatus.UNAUTHORIZED)
//                .body(
//                        new ErrorDto(e.getMessage(),e.getMessage())
//                );
//    }
}
