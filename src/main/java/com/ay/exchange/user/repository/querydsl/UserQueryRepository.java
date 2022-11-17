package com.ay.exchange.user.repository.querydsl;


import com.ay.exchange.user.dto.query.MyPageInfo;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.ay.exchange.user.entity.QUser.user;
import static com.ay.exchange.board.entity.QBoard.board;
import static com.ay.exchange.exchange.entity.QExchange.exchange;
import static com.querydsl.core.group.GroupBy.*;

@Repository
@RequiredArgsConstructor
public class UserQueryRepository {
    private final JPAQueryFactory queryFactory;

    public MyPageInfo getMyPage(String userId) {
        return queryFactory.from(user)
                .leftJoin(board)
                .on(board.userId.eq(userId))
                .leftJoin(exchange)
                .on(exchange.userId.eq(userId).and(exchange.type.eq(1)))
                .where(user.userId.eq(userId))
                .transform(
                        groupBy(user.userId)
                                .as(Projections.fields(
                                        MyPageInfo.class,
                                        user.nickName,
                                        user.profileImage.coalesce("default.svg").as("profileImage"),
                                        user.exchangeSuccessCount,
                                        set(board.id).as("myDataCount"),
                                        set(exchange.boardId).as("downloadCount")
                                ))
                ).get(userId);
    }

    public Long getExchangeRequestCount(Set<Long> boards) {
        return queryFactory.select(exchange.count())
                .from(exchange)
                .where(exchange.boardId.in(boards))
                .fetchOne();
    }
}
