package com.ay.exchange.user.exception;

import com.ay.exchange.common.error.dto.ErrorMessage;
import com.ay.exchange.common.error.exception.ErrorException;

public class ExistsEmailException extends ErrorException {
    public ExistsEmailException() {
        super(ErrorMessage.EXISTS_EMAIL);
    }
}
