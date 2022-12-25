package com.ay.exchange.management.repository;

import com.ay.exchange.common.util.Approval;
import com.ay.exchange.management.dto.request.BoardIdRequest;
import com.ay.exchange.management.dto.request.SuspensionRequest;
import com.ay.exchange.management.dto.response.BoardInfo;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.ay.exchange.board.entity.QBoard.board;
import static com.ay.exchange.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class ManagementRepository {
    private final JPAQueryFactory queryFactory;

    public Long findRequestBoardTotal() {
        return queryFactory.select(board.count())
                .from(board)
                .where(board.approval.eq(Approval.WAITING.getApproval()))
                .fetchOne();
    }

    public List<BoardInfo> findRequestBoards(PageRequest pageRequest) {
        return queryFactory.select(Projections.fields(
                        BoardInfo.class,
                        board.id,
                        board.boardCategory,
                        board.title,
                        user.nickName.as("writer"),
                        board.createdDate.as("date")
                ))
                .from(board)
                .innerJoin(user)
                .on(board.email.eq(user.email))
                .where(board.approval.eq(Approval.WAITING.getApproval()))
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .fetch();
    }

    public boolean updateBoardApproval(BoardIdRequest boardIdRequest) {
        return queryFactory.update(board)
                .set(board.approval, Approval.AGREE.getApproval())
                .where(board.id.eq(boardIdRequest.getBoardId()))
                .execute() == 1L;
    }

    public boolean deleteBoard(BoardIdRequest boardIdRequest) {
        return queryFactory.delete(board)
                .where(board.id.eq(boardIdRequest.getBoardId()))
                .execute() == 1L;
    }

    public long updateUserSuspensionByEmail(SuspensionRequest suspensionRequest) {
        return queryFactory.update(user)
                .set(user.suspendedDate, suspensionRequest.getDate())
                .where(user.email.eq(suspensionRequest.getEmail()))
                .execute();
    }
}
