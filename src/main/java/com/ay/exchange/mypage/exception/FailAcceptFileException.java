package com.ay.exchange.mypage.exception;

import com.ay.exchange.common.error.dto.ErrorMessage;
import com.ay.exchange.common.error.exception.ErrorException;

public class FailAcceptFileException extends ErrorException {
    public FailAcceptFileException() {
        super(ErrorMessage.FAIL_ACCEPT_FILE);
    }
}