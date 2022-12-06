package com.ay.exchange.comment.exception;

import com.ay.exchange.common.error.dto.ErrorMessage;
import com.ay.exchange.common.error.exception.ErrorException;

public class FailDeleteCommentException extends ErrorException {
    public FailDeleteCommentException() {
        super(ErrorMessage.FAIL_DELETE_COMMENT);
    }
}
