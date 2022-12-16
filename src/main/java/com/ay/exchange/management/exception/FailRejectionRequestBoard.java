package com.ay.exchange.management.exception;

import com.ay.exchange.common.error.dto.ErrorMessage;
import com.ay.exchange.common.error.exception.ErrorException;

public class FailRejectionRequestBoard extends ErrorException {
    public FailRejectionRequestBoard() {
        super(ErrorMessage.FAIL_REJECTION_REQUEST_BOARD);
    }
}
