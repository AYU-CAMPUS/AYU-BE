package com.ay.exchange.common.util;

import org.springframework.data.domain.PageRequest;

public class PagingGenerator {
    private static final int SIZE = 10;

    public static PageRequest getPageRequest(Integer page) {
        return PageRequest.of(page > 0 ? (page - 1) : 0, SIZE);
    }
}
