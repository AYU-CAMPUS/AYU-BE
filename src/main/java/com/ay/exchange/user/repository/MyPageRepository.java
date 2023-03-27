package com.ay.exchange.user.repository;

import com.ay.exchange.common.util.ExchangeType;
import com.ay.exchange.user.dto.*;
import com.ay.exchange.user.dto.request.ExchangeAccept;
import com.ay.exchange.user.dto.request.UserInfoRequest;
import com.ay.exchange.user.dto.response.LoginNotificationResponse;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

import java.util.*;

import static com.ay.exchange.user.entity.QUser.user;
import static com.ay.exchange.board.entity.QBoard.board;
import static com.ay.exchange.exchange.entity.QExchange.exchange;
import static com.querydsl.core.group.GroupBy.*;

@Repository
@RequiredArgsConstructor
public class MyPageRepository {
    private final JPAQueryFactory queryFactory;
    private final EntityManager em;
    private final String SEPARATOR = ";";

    public MyPageInfo getMyPage(String email) {
        return queryFactory.from(user)
                .leftJoin(board)
                .on(board.email.eq(email)
                        .and(board.approval.eq(1)))
                .leftJoin(exchange)
                .on(exchange.email.eq(email).and(exchange.type.eq(ExchangeType.ACCEPT.getType())))
                .where(user.email.eq(email))
                .transform(
                        groupBy(user.email)
                                .as(Projections.fields(
                                        MyPageInfo.class,
                                        user.nickName,
                                        user.profileImage.coalesce("default.svg").as("profileImage"),
                                        user.exchangeSuccessCount,
                                        set(board.id).as("myDataCount"),
                                        set(exchange.Id).as("exchangeRequests"),
                                        user.desiredData
                                ))
                ).get(email);
    }

    public boolean updateProfile(String email, String filePath) {
        return queryFactory.update(user)
                .set(user.profileImage, filePath)
                .where(user.email.eq(email))
                .execute() != 1L;
    }

    public String findProfilePath(String email) {
        return queryFactory.select(user.profileImage)
                .from(user)
                .where(user.email.eq(email))
                .fetchOne();
    }

    public void withdrawalUser(String email) {
        queryFactory.delete(user)
                .where(user.email.eq(email))
                .execute();
    }

    public boolean updateUserInfo(String email, UserInfoRequest userInfoRequest) {
        return queryFactory.update(user)
                .set(user.nickName, userInfoRequest.getNickName())
                .set(user.desiredData, mergeStrings(userInfoRequest.getDesiredData()))
                .where(user.email.eq(email))
                .execute() != 1L;
    }

    public LoginNotificationResponse findUserNotificationByEmail(String userEmail) {
        return queryFactory.select(Projections.fields(
                        LoginNotificationResponse.class,
                        user.nickName,
                        exchange.count().as("numberOfExchange"),
                        user.suspendedDate,
                        user.suspendedReason))
                .from(user)
                .leftJoin(exchange)
                .on(exchange.email.eq(user.email))
                .where(user.email.eq(userEmail))
                .limit(100L)
                .fetchOne();
    }

    public void updateUserSuspendedDate(String email) {
        queryFactory.update(user)
                .setNull(user.suspendedDate)
                .setNull(user.suspendedReason)
                .where(user.email.eq(email))
                .execute();
    }

    private String mergeStrings(List<String> desiredData) {
        return StringUtils.join(desiredData, SEPARATOR);
    }

    public int increaseExchangeCompletion(ExchangeAccept exchangeAccept, String email) {
        //게시글과 사용자 교환 완료 증가
        String sql = "UPDATE board b, user u SET b.exchange_success_count=b.exchange_success_count+1" +
                ", u.exchange_success_count=u.exchange_success_count+1 WHERE (b.board_id=? AND u.email = ?) OR (b.board_id=? AND u.email=?)";
        return em.createNativeQuery(sql)
                .setParameter(1, exchangeAccept.getBoardId())
                .setParameter(2, email)
                .setParameter(3, exchangeAccept.getRequesterBoardId())
                .setParameter(4, exchangeAccept.getRequesterId())
                .executeUpdate();
    }
}