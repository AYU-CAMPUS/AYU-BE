package com.ay.exchange.user.exception;

import com.ay.exchange.common.error.dto.ErrorMessage;
import com.ay.exchange.common.error.exception.ErrorException;

public class FailRefusalFileException extends ErrorException {
    public FailRefusalFileException() {
        super(ErrorMessage.FAIL_REFUSAL_FILE);
    }
}