package com.ay.exchange.mypage.exception;

import com.ay.exchange.common.error.dto.ErrorMessage;
import com.ay.exchange.common.error.exception.ErrorException;

public class DuplicateNickNameException extends ErrorException {
    public DuplicateNickNameException() {
        super(ErrorMessage.DUPLICATE_NICKNAME);
    }
}