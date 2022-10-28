package com.ay.exchange.aws.exception;

import com.ay.exchange.common.error.dto.ErrorMessage;
import com.ay.exchange.common.error.exception.ErrorException;

public class FileUploadFailedException extends ErrorException {
    public FileUploadFailedException() {
        super(ErrorMessage.FILE_UPLOAD_ERROR);
    }
}
