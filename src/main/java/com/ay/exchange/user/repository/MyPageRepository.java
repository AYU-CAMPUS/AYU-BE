package com.ay.exchange.user.repository;


import com.ay.exchange.common.util.Approval;
import com.ay.exchange.common.util.DateGenerator;
import com.ay.exchange.user.dto.*;
import com.ay.exchange.user.dto.request.ExchangeAccept;
import com.ay.exchange.user.dto.request.ExchangeRefusal;
import com.ay.exchange.user.dto.request.UserInfoRequest;
import com.ay.exchange.user.dto.response.DownloadableResponse;
import com.ay.exchange.user.dto.response.ExchangeResponse;
import com.ay.exchange.user.dto.response.LoginNotificationResponse;
import com.ay.exchange.user.dto.response.MyDataResponse;

import com.ay.exchange.user.exception.*;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.DateTemplate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.ay.exchange.exchange.entity.QExchangeCompletion.exchangeCompletion;
import static com.ay.exchange.user.entity.QUser.user;
import static com.ay.exchange.board.entity.QBoard.board;
import static com.ay.exchange.exchange.entity.QExchange.exchange;
import static com.querydsl.core.group.GroupBy.*;
import static org.hibernate.sql.InFragment.NULL;

@Repository
@RequiredArgsConstructor
public class MyPageRepository {
    private final JPAQueryFactory queryFactory;
    private final EntityManager em;
    private final String SEPARATOR = ";";

    public MyPageInfo getMyPage(String email) {
        return queryFactory.from(user)
                .leftJoin(board)
                .on(board.email.eq(email))
                .leftJoin(exchange)
                .on(exchange.email.eq(email).and(exchange.type.eq(-2)))
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

    public Long getDonwloadableCount(String email) {
        return queryFactory.select(exchangeCompletion.count())
                .from(exchangeCompletion)
                .where(exchangeCompletion.email.eq(email))
                .fetchOne();
    }

//    @Transactional(rollbackFor = Exception.class)
//    public Boolean updatePassword(String userId, String password) {
//        return queryFactory.update(user)
//                .where(user.userId.eq(userId))
//                .set(user.password, password)
//                .execute() == 1L;
//    }

    public MyDataResponse getMyData(PageRequest pageRequest, String email) {
        Long count = queryFactory.select(board.count())
                .from(board)
                .where(board.email.eq(email))
                .fetchOne();

        List<MyDataInfo> myDataInfos = queryFactory
                .select(Projections.fields(
                        MyDataInfo.class,
                        board.createdDate,
                        board.title,
                        board.id.as("boardId"),
                        board.boardCategory.category
                ))
                .from(board)
                .where(board.email.eq(email)
                        .and(board.approval.eq(Approval.AGREE.getApproval())))
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .orderBy(board.id.desc())
                .fetch();

        return new MyDataResponse(count, myDataInfos);
    }

    public DownloadableResponse getDownloadable(PageRequest pageRequest, String email) {
        Long count = queryFactory.select(exchangeCompletion.count())
                .from(exchangeCompletion)
                .where(exchangeCompletion.email.eq(email))
                .fetchOne();

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

    public FilePathInfo getFilePath(Long requesterBoardId, String email) {
        FilePathInfo filePathInfo = queryFactory.select(Projections.fields(
                        FilePathInfo.class,
                        board.email,
                        board.filePath
                ))
                .from(exchangeCompletion)
                .innerJoin(board)
                .on(board.id.eq(exchangeCompletion.requesterBoardId))
                .where(exchangeCompletion.requesterBoardId.eq(requesterBoardId)
                        .and(exchangeCompletion.email.eq(email)))
                .fetchOne();
        return filePathInfo;
    }

    public ExchangeResponse getExchanges(PageRequest pageRequest, String email) {
        Long count = queryFactory.select(exchange.count())
                .from(exchange)
                .where(exchange.email.eq(email))
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
                        exchange.requesterEmail.as("requesterId"),
                        board.title,
                        exchange.boardId,
                        board.id.as("requesterBoardId")
                ))
                .from(exchange)
                .innerJoin(user)
                .on(exchange.requesterEmail.eq(user.email))
                .innerJoin(board)
                .on(exchange.requesterBoardId.eq(board.id))
                .where(exchange.email.eq(email))
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .orderBy(exchange.Id.desc())
                .fetch();

        return new ExchangeResponse(count, exchangeInfos);
    }

