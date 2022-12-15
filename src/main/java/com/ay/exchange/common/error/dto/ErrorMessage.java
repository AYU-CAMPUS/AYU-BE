package com.ay.exchange.common.error.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorMessage {
    NOT_USER(HttpStatus.NOT_FOUND,"안양대학교 웹메일만 로그인이 가능합니다."),

    NOT_FOUND_BOARD(HttpStatus.NOT_FOUND, "게시글이 존재하지 않습니다."),
    FILE_INVALID(HttpStatus.BAD_REQUEST, "파일 형식이 잘못되었습니다."),
    FAIL_WRITE_BOARD(HttpStatus.UNPROCESSABLE_ENTITY,"게시글 작성에 실패하였습니다."),
    FAIL_DELETE_BOARD(HttpStatus.UNPROCESSABLE_ENTITY,"게시글 삭제에 실패하였습니다."),
    FAIL_MODIFY_BOARD(HttpStatus.UNPROCESSABLE_ENTITY,"최근 교환일이 3일이 경과되거나 교환요청이 없는 경우 수정이 가능해요."),

    FILE_UPLOAD_ERROR(HttpStatus.NOT_FOUND, "파일 업로드에 실패하였습니다."),
    FILE_NOT_EXISTS(HttpStatus.NOT_FOUND, "파일이 없습니다."),

    UNABLE_EXCHANGE(HttpStatus.UNPROCESSABLE_ENTITY, "교환신청에 실패하였습니다."),

    NOT_EXISTS_FILE(HttpStatus.CONFLICT, "파일이 존재하지 않거나 올바른 사용자가 아닙니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "중복된 닉네임 입니다."),
    FAIL_ACCEPT_FILE(HttpStatus.UNPROCESSABLE_ENTITY, "교환 수락에 실패하였습니다."),
    FAIL_REFUSAL_FILE(HttpStatus.UNPROCESSABLE_ENTITY, "교환 거절에 실패하였습니다."),
    FAIL_UPDATE_PROFILE(HttpStatus.UNPROCESSABLE_ENTITY, "프로필 변경에 실패하였습니다."),
    FAIL_WITHDRAWAL(HttpStatus.UNPROCESSABLE_ENTITY, "회원 탈퇴에 실패하였습니다."),
    FAIL_UPDATE_USER_INFO(HttpStatus.UNPROCESSABLE_ENTITY, "정보 변경에 실패하였습니다."),

    FAIL_REPORT(HttpStatus.UNPROCESSABLE_ENTITY, "이미 신고가 접수되었거나 오류로 신고에 실패하였습니다."),

    FAIL_WRITE_COMMENT(HttpStatus.UNPROCESSABLE_ENTITY, "댓글 작성에 실패하였습니다."),
    FAIL_DELETE_COMMENT(HttpStatus.UNPROCESSABLE_ENTITY,"댓글 삭제에 실패하였습니다."),

    FAIL_ACCEPT_REQUEST_BOARD(HttpStatus.UNPROCESSABLE_ENTITY, "게시글 허가에 실패하였습니다"),
    FAIL_REJECTION_REQUEST_BOARD(HttpStatus.UNPROCESSABLE_ENTITY, "게시글 거절에 실패하였습니다");

    private final HttpStatus status;
    private final String description;
}
