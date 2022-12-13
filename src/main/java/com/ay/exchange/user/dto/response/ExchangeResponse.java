package com.ay.exchange.user.dto.response;

import com.ay.exchange.user.dto.ExchangeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class ExchangeResponse {
    @Schema(description = "교환 신청 수")
    private Long exchangePages;

    private List<ExchangeInfo> exchangeInfos;
}