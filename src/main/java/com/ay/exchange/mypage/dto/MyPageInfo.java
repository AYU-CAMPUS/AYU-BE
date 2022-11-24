package com.ay.exchange.mypage.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Getter
public class MyPageInfo {
    private String nickName;
    private String profileImage;
    private Integer exchangeSuccessCount;
    private Set<Long> myDataCount;
    private Set<Long> downloadCount;

    public Set<Long> getMyDataCounts() {
        return myDataCount == null ? new HashSet<Long>() : myDataCount;
    }

    public int getDownloadCount() {
        return downloadCount == null ? 0 : downloadCount.size();
    }
}