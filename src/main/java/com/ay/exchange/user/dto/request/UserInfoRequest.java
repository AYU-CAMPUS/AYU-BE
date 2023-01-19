package com.ay.exchange.user.dto.request;

import com.ay.exchange.common.util.CharacterConstraint;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserInfoRequest {
    @Schema(description = "바꿀 닉네임 -> 중복 확인을 안 눌렀으면 기존 닉네임으로 요청")
    @Pattern(regexp = "^[a-zA-Z\\d가-힣ㄱ-ㅎ]{1,8}$")
    private String nickName;

    @Schema(description = "원하는 자료 -> 닉네임만 변경하는 거면 기존 원하는 자료 값으로 요청")
    @CharacterConstraint
    private List<String> desiredData;
}