package com.ay.exchange.user.exception;

import com.ay.exchange.common.error.dto.ErrorMessage;
import com.ay.exchange.common.error.exception.ErrorException;

public class NotExistsFileException extends ErrorException {
    public NotExistsFileException() {
        super(ErrorMessage.NOT_EXISTS_FILE);
    }
}