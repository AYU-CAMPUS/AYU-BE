package com.ay.exchange.user.exception;

import com.ay.exchange.common.error.dto.ErrorMessage;
import com.ay.exchange.common.error.exception.ErrorException;

public class DuplicateUserIdException extends ErrorException {
    public DuplicateUserIdException() {super(ErrorMessage.DUPLICATE_USER_ID);}
}
