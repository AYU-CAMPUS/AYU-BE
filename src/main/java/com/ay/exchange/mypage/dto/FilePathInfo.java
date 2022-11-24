package com.ay.exchange.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class FilePathInfo {
    private String userId;
    private String filePath;

    @Override
    public String toString() {
        return userId + "/" + filePath;
    }
}