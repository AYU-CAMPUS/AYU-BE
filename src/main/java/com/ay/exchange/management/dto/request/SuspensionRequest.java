package com.ay.exchange.management.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SuspensionRequest {
    @Schema(description = "정지당할 사용자 이메일")
    private String email;

    @Schema(description = "정지 기간 ex)2022-12-12 형태로")
    private String date;
}
