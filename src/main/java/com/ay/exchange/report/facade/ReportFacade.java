package com.ay.exchange.report.facade;

import com.ay.exchange.jwt.JwtTokenProvider;
import com.ay.exchange.report.dto.request.ReportBoardRequest;
import com.ay.exchange.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ReportFacade {
    private final ReportService reportService;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional(rollbackFor = Exception.class)
    public void reportBoard(ReportBoardRequest reportBoardRequest, String token) {
        reportService.reportBoard(reportBoardRequest, jwtTokenProvider.getUserEmail(token));
    }
}
