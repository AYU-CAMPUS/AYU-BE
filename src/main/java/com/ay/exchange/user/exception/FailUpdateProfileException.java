package com.ay.exchange.user.exception;

import com.ay.exchange.common.error.dto.ErrorMessage;
import com.ay.exchange.common.error.exception.ErrorException;

public class FailUpdateProfileException extends ErrorException {
    public FailUpdateProfileException() {
        super(ErrorMessage.FAIL_UPDATE_PROFILE);
    }
}