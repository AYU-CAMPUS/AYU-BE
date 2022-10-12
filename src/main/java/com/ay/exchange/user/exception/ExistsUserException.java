package com.ay.exchange.user.exception;

import com.ay.exchange.common.error.dto.ErrorMessage;
import com.ay.exchange.common.error.exception.ErrorException;

public class ExistsUserException extends ErrorException {
    public ExistsUserException() {super(ErrorMessage.EXISTS_USER);}
}
