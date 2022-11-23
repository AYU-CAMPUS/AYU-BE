package com.ay.exchange.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class MyDataResponse {
    @Schema(description = "내 자료 전체 수")
    private Long dataPages;

    private List<MyDataInfo> myDataInfos;
}