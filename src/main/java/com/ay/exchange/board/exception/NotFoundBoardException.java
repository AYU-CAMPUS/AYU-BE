package com.ay.exchange.board.exception;

import com.ay.exchange.common.error.dto.ErrorMessage;
import com.ay.exchange.common.error.exception.ErrorException;

public class NotFoundBoardException extends ErrorException {
    public NotFoundBoardException(){super(ErrorMessage.NOT_FOUND_BOARD);}
}
