package com.ay.exchange.aws.exception;

import com.ay.exchange.common.error.dto.ErrorMessage;
import com.ay.exchange.common.error.exception.ErrorException;

public class EmptyFileException extends ErrorException {
    public EmptyFileException() {
        super(ErrorMessage.FILE_NOT_EXISTS);
    }
}
