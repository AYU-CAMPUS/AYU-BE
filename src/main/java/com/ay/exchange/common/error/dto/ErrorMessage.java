package com.ay.exchange.common.error.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorMessage {
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 존재하는 이메일 입니다."),
    DUPLICATE_USER_ID(HttpStatus.CONFLICT, "이미 존재하는 아이디 입니다."),

    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "이미 존재하는 닉네임 입니다."),
    NOT_EXIST_USER(HttpStatus.NOT_FOUND, "아이디 또는 비밀번호가 존재하지 않습니다."),
    NOT_EXISTS_USERID(HttpStatus.NOT_FOUND, "이메일이 존재하지 않습니다."),
    NOT_VALID_ROLE_ERROR(HttpStatus.FORBIDDEN, "유효하지 않은 권한입니다."),
    FAIL_VERIFICATION(HttpStatus.PRECONDITION_FAILED, "인증에 실패하였습니다"),

    NOT_FOUND_BOARD(HttpStatus.NOT_FOUND, "게시글이 존재하지 않습니다."),
    FILE_INVALID(HttpStatus.BAD_REQUEST, "파일 형식이 잘못되었습니다."),
    FAIL_WRITE_BOARD(HttpStatus.UNPROCESSABLE_ENTITY,"게시글 작성에 실패하였습니다."),
    FAIL_DELETE_BOARD(HttpStatus.UNPROCESSABLE_ENTITY,"게시글 삭제에 실패하였습니다."),

    FILE_UPLOAD_ERROR(HttpStatus.NOT_FOUND, "파일 업로드에 실패하였습니다."),
    FILE_NOT_EXISTS(HttpStatus.NOT_FOUND, "파일이 없습니다."),

    UNABLE_EXCHANGE(HttpStatus.UNPROCESSABLE_ENTITY, "교환신청에 실패하였습니다."),

    NOT_EXISTS_FILE(HttpStatus.CONFLICT, "파일이 존재하지 않거나 올바른 사용자가 아닙니다."),
    FAIL_ACCEPT_FILE(HttpStatus.UNPROCESSABLE_ENTITY, "교환 수락에 실패하였습니다."),
    FAIL_REFUSAL_FILE(HttpStatus.UNPROCESSABLE_ENTITY, "교환 거절에 실패하였습니다."),
    FAIL_UPDATE_PROFILE(HttpStatus.UNPROCESSABLE_ENTITY, "프로필 변경에 실패하였습니다."),
    FAIL_WITHDRAWAL(HttpStatus.UNPROCESSABLE_ENTITY, "회원 탈퇴에 실패하였습니다."),
    FAIL_UPDATE_USER_INFO(HttpStatus.UNPROCESSABLE_ENTITY, "정보 변경에 실패하였습니다."),

    FAIL_REPORT(HttpStatus.UNPROCESSABLE_ENTITY, "이미 신고가 접수되었거나 오류로 신고에 실패하였습니다."),

    FAIL_WRITE_COMMENT(HttpStatus.UNPROCESSABLE_ENTITY, "댓글 작성에 실패하였습니다."),
    FAIL_DELETE_COMMENT(HttpStatus.UNPROCESSABLE_ENTITY,"댓글 삭제에 실패하였습니다.");

    private final HttpStatus status;
    private final String description;
}
