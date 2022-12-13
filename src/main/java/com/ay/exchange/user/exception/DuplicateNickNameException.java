package com.ay.exchange.user.exception;

import com.ay.exchange.common.error.dto.ErrorMessage;
import com.ay.exchange.common.error.exception.ErrorException;

public class DuplicateNickNameException extends ErrorException {
    public DuplicateNickNameException() {
        super(ErrorMessage.DUPLICATE_NICKNAME);
    }
}