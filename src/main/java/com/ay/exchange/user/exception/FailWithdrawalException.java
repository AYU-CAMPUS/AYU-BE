package com.ay.exchange.user.exception;

import com.ay.exchange.common.error.dto.ErrorMessage;
import com.ay.exchange.common.error.exception.ErrorException;

public class FailWithdrawalException extends ErrorException {
    public FailWithdrawalException() {
        super(ErrorMessage.FAIL_WITHDRAWAL);
    }
}