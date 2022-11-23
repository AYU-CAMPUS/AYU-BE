package com.ay.exchange.board.repository.querydsl.impl;

import com.ay.exchange.board.dto.query.BoardContentInfo2Dto;
import com.ay.exchange.board.dto.query.BoardContentInfoDto;
import com.ay.exchange.board.dto.response.BoardContentResponse;
import com.ay.exchange.board.entity.Board;
import com.ay.exchange.board.entity.BoardContent;
import com.ay.exchange.board.repository.querydsl.BoardContentQueryRepository;
import com.ay.exchange.comment.dto.response.CommentInfoDto;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.DateTemplate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.ay.exchange.board.entity.QBoard.board;
import static com.ay.exchange.board.entity.QBoardContent.boardContent;
import static com.ay.exchange.comment.entity.QComment.comment;
import static com.ay.exchange.exchange.entity.QExchange.*;
import static com.ay.exchange.user.entity.QUser.user;

@RequiredArgsConstructor
public class BoardContentQueryRepositoryImpl implements BoardContentQueryRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public BoardContentResponse findBoardContent(Long boardId, Pageable pageable, String userId) {
        List<CommentInfoDto> commentList = new ArrayList<>();

        Long count = queryFactory
                .select(comment.count())
                .from(comment)
                .where(comment.board.id.eq(boardId))
                .fetchOne();

        if (count == 0) {
            BoardContentInfo2Dto boardContentInfo2Dto = queryFactory
                    .select(Projections.fields(
                            BoardContentInfo2Dto.class,
                            boardContent.content,
                            boardContent.board.title,
                            boardContent.board.writer,
                            boardContent.board.boardCategory,
                            boardContent.board.views,
                            boardContent.board.numberOfFilePages,
                            boardContent.board.numberOfSuccessfulExchanges,
                            boardContent.board.createdDate,
                            exchange.type.coalesce(0).as("exchangeType"), //null
                            boardContent.board.userId
                    ))
                    .from(boardContent)
                    .leftJoin(exchange)
                    .on(boardContent.board.id.eq(exchange.board.id)
                            .and(exchange.user.userId.eq(userId)))
                    .innerJoin(boardContent.board, board)
                    .where(board.id.eq(boardId)
                            .and(boardContent.board.id.eq(boardId)))
                    .fetchOne();

            return new BoardContentResponse(
                    count,
                    commentList,
                    boardContentInfo2Dto.getContent(),
                    boardContentInfo2Dto.getTitle(),
                    boardContentInfo2Dto.getWriter(),
                    boardContentInfo2Dto.getBoardCategory(),
                    boardContentInfo2Dto.getViews(),
                    boardContentInfo2Dto.getNumberOfFilePages(),
                    boardContentInfo2Dto.getNumberOfSuccessfulExchanges(),
                    boardContentInfo2Dto.getCreatedDate(),
                    boardContentInfo2Dto.getUserId().equals(userId) ? -1 : boardContentInfo2Dto.getExchangeType() //-1이면 내가 쓴 글임
            );
        }

        List<BoardContentInfoDto> result = getBoardContentInfoDto(boardId, pageable, userId);

        addCommentList(result, commentList);

        BoardContent resultBoardContent = result.get(0).getBoardContent();

        Board resultBoard = result.get(0).getBoard();

        return new BoardContentResponse(
                count,
                commentList,
                resultBoardContent.getContent(),
                resultBoard.getTitle(),
                resultBoard.getWriter(),
                resultBoard.getBoardCategory(),
                resultBoard.getViews(),
                resultBoard.getNumberOfFilePages(),
                resultBoard.getNumberOfSuccessfulExchanges(),
                resultBoard.getCreatedDate(),
                resultBoard.getUserId().equals(userId) ? -1 : result.get(0).getExchangeType()
        );

//        Map<Long, BoardContentInfoDto> boardContentInfoDto = queryFactory
//                .from(comment)
//                .innerJoin(comment.boardContent, boardContent)
//                .where(comment.boardContent.id.eq(boardId))
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .transform(
//                        groupBy(boardContent.id).as(new QBoardContentInfoDto(
//                                boardContent.content,
//                                list(
//                                        new QCommentInfoDto(
//                                                comment.writer,
//                                                comment.content,
//                                                comment.depth,
//                                                comment.groupId.longValue(),
//                                                comment.createdDate
//                                        )
//                                )
//                        ))
//                );
//        List<BoardContentInfoDto> comments=boardContentInfoDto.keySet().stream()
//                .map(boardContentInfoDto::get)
//                .collect(Collectors.toList());
//        System.out.println("LASTSIZE:"+ comments.get(0).getCommentInfoList().size());
// return result.get(0);
    }

    private void addCommentList(List<BoardContentInfoDto> result, List<CommentInfoDto> commentList) {
        for (BoardContentInfoDto boardContentInfo : result) {
            commentList.add(new CommentInfoDto(
                    boardContentInfo.getWriter(),
                    boardContentInfo.getContent(),
                    boardContentInfo.getDepth(),
                    boardContentInfo.getGroupId(),
                    boardContentInfo.getCreatedDate(),
                    boardContentInfo.getProfileImage()
            ));
        }
    }

    private List<BoardContentInfoDto> getBoardContentInfoDto(Long boardId, Pageable pageable, String userId) {
        return queryFactory
                .select(Projections.constructor(
                        BoardContentInfoDto.class,
                        comment.writer,
                        comment.content,
                        comment.depth,
                        comment.groupId.longValue(),
                        comment.createdDate,
                        //comment.board.boardContent,
                        boardContent,
                        board,
                        exchange.type.coalesce(0).as("exchangeType"),
                        user.profileImage.coalesce("default.svg")
                ))
                .from(comment)
                .innerJoin(user)
                .on(comment.userId.eq(user.userId))
                .leftJoin(exchange)
                .on(comment.board.id.eq(exchange.board.id)
                        .and(exchange.user.userId.eq(userId)))
                .leftJoin(boardContent)
                .on(comment.board.id.eq(boardContent.board.id))
                .innerJoin(comment.board, board)
                .where(comment.board.id.eq(boardId)
                        .and(board.id.eq(boardId))
                        .and(boardContent.board.id.eq(boardId)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    public Boolean isModifiable() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -3);
        Date date = new Date(calendar.getTimeInMillis());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Long count = queryFactory.select(exchange.count())
                .from(exchange)
                .where(getExchangeDate().gt(simpleDateFormat.format(date))
                        .or(exchange.type.eq(1))
                        .and(exchange.board.id.eq(2L)))
                .limit(1L)
                .fetchOne();
        return count == 0;
    }

    private DateTemplate getExchangeDate() {
        return Expressions.dateTemplate(
                String.class,
                "DATE_FORMAT({0}, {1})",
                exchange.createdDate,
                ConstantImpl.create("%Y-%m-%d")
        );
    }
}
