package com.ay.exchange.report.service;

import com.ay.exchange.report.dto.request.ReportBoardRequest;
import com.ay.exchange.report.dto.request.ReportCommentRequest;
import com.ay.exchange.report.exception.ReportException;
import com.ay.exchange.report.repository.ReportQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportQueryRepository reportQueryRepository;

    public void reportBoard(ReportBoardRequest reportBoardRequest, String email) {
        try {
            reportQueryRepository.reportBoard(reportBoardRequest, email); //하나의 게시글에서는 하나의 신고만 가능하도록 한다.
        } catch (Exception e) {
            throw new ReportException();
        }

    }

    public void reportComment(ReportCommentRequest reportCommentRequest, String email) {
        try {
            reportQueryRepository.reportComment(reportCommentRequest, email); //하나의 댓글에는 하나의 신고만 가능하도록 한다.
        } catch (Exception e) {
            throw new ReportException();
        }
    }
}