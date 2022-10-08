package com.ay.exchange.common.error.dto;

import lombok.Getter;

@Getter
public class ErrorDto {
    private final String message;
    private final String reason;

    public ErrorDto(String message, String reason) {
        this.message = message;
        this.reason = reason;
    }

    public ErrorDto(ErrorMessage errorMessage){
        message= errorMessage.name();
        reason= errorMessage.getDescription();
    }
}
