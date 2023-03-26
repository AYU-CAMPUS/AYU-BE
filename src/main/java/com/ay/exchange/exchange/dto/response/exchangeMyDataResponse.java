package com.ay.exchange.exchange.dto.response;

import com.ay.exchange.exchange.dto.MyDataInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class exchangeMyDataResponse {
    @Schema(description = "내 자료 수")
    private Long exchangePages;

    private List<MyDataInfo> myDataInfos;
}
