package com.ay.exchange.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.io.ByteArrayResource;

@AllArgsConstructor
@Getter
public class DownloadFileInfo {
    private String filePath;
    private ByteArrayResource resource;

    public int getDataLength(){
        return resource.getByteArray().length;
    }
}
