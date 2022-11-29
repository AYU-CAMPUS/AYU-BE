package com.ay.exchange.board.exception;

import com.ay.exchange.common.error.dto.ErrorMessage;
import com.ay.exchange.common.error.exception.ErrorException;

public class FileInvalidException extends ErrorException {
    public FileInvalidException() {
        super(ErrorMessage.FILE_INVALID);
    }
}