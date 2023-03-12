package com.ay.exchange.report.repository;

import com.ay.exchange.common.util.DateUtil;
import com.ay.exchange.report.dto.query.CommentInfo;
import com.ay.exchange.report.dto.request.ReportBoardRequest;
import com.ay.exchange.report.dto.request.ReportCommentRequest;
import com.ay.exchange.report.exception.ReportException;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import static com.ay.exchange.comment.entity.QComment.comment;


@RequiredArgsConstructor
@Repository
public class ReportQueryRepository {
    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    public void reportBoard(ReportBoardRequest reportBoardRequest, String email) {
        String sql = "INSERT INTO report_board(board_id,email,reason,date) VALUES(?,?,?,?)";
        Query query = em.createNativeQuery(sql)
                .setParameter(1, reportBoardRequest.getBoardId())
                .setParameter(2, email)
                .setParameter(3, reportBoardRequest.getReason())
                .setParameter(4, DateUtil.getCurrentDate());
        query.executeUpdate();
    }

    public void reportComment(ReportCommentRequest reportCommentRequest, String email) {
        String sql = "INSERT INTO report_comment(email,comment_id, reason,date) VALUES(?,?,?,?,?)";
        Query query = em.createNativeQuery(sql)
                .setParameter(1, email)
                .setParameter(2, reportCommentRequest.getCommentId())
                .setParameter(3, reportCommentRequest.getReason())
                .setParameter(4, DateUtil.getCurrentDate());
        query.executeUpdate();
    }
}