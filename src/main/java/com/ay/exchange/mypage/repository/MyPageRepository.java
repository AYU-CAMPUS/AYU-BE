package com.ay.exchange.mypage.repository;


import com.ay.exchange.common.util.DateGenerator;
import com.ay.exchange.mypage.dto.*;
import com.ay.exchange.mypage.dto.request.ExchangeAccept;
import com.ay.exchange.mypage.dto.request.ExchangeRefusal;
import com.ay.exchange.mypage.dto.response.DownloadableResponse;
import com.ay.exchange.mypage.dto.response.ExchangeResponse;
import com.ay.exchange.mypage.dto.response.MyDataResponse;
import com.ay.exchange.mypage.exception.FailAcceptFileException;
import com.ay.exchange.mypage.exception.FailRefusalFileException;
import com.ay.exchange.mypage.exception.FailUpdateProfileException;
import com.ay.exchange.mypage.exception.FailWithdrawalException;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.DateTemplate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.ay.exchange.board.entity.QBoardContent.boardContent;
import static com.ay.exchange.comment.entity.QComment.comment;
import static com.ay.exchange.exchange.entity.QExchangeCompletion.exchangeCompletion;
import static com.ay.exchange.user.entity.QUser.user;
import static com.ay.exchange.board.entity.QBoard.board;
import static com.ay.exchange.exchange.entity.QExchange.exchange;
import static com.querydsl.core.group.GroupBy.*;

@Repository
@RequiredArgsConstructor
public class MyPageRepository {
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
    public void acceptExchange(ExchangeAccept exchangeAccept, String userId) {
        //교환신청삭제
        Long count = queryFactory.delete(exchange)
                .where(exchange.Id.eq(exchangeAccept.getExchangeId())
                        .or(exchange.boardId.eq(exchangeAccept.getRequesterBoardId())
                                .and(exchange.userId.eq(exchangeAccept.getRequesterId()))))
                .execute();
        if (count != 2L) throw new FailAcceptFileException();

        //교환완료
        String date = DateGenerator.getCurrentDate();
        String sql = "INSERT INTO exchange_completion(board_id,date,user_id,requester_board_id) VALUES(?,?,?,?),(?,?,?,?)";
        Query query = em.createNativeQuery(sql)
                .setParameter(1, exchangeAccept.getRequesterBoardId())
                .setParameter(2, date)
                .setParameter(3, userId)
                .setParameter(4, exchangeAccept.getBoardId())
                .setParameter(5, exchangeAccept.getBoardId())
                .setParameter(6, date)
                .setParameter(7, exchangeAccept.getRequesterId())
                .setParameter(8, exchangeAccept.getRequesterBoardId());
        if (query.executeUpdate() != 2) throw new FailAcceptFileException();

        //게시글과 사용자 교환 완료 증가
        sql = "UPDATE board b, user u SET b.exchange_success_count=b.exchange_success_count+1" +
                ", u.exchange_success_count=u.exchange_success_count+1 WHERE b.user_id=u.user_id AND b.board_id=? AND u.user_id = ? OR b.board_id=? AND u.user_id=?";
        query = em.createNativeQuery(sql)
                .setParameter(1, exchangeAccept.getBoardId())
                .setParameter(2, userId)
                .setParameter(3, exchangeAccept.getRequesterBoardId())
                .setParameter(4, exchangeAccept.getRequesterId());
        if (query.executeUpdate() != 4) {
            throw new FailAcceptFileException();
        }


        //exchangeRequest.getApplicantId(); //사용자 고유 아이디로 알림을 주자
    }

    @Transactional(rollbackFor = Exception.class)
    public void refuseExchange(ExchangeRefusal exchangeRefusal, String userId) {
        if (queryFactory.delete(exchange)
                .where(exchange.Id.eq(exchangeRefusal.getExchangeId())
                        .or(exchange.requesterUserId.eq(userId)
                                .and(exchange.userId.eq(exchangeRefusal.getRequesterId()))
                                .and(exchange.boardId.eq(exchangeRefusal.getRequesterBoardId()))
                                .and(exchange.requesterBoardId.eq(exchangeRefusal.getBoardId()))))
                .execute() != 2L) {
            throw new FailRefusalFileException();
        }
    }

    public void updateProfile(String userId, String filePath) {
        System.out.println(filePath);
        if (queryFactory.update(user)
                .set(user.profileImage, filePath)
                .where(user.userId.eq(userId))
                .execute() != 1L) {
            throw new FailUpdateProfileException();
        }
    }

    public String findProfilePath(String userId) {
        String profileImage = queryFactory.select(user.profileImage)
                .from(user)
                .where(user.userId.eq(userId))
                .fetchOne();
        return profileImage;
    }

    public void withdrawalUser(String userId) {
        if (canWithdrawal(userId)) {
            queryFactory.delete(user)
                    .where(user.userId.eq(userId))
                    .execute();
            return;
        }
        throw new FailWithdrawalException();
    }

    private Boolean canWithdrawal(String userId) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -3);
        Date date = new Date(calendar.getTimeInMillis());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Long count = queryFactory.select(exchangeCompletion.count())
                .from(exchangeCompletion)
                .where(getExchangeDate().gt(simpleDateFormat.format(date))
                        .and(exchangeCompletion.userId.eq(userId)))
                .limit(1L)
                .fetchOne();
        return count == 0L;
    }

    private DateTemplate getExchangeDate() {
        return Expressions.dateTemplate(
                String.class,
                "DATE_FORMAT({0}, {1})",
                exchangeCompletion.date,
                ConstantImpl.create("%Y-%m-%d")
        );
    }
}