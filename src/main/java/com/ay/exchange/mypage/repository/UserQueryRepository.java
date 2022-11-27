package com.ay.exchange.mypage.repository;


import com.ay.exchange.common.util.DateGenerator;
import com.ay.exchange.mypage.dto.*;
import com.ay.exchange.mypage.dto.request.ExchangeRequest;
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

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.*;

import static com.ay.exchange.exchange.entity.QExchangeCompletion.exchangeCompletion;
import static com.ay.exchange.user.entity.QUser.user;
import static com.ay.exchange.board.entity.QBoard.board;
import static com.ay.exchange.exchange.entity.QExchange.exchange;
import static com.querydsl.core.group.GroupBy.*;

@Repository
@RequiredArgsConstructor
public class UserQueryRepository {
    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    public MyPageInfo getMyPage(String userId) {
        return queryFactory.from(user)
                .leftJoin(board)
                .on(board.userId.eq(userId))
                .leftJoin(exchange)
                .on(exchange.userId.eq(userId).and(exchange.type.eq(-2)))
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
        Long count = queryFactory.select(exchangeCompletion.count())
                .from(exchangeCompletion)
                .where(exchangeCompletion.userId.eq(userId))
                .fetchOne();

        List<DownloadableInfo> downloadableInfos = queryFactory
                .select(Projections.fields(
                        DownloadableInfo.class,
                        exchangeCompletion.date.as("exchangeDate"),
                        board.title,
                        user.nickName.as("writer"),
                        board.id.as("requesterBoardId")
                ))
                .from(exchangeCompletion)
                .innerJoin(board)
                .on(exchangeCompletion.requesterBoardId.eq(board.id))
                .innerJoin(user)
                .on(board.userId.eq(user.userId))
                .where(exchangeCompletion.userId.eq(userId))
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .fetch();

        return new DownloadableResponse(count, downloadableInfos);
    }

    public FilePathInfo getFilePath(Long requesterBoardId, String userId) {
        FilePathInfo filePathInfo = queryFactory.select(Projections.fields(
                        FilePathInfo.class,
                        board.userId,
                        board.filePath
                ))
                .from(exchangeCompletion)
                .innerJoin(board)
                .on(board.id.eq(exchangeCompletion.requesterBoardId))
                .where(exchangeCompletion.requesterBoardId.eq(requesterBoardId)
                        .and(exchangeCompletion.userId.eq(userId)))
                .fetchOne();
        return filePathInfo;
    }

    public ExchangeResponse getExchanges(PageRequest pageRequest, String userId) {
        Long count = queryFactory.select(exchange.count())
                .from(exchange)
                .where(exchange.userId.eq(userId))
                .fetchOne();
        if (count == 0L) {
            return new ExchangeResponse(0L, new ArrayList<>());
        }

        List<ExchangeInfo> exchangeInfos = queryFactory
                .select(Projections.fields(
                        ExchangeInfo.class,
                        exchange.Id.as("exchangeId"),
                        exchange.createdDate.as("applicationDate"),
                        user.nickName.as("requesterNickName"),
                        exchange.requesterUserId.as("requesterId"),
                        board.title,
                        exchange.boardId,
                        board.id.as("requesterBoardId")
                ))
                .from(exchange)
                .innerJoin(user)
                .on(exchange.requesterUserId.eq(user.userId))
                .innerJoin(board)
                .on(exchange.requesterBoardId.eq(board.id))
                .where(exchange.userId.eq(userId))
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .fetch();

        return new ExchangeResponse(count, exchangeInfos);
    }

    @Transactional(rollbackFor = Exception.class)
    public void acceptExchange(Long exchangeId, ExchangeRequest exchangeRequest, String userId) {
        //교환신청삭제
        Long count = queryFactory.delete(exchange)
                .where(exchange.Id.eq(exchangeId)
                        .or(exchange.boardId.eq(exchangeRequest.getRequesterBoardId())
                                .and(exchange.userId.eq(exchangeRequest.getRequesterId()))))
                .execute();
        if (count != 2L) throw new FailAcceptFileException();

        //교환완료
        String date = DateGenerator.getCurrentDate();
        String sql = "INSERT INTO exchange_completion(board_id,date,user_id) VALUES(?,?,?),(?,?,?)";
        Query query = em.createNativeQuery(sql)
                .setParameter(1, exchangeRequest.getRequesterBoardId())
                .setParameter(2, date)
                .setParameter(3, userId)
                .setParameter(4, exchangeRequest.getBoardId())
                .setParameter(5, date)
                .setParameter(6, exchangeRequest.getRequesterId());
        if (query.executeUpdate() != 2) throw new FailAcceptFileException();

        //게시글과 사용자 교환 완료 증가
        sql = "UPDATE board b, user u SET b.exchange_success_count=b.exchange_success_count+1" +
                ", u.exchange_success_count=u.exchange_success_count+1 WHERE b.user_id=u.user_id AND b.board_id=? AND u.user_id = ? OR b.board_id=? AND u.user_id=?";
        query = em.createNativeQuery(sql)
                .setParameter(1, exchangeRequest.getBoardId())
                .setParameter(2, userId)
                .setParameter(3, exchangeRequest.getRequesterBoardId())
                .setParameter(4, exchangeRequest.getRequesterId());
        if (query.executeUpdate() != 4) {
            throw new FailAcceptFileException();
        }


        //exchangeRequest.getApplicantId(); //사용자 고유 아이디로 알림을 주자
    }

}