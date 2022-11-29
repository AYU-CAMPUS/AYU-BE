package com.ay.exchange.common.util;

import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class FileValidator {
    private static final List<String> allowedFileTypes = List.of(
            "application/zip",    // .zip
            "application/pdf",    // .pdf
            "application/msword", // .doc, .dot
            "application/x-hwp", "applicaion/haansofthwp", "application/x-tika-msoffice", // .hwp
            "application/x-tika-ooxml", // .xlsx, .pptx, .docx
            "text/plain",     // .txt, .html 등
            "application/vnd.ms-word",          // .docx 등 워드 관련
            "application/vnd.ms-excel",         // .xls 등 엑셀 관련
            "application/vnd.ms-powerpoint",    // .ppt 등 파워포인트 관련
            "application/vnd.openxmlformats-officedocument",    // .docx, .dotx, .xlsx, .xltx, .pptx, .potx, .ppsx
            "applicaion/vnd.hancom");

    private static final List<String> allowedImageTypes = List.of(
            "image/png",
            "image/jpeg",
            "image/jpg"
    );

    public static boolean isAllowedImageType(MultipartFile file) {
        try {
            InputStream inputStream = file.getInputStream();
            Tika tika = new Tika();
            String mimeType = tika.detect(inputStream);
            if (allowedImageTypes.contains(mimeType)) {
                return true;
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean isAllowedFileType(MultipartFile file) {
        try {
            InputStream inputStream = file.getInputStream();
            Tika tika = new Tika();
            String mimeType = tika.detect(inputStream);
            if (allowedFileTypes.contains(mimeType)) {
                return true;
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }
}