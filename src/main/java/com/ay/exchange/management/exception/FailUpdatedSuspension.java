package com.ay.exchange.management.exception;

import com.ay.exchange.common.error.dto.ErrorMessage;
import com.ay.exchange.common.error.exception.ErrorException;

public class FailUpdatedSuspension extends ErrorException {

    public FailUpdatedSuspension() {
        super(ErrorMessage.FAIL_UPDATED_SUSPENSION);
    }
}
