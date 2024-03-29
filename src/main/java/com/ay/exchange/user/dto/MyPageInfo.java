package com.ay.exchange.user.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class MyPageInfo {
    private String nickName;
    private String profileImage;
    private Integer exchangeSuccessCount;
    private Set<Long> myDataCount;
    private Set<Long> exchangeRequests;
    private String desiredData;

    public Set<Long> getMyDataCounts() {
        return myDataCount == null ? new HashSet<Long>() : myDataCount;
    }

    public int getExchangeRequestsCount() {
        return exchangeRequests == null ? 0 : exchangeRequests.size();
    }
}