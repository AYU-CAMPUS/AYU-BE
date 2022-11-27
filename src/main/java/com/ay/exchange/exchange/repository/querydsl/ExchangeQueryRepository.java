package com.ay.exchange.exchange.repository.querydsl;

import com.ay.exchange.common.util.DateGenerator;
import com.ay.exchange.exchange.dto.ExchangeInfo;
import com.ay.exchange.exchange.dto.request.ExchangeRequest;
import com.ay.exchange.exchange.dto.response.ExchangeResponse;
import com.ay.exchange.exchange.exception.UnableExchangeException;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

import static com.ay.exchange.board.entity.QBoard.board;
import static com.ay.exchange.exchange.entity.QExchange.exchange;

@Repository
@RequiredArgsConstructor
public class ExchangeQueryRepository {
    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    public ExchangeResponse getMyData(PageRequest pageRequest, String userId) {
        Long count = queryFactory.select(board.count())
                .from(board)
                .where(board.userId.eq(userId))
                .fetchOne();

        List<ExchangeInfo> exchangaInfos = queryFactory
                .select(Projections.fields(
                        ExchangeInfo.class,
                        board.title,
                        board.id.as("boardId")
                ))
                .from(board)
                .where(board.userId.eq(userId))
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .fetch();

        return new ExchangeResponse(count, exchangaInfos);
    }

    @Transactional(rollbackFor = Exception.class)
    public void requestExchange(ExchangeRequest exchangeRequest, String userId) {
        String boardUserId = queryFactory.select(board.userId)
                .from(board)
                .where(board.id.eq(exchangeRequest.getBoardId()))
                .fetchOne();
        if (boardUserId == null) throw new UnableExchangeException();

        Integer canExchange = queryFactory.selectOne()
                .from(board)
                .where(board.id.eq(exchangeRequest.getRequesterBoardId())
                        .and(board.userId.eq(userId)))
                .fetchFirst();
        if (canExchange == null) throw new UnableExchangeException();

        String sql = "INSERT INTO exchange(created_date, last_modified_date, board_id, requester_board_id, requester_user_id, type, user_id)" +
                " VALUES(?,?,?,?,?,?,?), (?,?,?,?,?,?,?)";
        String currentDate = DateGenerator.getCurrentDate();
        Query query = em.createNativeQuery(sql)
                .setParameter(1, currentDate)
                .setParameter(2, currentDate)
                .setParameter(3, exchangeRequest.getBoardId())
                .setParameter(4, exchangeRequest.getRequesterBoardId())
                .setParameter(5, userId)
                .setParameter(6, 1)
                .setParameter(7, boardUserId)
                .setParameter(8, currentDate)
                .setParameter(9, currentDate)
                .setParameter(10, exchangeRequest.getRequesterBoardId())
                .setParameter(11, exchangeRequest.getBoardId())
                .setParameter(12, boardUserId)
                .setParameter(13, 1)
                .setParameter(14, userId);
        if (query.executeUpdate() != 2) throw new UnableExchangeException();
    }
}