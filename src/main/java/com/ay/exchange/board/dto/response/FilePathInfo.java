package com.ay.exchange.board.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class FilePathInfo {
    private String email;
    private String filePath;

    @Override
    public String toString() {
        return email + "/" + filePath;
    }
}