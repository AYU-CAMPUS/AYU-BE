package com.ay.exchange.report.exception;

import com.ay.exchange.common.error.dto.ErrorMessage;
import com.ay.exchange.common.error.exception.ErrorException;

public class ReportException extends ErrorException {
    public ReportException() {
        super(ErrorMessage.FAIL_REPORT);
    }
}