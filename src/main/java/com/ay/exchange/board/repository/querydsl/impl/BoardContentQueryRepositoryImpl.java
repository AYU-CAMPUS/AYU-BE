package com.ay.exchange.board.repository.querydsl.impl;

import com.ay.exchange.board.dto.query.BoardContentInfo2Dto;
import com.ay.exchange.board.dto.query.BoardContentInfoDto;
import com.ay.exchange.board.dto.response.ModifiableBoardResponse;
import com.ay.exchange.board.repository.querydsl.BoardContentQueryRepository;
import com.ay.exchange.common.util.Approval;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;

import java.util.*;

import static com.ay.exchange.board.entity.QBoard.board;
import static com.ay.exchange.board.entity.QBoardContent.boardContent;
import static com.ay.exchange.comment.entity.QComment.comment;
import static com.ay.exchange.exchange.entity.QExchange.*;
import static com.ay.exchange.exchange.entity.QExchangeCompletion.exchangeCompletion;
import static com.ay.exchange.user.entity.QUser.user;

@RequiredArgsConstructor
public class BoardContentQueryRepositoryImpl implements BoardContentQueryRepository {
    private final JPAQueryFactory queryFactory;
    private final Long INTERCHANGEABLE = 0L;

    @Override
    public BoardContentInfo2Dto findBoardContentWithNoComments(Long boardId, Pageable pageable, String email) {
        return queryFactory
                .select(Projections.fields(
                        BoardContentInfo2Dto.class,
                        boardContent.content,
                        board.title,
                        user.nickName.as("writer"),
                        board.boardCategory,
                        board.numberOfFilePages,
                        board.exchangeSuccessCount.as("numberOfSuccessfulExchanges"),
                        board.createdDate,
                        exchange.type.coalesce(exchangeCompletion.Id.coalesce(INTERCHANGEABLE)).as("exchangeType"), //null
                        board.email,
                        user.desiredData
                ))
                .from(boardContent)
                .leftJoin(exchange)
                .on(boardContent.board.id.eq(exchange.boardId)
                        .and(exchange.requesterEmail.eq(email)))
                .innerJoin(boardContent.board, board)
                .innerJoin(user)
                .on(board.email.eq(user.email))
                .leftJoin(exchangeCompletion)
                .on(exchangeCompletion.requesterBoard.eq(boardContent.board)
                        .and(exchangeCompletion.email.eq(email)))
                .where(board.id.eq(boardId)
                        .and(boardContent.board.id.eq(boardId))
                        .and(board.approval.eq(Approval.AGREE.getApproval())))
                .fetchOne();
    }

    @Override
    public List<BoardContentInfoDto> findBoardContentWithComments(Long boardId, Pageable pageable, String email){
        return queryFactory
                .select(Projections.constructor(
                        BoardContentInfoDto.class,
                        comment.id.as("commentId"),
                        user.nickName.as("writer"),
                        comment.content,
                        comment.depth,
                        comment.groupId.longValue(),
                        comment.createdDate,
                        boardContent,
                        board,
                        exchange.type.coalesce(exchangeCompletion.Id.coalesce(INTERCHANGEABLE)).as("exchangeType"),
                        user.profileImage.coalesce("default.svg"),
                        board.user.nickName,
                        user.desiredData
                ))
                .from(comment)
                .innerJoin(user)
                .on(comment.email.eq(user.email))
                .innerJoin(board)
                .on(comment.boardId.eq(board.id))
                .innerJoin(boardContent)
                .on(comment.board.eq(boardContent.board))
                .leftJoin(exchange)
                .on(board.id.eq(exchange.boardId)
                        .and(exchange.email.eq(email))
                        .or(board.id.eq(exchange.requesterBoardId)
                                .and(exchange.requesterEmail.eq(email))))
                .leftJoin(exchangeCompletion)
                .on(exchangeCompletion.requesterBoardId.eq(board.id)
                        .and(exchangeCompletion.email.eq(email)))
                .where(comment.board.id.eq(boardId)
                        .and(board.id.eq(boardId))
                        .and(boardContent.board.id.eq(boardId))
                        .and(board.approval.eq(Approval.AGREE.getApproval())))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public ModifiableBoardResponse findModifiableBoard(String email, Long boardId) {
        return queryFactory.select(Projections.fields(
                        ModifiableBoardResponse.class,
                        board.title,
                        board.boardCategory,
                        board.numberOfFilePages,
                        board.originalFileName,
                        boardContent.content
                ))
                .from(board)
                .innerJoin(boardContent)
                .on(board.eq(boardContent.board))
                .where(board.id.eq(boardId)
                        .and(board.email.eq(email))
                        .and(board.approval.eq(Approval.AGREE.getApproval()))
                )
                .fetchOne();
    }

}
