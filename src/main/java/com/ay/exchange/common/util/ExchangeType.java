package com.ay.exchange.common.util;

public enum ExchangeType {
    INTERCHANGEABLE(0L),
    COMPLETION(1L),
    OWNER(-1L),
    REQUEST(-2L),
    ACCEPT(-3L);

    private Long type;

    ExchangeType(Long type) {
        this.type = type;
    }

    public Long getType() {
        return type;
    }

}
