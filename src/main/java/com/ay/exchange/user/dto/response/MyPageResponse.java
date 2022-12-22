package com.ay.exchange.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MyPageResponse {
    @Schema(description = "닉네임")
    private String nickName;

    @Schema(description = "프로필 이미지 경로")
    private String profileImage;

    @Schema(description = "교환 완료 수")
    private Integer exchangeSuccessCount;

    @Schema(description = "내가 올린 자료 수")
    private Integer myDataCount;

    @Schema(description = "다운로드 가능한 자료 수")
    private Integer downloadCount;

    @Schema(description = "교환 중인 수")
    private Integer exchangeRequestCount;

    @Schema(description = "원하는 자료")
    private List<String> desiredData;

}