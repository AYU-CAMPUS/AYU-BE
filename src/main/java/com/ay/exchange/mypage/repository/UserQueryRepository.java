package com.ay.exchange.mypage.repository;


import com.ay.exchange.mypage.dto.*;
import com.ay.exchange.mypage.dto.response.DownloadableResponse;
import com.ay.exchange.mypage.dto.response.ExchangeResponse;
import com.ay.exchange.mypage.dto.response.MyDataResponse;
import com.ay.exchange.mypage.exception.FailAcceptFileException;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
                .where(board.userId.eq(userId))
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

    public DownloadableResponse getDownloadable(PageRequest pageRequest, String userId) {
        Long count = queryFactory.select(exchange.count())
                .from(exchange)
                .where(exchange.userId.eq(userId)
                        .and(exchange.type.eq(2)))
                .fetchOne();

        List<DownloadableInfo> downloadableInfos = queryFactory
                .select(Projections.fields(
                        DownloadableInfo.class,
                        exchange.lastModifiedDate.as("exchangeDate"),
                        board.title,
                        board.writer,
                        board.id.as("boardId")
                ))
                .from(exchange)
                .innerJoin(board)
                .on(exchange.boardId.eq(board.id))
                .where(exchange.userId.eq(userId)
                        .and(exchange.type.eq(2)))
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .fetch();

        return new DownloadableResponse(count, downloadableInfos);
    }

    public FilePathInfo getFilePath(Long boardId, String userId) {
        FilePathInfo filePathInfo = queryFactory.select(Projections.fields(
                        FilePathInfo.class,
                        board.userId,
                        board.filePath
                ))
                .from(exchange)
                .innerJoin(board)
                .on(board.id.eq(exchange.boardId))
                .where(exchange.userId.eq(userId)
                        .and(exchange.type.eq(2))
                        .and(exchange.boardId.eq(boardId)))
                .fetchOne();
        return filePathInfo;
    }

    public ExchangeResponse getExchanges(PageRequest pageRequest, String userId) {
        List<Long> boards = queryFactory.select(board.id)
                .from(board)
                .where(board.userId.eq(userId))
                .fetch();
        if (boards.isEmpty()) {
            return new ExchangeResponse(0L, new ArrayList<>());
        }

        Long count = queryFactory.select(exchange.count())
                .from(exchange)
                .where(exchange.type.eq(1)
                        .and(exchange.boardId.in(boards)))
                .fetchOne();
        if (count == 0L) {
            return new ExchangeResponse(0L, new ArrayList<>());
        }

        List<ExchangeInfo> exchangeInfos = queryFactory
                .select(Projections.fields(
                        ExchangeInfo.class,
                        exchange.Id.as("exchangeId"),
                        exchange.createdDate.as("applicationDate"),
                        user.nickName.as("applicant"),
                        board.title,
                        board.id.as("boardId"),
                        exchange.userId.as("applicantId")
                ))
                .from(exchange)
                .innerJoin(user)
                .on(exchange.userId.eq(user.userId))
                .innerJoin(board)
                .on(exchange.boardId.eq(board.id))
                .where(exchange.boardId.in(boards)
                        .and(exchange.type.eq(1)))
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .fetch();

        return new ExchangeResponse(count, exchangeInfos);
    }

}