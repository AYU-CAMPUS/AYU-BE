package com.ay.exchange.exchange.repository.querydsl;

import com.ay.exchange.common.util.Approval;

import com.ay.exchange.exchange.dto.ExchangeInfo;
import com.ay.exchange.exchange.dto.request.ExchangeRequest;
import com.ay.exchange.exchange.dto.response.ExchangeResponse;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

import static com.ay.exchange.board.entity.QBoard.board;
import static com.ay.exchange.exchange.entity.QExchange.exchange;
import static com.ay.exchange.exchange.entity.QExchangeCompletion.exchangeCompletion;

@Repository
@RequiredArgsConstructor
public class ExchangeQueryRepository {
    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    public ExchangeResponse getMyData(PageRequest pageRequest, String email) {
        Long count = queryFactory.select(board.count())
                .from(board)
                .where(board.email.eq(email)
                        .and(board.approval.eq(Approval.AGREE.getApproval())))
                .fetchOne();

        List<ExchangeInfo> exchangaInfos = queryFactory
                .select(Projections.fields(
                        ExchangeInfo.class,
                        board.title,
                        board.id.as("boardId")
                ))
                .from(board)
                .where(board.email.eq(email)
                        .and(board.approval.eq(Approval.AGREE.getApproval())))
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .fetch();

        return new ExchangeResponse(count, exchangaInfos);
    }

    public int requestExchange(ExchangeRequest exchangeRequest, String boardUserEmail, String email, String currentDate) {
        String sql = "INSERT INTO exchange(created_date, last_modified_date, board_id, requester_board_id, requester_email, type, email)" +
                " VALUES(?,?,?,?,?,?,?), (?,?,?,?,?,?,?)";
        Query query = em.createNativeQuery(sql)
                .setParameter(1, currentDate)
                .setParameter(2, currentDate)
                .setParameter(3, exchangeRequest.getBoardId())
                .setParameter(4, exchangeRequest.getRequesterBoardId())
                .setParameter(5, email)
                .setParameter(6, -3) //-3은 교환신청을 받음
                .setParameter(7, boardUserEmail)
                .setParameter(8, currentDate)
                .setParameter(9, currentDate)
                .setParameter(10, exchangeRequest.getRequesterBoardId())
                .setParameter(11, exchangeRequest.getBoardId())
                .setParameter(12, boardUserEmail)
                .setParameter(13, -2) //-2는 교환요청을 함
                .setParameter(14, email);
        return query.executeUpdate();
    }

    public boolean existsExchangeCompletion(ExchangeRequest exchangeRequest) {
        return queryFactory.selectOne()
                .from(exchangeCompletion)
                .where(exchangeCompletion.boardId.eq(exchangeRequest.getBoardId())
                        .and(exchangeCompletion.requesterBoardId.eq(exchangeRequest.getRequesterBoardId())))
                .fetchFirst() != null;
    }

    public boolean existsExchange(ExchangeRequest exchangeRequest, String email) {
        return queryFactory.selectOne()
                .from(exchange)
                .where(exchange.boardId.eq(exchangeRequest.getBoardId())
                        .and(exchange.requesterEmail.eq(email)))
                .fetchFirst() != null;
    }

    public String findBoardUserEmail(Long boardId, String email) {
        String boardUserEmail = queryFactory.select(board.email)
                .from(board)
                .where(board.id.eq(boardId)
                        .and(board.approval.eq(Approval.AGREE.getApproval()))
                        .and(board.email.ne(email)))
                .fetchOne();
        return boardUserEmail;
    }

    public boolean existsRequesterBoard(Long requesterBoardId, String email) {
        return queryFactory.selectOne()
                .from(board)
                .where(board.id.eq(requesterBoardId)
                        .and(board.email.eq(email))
                        .and(board.approval.eq(Approval.AGREE.getApproval())))
                .fetchFirst() != null;
    }
}