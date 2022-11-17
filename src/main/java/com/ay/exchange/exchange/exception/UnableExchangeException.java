package com.ay.exchange.exchange.exception;

import com.ay.exchange.common.error.dto.ErrorMessage;
import com.ay.exchange.common.error.exception.ErrorException;

public class UnableExchangeException extends ErrorException {

    public UnableExchangeException() {
        super(ErrorMessage.UNABLE_EXCHANGE);
    }
}