package com.ay.exchange.common.error.exception;

import com.ay.exchange.common.error.dto.ErrorMessage;
import lombok.Getter;

@Getter
public class ErrorException extends RuntimeException {
    private final ErrorMessage errorMessage;

    public ErrorException(ErrorMessage errorMessage){
        super(errorMessage.getDescription());
        this.errorMessage=errorMessage;
    }
}
