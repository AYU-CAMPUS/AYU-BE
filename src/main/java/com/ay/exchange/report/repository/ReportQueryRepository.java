package com.ay.exchange.report.repository;

import com.ay.exchange.common.util.DateGenerator;
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

    @Transactional(rollbackFor = Exception.class)
    public void reportBoard(ReportBoardRequest reportBoardRequest, String userId) {
        String sql = "INSERT INTO report_board(board_id,user_id,reason,date) VALUES(?,?,?,?)";
        Query query = em.createNativeQuery(sql)
                .setParameter(1, reportBoardRequest.getBoardId())
                .setParameter(2, userId)
                .setParameter(3, reportBoardRequest.getReason())
                .setParameter(4, DateGenerator.getCurrentDate());

        try {
            if (query.executeUpdate() != 1) {
                throw new ReportException();
            }
        } catch (Exception e) {
            throw new ReportException();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void reportComment(ReportCommentRequest reportCommentRequest, String userId) {
        CommentInfo commentInfo = queryFactory.select(Projections.fields(
                        CommentInfo.class,
                        comment.userId.as("targetUserId"),
                        comment.content
                ))
                .from(comment)
                .where(comment.id.eq(reportCommentRequest.getCommentId()))
                .fetchOne();
        if (commentInfo == null) throw new ReportException();

        String sql = "INSERT INTO report_comment(user_id,target_user_id,content,reason,date) VALUES(?,?,?,?,?)";
        Query query = em.createNativeQuery(sql)
                .setParameter(1, userId)
                .setParameter(2, commentInfo.getTargetUserId())
                .setParameter(3, commentInfo.getContent())
                .setParameter(4, reportCommentRequest.getReason())
                .setParameter(5, DateGenerator.getCurrentDate());

        try {
            if (query.executeUpdate() != 1) {
                throw new ReportException();
            }
        } catch (Exception e) {
            throw new ReportException();
        }
    }
}