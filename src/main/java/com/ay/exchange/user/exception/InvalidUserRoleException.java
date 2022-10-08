package com.ay.exchange.user.exception;

import com.ay.exchange.common.error.dto.ErrorMessage;
import com.ay.exchange.common.error.exception.ErrorException;

public class InvalidUserRoleException extends ErrorException {
    public InvalidUserRoleException(){
        super(ErrorMessage.NOT_VALID_ROLE_ERROR);
    }
}
