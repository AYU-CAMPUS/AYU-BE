package com.ay.exchange.user.exception;

import com.ay.exchange.common.error.dto.ErrorMessage;
import com.ay.exchange.common.error.exception.ErrorException;

public class DuplicateEmailException extends ErrorException {
    public DuplicateEmailException() {
        super(ErrorMessage.DUPLICATE_EMAIL);
    }
}
