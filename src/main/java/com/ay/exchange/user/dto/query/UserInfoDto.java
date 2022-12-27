package com.ay.exchange.user.dto.query;

import com.ay.exchange.user.entity.vo.Authority;

public interface UserInfoDto {
    String getNickName();
    Authority getAuthority();
    String getSuspendedDate();
    String getSuspendedReason();
}
