package com.ay.exchange.board.exception;

import com.ay.exchange.common.error.dto.ErrorMessage;
import com.ay.exchange.common.error.exception.ErrorException;

public class FailWriteBoardException extends ErrorException {
    public FailWriteBoardException() {
        super(ErrorMessage.FAIL_WRITE_BOARD);
    }
}