    public void acceptExchange(ExchangeAccept exchangeAccept, String email) {
        //교환신청삭제
        Long count = queryFactory.delete(exchange)
                .where(exchange.Id.eq(exchangeAccept.getExchangeId())
                        .or(exchange.boardId.eq(exchangeAccept.getRequesterBoardId())
                                .and(exchange.email.eq(exchangeAccept.getRequesterId()))))
                .execute();
        if (count != 2L) throw new FailAcceptFileException();

        //교환완료
        String date = DateGenerator.getCurrentDate();
        String sql = "INSERT INTO exchange_completion(board_id,date,email,requester_board_id) VALUES(?,?,?,?),(?,?,?,?)";
        Query query = em.createNativeQuery(sql)
                .setParameter(1, exchangeAccept.getRequesterBoardId())
                .setParameter(2, date)
                .setParameter(3, exchangeAccept.getRequesterId())
                .setParameter(4, exchangeAccept.getBoardId())
                .setParameter(5, exchangeAccept.getBoardId())
                .setParameter(6, date)
                .setParameter(7, email)
                .setParameter(8, exchangeAccept.getRequesterBoardId());
        if (query.executeUpdate() != 2) throw new FailAcceptFileException();

        //게시글과 사용자 교환 완료 증가
        sql = "UPDATE board b, user u SET b.exchange_success_count=b.exchange_success_count+1" +
                ", u.exchange_success_count=u.exchange_success_count+1 WHERE b.email=u.email AND b.board_id=? AND u.email = ? OR b.board_id=? AND u.email=?";
        query = em.createNativeQuery(sql)
                .setParameter(1, exchangeAccept.getBoardId())
                .setParameter(2, email)
                .setParameter(3, exchangeAccept.getRequesterBoardId())
                .setParameter(4, exchangeAccept.getRequesterId());
        if (query.executeUpdate() != 4) {
            throw new FailAcceptFileException();
        }


        //exchangeRequest.getApplicantId(); //사용자 고유 아이디로 알림을 주자
    }

    @Transactional(rollbackFor = Exception.class)
    public void refuseExchange(ExchangeRefusal exchangeRefusal, String email) {
        if (queryFactory.delete(exchange)
                .where(exchange.Id.eq(exchangeRefusal.getExchangeId())
                        .or(exchange.requesterEmail.eq(email)
                                .and(exchange.email.eq(exchangeRefusal.getRequesterId()))
                                .and(exchange.boardId.eq(exchangeRefusal.getRequesterBoardId()))
                                .and(exchange.requesterBoardId.eq(exchangeRefusal.getBoardId()))))
                .execute() != 2L) {
            throw new FailRefusalFileException();
        }
    }

    public void updateProfile(String email, String filePath) {
        System.out.println(filePath);
        if (queryFactory.update(user)
                .set(user.profileImage, filePath)
                .where(user.email.eq(email))
                .execute() != 1L) {
            throw new FailUpdateProfileException();
        }
    }

    public String findProfilePath(String email) {
        String profileImage = queryFactory.select(user.profileImage)
                .from(user)
                .where(user.email.eq(email))
                .fetchOne();
        return profileImage;
    }

    public void withdrawalUser(String email) {
        if (canWithdrawal(email)) {
            queryFactory.delete(user)
                    .where(user.email.eq(email))
                    .execute();
            return;
        }
        throw new FailWithdrawalException();
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateUserInfo(String email, UserInfoRequest userInfoRequest) {
        if (checkExistsByNickName(email, userInfoRequest.getNickName())) {
            throw new DuplicateNickNameException();
        }

        if (queryFactory.update(user)
                .set(user.nickName, userInfoRequest.getNickName())
                .set(user.desiredData, mergeStrings(userInfoRequest.getDesiredData()))
                .where(user.email.eq(email))
                .execute() != 1L) {
            throw new FailUpdateUserInfoException();
        }

    }

    public LoginNotificationResponse findUserNotificiatonByEmail(String userEmail) {
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

    private boolean checkExistsByNickName(String email, String nickName) {
        if (queryFactory.select(user.count())
                .from(user)
                .where(user.email.ne(email)
                        .and(user.nickName.eq(nickName)))
                .fetchOne() == 1L) {
            return true;
        }
        return false;
    }

    private String mergeStrings(List<String> desiredData) {
        return StringUtils.join(desiredData, SEPARATOR);
    }

    private Boolean canWithdrawal(String email) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -3);
        Date date = new Date(calendar.getTimeInMillis());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Long count = queryFactory.select(exchangeCompletion.count())
                .from(exchangeCompletion)
                .where(getExchangeDate().gt(simpleDateFormat.format(date))
                        .and(exchangeCompletion.email.eq(email)))
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