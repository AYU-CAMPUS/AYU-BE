package com.ay.exchange.management.exception;

import com.ay.exchange.common.error.dto.ErrorMessage;
import com.ay.exchange.common.error.exception.ErrorException;

public class FailAcceptRequestBoard extends ErrorException {
    public FailAcceptRequestBoard() {
        super(ErrorMessage.FAIL_ACCEPT_REQUEST_BOARD);
    }
}
