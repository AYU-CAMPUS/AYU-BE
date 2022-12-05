package com.ay.exchange.board.exception;

import com.ay.exchange.common.error.dto.ErrorMessage;
import com.ay.exchange.common.error.exception.ErrorException;

public class FailDeleteBoardException extends ErrorException {
    public FailDeleteBoardException() {
        super(ErrorMessage.FAIL_DELETE_BOARD);
    }
}
