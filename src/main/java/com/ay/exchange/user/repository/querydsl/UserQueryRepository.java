package com.ay.exchange.user.repository.querydsl;


import com.ay.exchange.user.dto.query.MyPageInfo;
import com.ay.exchange.user.dto.response.MyDataInfo;
import com.ay.exchange.user.dto.response.MyDataResponse;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

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

    @Transactional(rollbackFor = Exception.class)
    public Boolean updatePassword(String userId, String password) {
        return queryFactory.update(user)
                .where(user.userId.eq(userId))
                .set(user.password, password)
                .execute() == 1L;
    }

    public MyDataResponse getMyData(PageRequest pageRequest, String userId) {
        Long count = queryFactory.select(board.count())
                .from(board)
                .where(board.userId.eq("tkddls8900"))
                .fetchOne();

        List<MyDataInfo> myDataInfos = queryFactory
                .select(Projections.fields(
                        MyDataInfo.class,
                        board.createdDate,
                        board.title,
                        board.id.as("boardId")
                ))
                .from(board)
                .where(board.userId.eq(userId))
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .fetch();

        return new MyDataResponse(count, myDataInfos);
    }
}