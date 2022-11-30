package com.ay.exchange.comment.exception;

import com.ay.exchange.common.error.dto.ErrorMessage;
import com.ay.exchange.common.error.exception.ErrorException;

public class FailWriteCommentException extends ErrorException {
    public FailWriteCommentException() {
        super(ErrorMessage.FAIL_WRITE_COMMENT);
    }
}