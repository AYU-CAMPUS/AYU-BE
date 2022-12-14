package com.ay.exchange.user.dto.response;

import com.ay.exchange.user.dto.DownloadableInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class DownloadableResponse {
    @Schema(description = "다운로드 가능한 자료 수")
    private Long downloadablePages;

    private List<DownloadableInfo> downloadableInfos;
}