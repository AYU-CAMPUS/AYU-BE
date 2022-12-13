package com.ay.exchange.user.exception;

import com.ay.exchange.common.error.dto.ErrorMessage;
import com.ay.exchange.common.error.exception.ErrorException;

public class FailUpdateUserInfoException extends ErrorException {
    public FailUpdateUserInfoException() {
        super(ErrorMessage.FAIL_UPDATE_USER_INFO);
    }
}
