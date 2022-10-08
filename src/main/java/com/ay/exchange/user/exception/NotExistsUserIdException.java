package com.ay.exchange.user.exception;

import com.ay.exchange.common.error.dto.ErrorMessage;
import com.ay.exchange.common.error.exception.ErrorException;

public class NotExistsUserIdException extends ErrorException {
    public NotExistsUserIdException(){
        super(ErrorMessage.NOT_EXISTS_USERID);
    }
}
