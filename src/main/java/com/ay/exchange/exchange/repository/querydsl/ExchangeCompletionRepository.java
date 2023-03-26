package com.ay.exchange.exchange.repository.querydsl;

import com.ay.exchange.user.dto.DownloadableInfo;
import com.ay.exchange.user.dto.request.ExchangeAccept;
import com.ay.exchange.user.dto.response.DownloadableResponse;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.DateTemplate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import java.util.List;

import static com.ay.exchange.board.entity.QBoard.board;
import static com.ay.exchange.exchange.entity.QExchangeCompletion.exchangeCompletion;
import static com.ay.exchange.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class ExchangeCompletionRepository {
    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    public boolean checkExchangeCompletionDate(String date, String email, Long boardId) {
        Long count = queryFactory.select(exchangeCompletion.count())
                .from(exchangeCompletion)
                .where(getExchangeCompletionDate().gt(date)
                        .and(exchangeCompletion.boardId.eq(boardId))
                        .and(exchangeCompletion.email.eq(email)))
                .limit(1L)
                .fetchOne();
        return count == 0L;
    }

    public boolean checkMyPageExchangeCompletionDate(String date, String email) {
        return queryFactory.select(exchangeCompletion.count())
                .from(exchangeCompletion)
                .where(getExchangeCompletionDate().gt(date)
                        .and(exchangeCompletion.email.eq(email)))
                .limit(1L)
                .fetchOne() == 0L;
    }

    public int acceptExchange(String currentDate, ExchangeAccept exchangeAccept, String email) {
        //교환완료
        String sql = "INSERT INTO exchange_completion(board_id,date,email,requester_board_id) VALUES(?,?,?,?),(?,?,?,?)";
        Query query = em.createNativeQuery(sql)
                .setParameter(1, exchangeAccept.getRequesterBoardId())
                .setParameter(2, currentDate)
                .setParameter(3, exchangeAccept.getRequesterId())
                .setParameter(4, exchangeAccept.getBoardId())
                .setParameter(5, exchangeAccept.getBoardId())
                .setParameter(6, currentDate)
                .setParameter(7, email)
                .setParameter(8, exchangeAccept.getRequesterBoardId());
        return query.executeUpdate();
    }

    public DownloadableResponse getDownloadable(PageRequest pageRequest, String email) {
        Long count = getDownloadableCount(email);

        List<DownloadableInfo> downloadableInfos = queryFactory
                .select(Projections.fields(
                        DownloadableInfo.class,
                        exchangeCompletion.date.as("exchangeDate"),
                        board.title,
                        user.nickName.as("writer"),
                        board.id.as("requesterBoardId"),
                        board.boardCategory.category
                ))
                .from(exchangeCompletion)
                .innerJoin(board)
                .on(exchangeCompletion.requesterBoardId.eq(board.id))
                .innerJoin(user)
                .on(board.email.eq(user.email))
                .where(exchangeCompletion.email.eq(email))
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .orderBy(exchangeCompletion.Id.desc())
                .fetch();

        return new DownloadableResponse(count, downloadableInfos);
    }

    public Long getDownloadableCount(String email) {
        return queryFactory.select(exchangeCompletion.count())
                .from(exchangeCompletion)
                .where(exchangeCompletion.email.eq(email))
                .fetchOne();
    }

    private DateTemplate getExchangeCompletionDate() {
        return Expressions.dateTemplate(
                String.class,
                "DATE_FORMAT({0}, {1})",
                exchangeCompletion.date,
                ConstantImpl.create("%Y-%m-%d")
        );
    }
}
