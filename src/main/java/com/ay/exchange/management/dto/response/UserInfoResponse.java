package com.ay.exchange.management.dto.response;

import com.ay.exchange.management.dto.query.UserInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class UserInfoResponse {
    @Schema(description = "총 페이지 수")
    private Long totalPages;

    @Schema(description = "유저 정보")
    private List<UserInfo> userInfos;

}
