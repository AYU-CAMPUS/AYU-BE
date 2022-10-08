package com.ay.exchange.common.error.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorMessage {
    NOT_EXIST_USER(HttpStatus.NOT_FOUND,"아이디 또는 비밀번호가 존재하지 않습니다"),
    NOT_EXISTS_USERID(HttpStatus.NOT_FOUND,"이메일이 존재하지 않습니다."),
    NOT_VALID_ROLE_ERROR(HttpStatus.FORBIDDEN, "유효하지 않은 권한입니다."),
    NOT_FOUND_BOARD(HttpStatus.NOT_FOUND, "게시글이 존재하지 않습니다." );

    private final HttpStatus status;
    private final String description;
}
