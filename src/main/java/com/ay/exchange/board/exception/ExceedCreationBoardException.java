package com.ay.exchange.board.exception;

import com.ay.exchange.common.error.dto.ErrorMessage;
import com.ay.exchange.common.error.exception.ErrorException;

public class ExceedCreationBoardException extends ErrorException {
    public ExceedCreationBoardException() {
        super(ErrorMessage.EXCEED_CREATION_BOARD);
    }
}
