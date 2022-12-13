package com.ay.exchange.report.service;

import com.ay.exchange.jwt.JwtTokenProvider;
import com.ay.exchange.report.dto.request.ReportBoardRequest;
import com.ay.exchange.report.dto.request.ReportCommentRequest;
import com.ay.exchange.report.repository.ReportQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportQueryRepository reportQueryRepository;
    private final JwtTokenProvider jwtTokenProvider;


    public Boolean reportBoard(ReportBoardRequest reportBoardRequest, String token) {
        reportQueryRepository.reportBoard(reportBoardRequest, jwtTokenProvider.getUserEmail(token));
        return true;
    }

    public Boolean reportComment(ReportCommentRequest reportCommentRequest, String token) {
        reportQueryRepository.reportComment(reportCommentRequest,jwtTokenProvider.getUserEmail(token));
        return true;
    }
}