package com.ay.exchange.board.exception;

import com.ay.exchange.common.error.dto.ErrorMessage;
import com.ay.exchange.common.error.exception.ErrorException;

public class FailModifyBoardException extends ErrorException {
    public FailModifyBoardException() {
        super(ErrorMessage.FAIL_MODIFY_BOARD);
    }
}
