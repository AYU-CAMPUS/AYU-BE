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
import java.util.*;
import java.util.stream.Collectors;

import static com.ay.exchange.board.entity.QBoard.board;
import static com.ay.exchange.board.entity.QBoardContent.boardContent;
import static com.ay.exchange.comment.entity.QComment.comment;
import static com.ay.exchange.exchange.entity.QExchange.*;
import static com.ay.exchange.exchange.entity.QExchangeCompletion.exchangeCompletion;
import static com.ay.exchange.user.entity.QUser.user;

@RequiredArgsConstructor
public class BoardContentQueryRepositoryImpl implements BoardContentQueryRepository {
    private final JPAQueryFactory queryFactory;
    private static final String SEPARATOR = ";";

    @Override
    public BoardContentResponse findBoardContent(Long boardId, Pageable pageable, String userId) {
        Long count = queryFactory
                .select(comment.count())
                .from(comment)
                .where(comment.board.id.eq(boardId))
                .fetchOne();

        List<CommentInfoDto> commentList = new ArrayList<>();
        if (count == 0) {
            //SELECT ex.type FROM board_content bc LEFT JOIN exchange ex ON (bc.board_id=ex.board_id AND ex.user_id='ksiisk99') OR (bc.board_id=ex.requester_board_id AND ex.requester_user_id='ksiisk99') INNER JOIN board b LEFT JOIN exchange_completion ec ON ec.board_id=bc.board_id AND ec.user_id='ksiisk99' WHERE b.board_id=3 AND bc.board_id=3;
            BoardContentInfo2Dto boardContentInfo2Dto = queryFactory
                    .select(Projections.fields(
                            BoardContentInfo2Dto.class,
                            boardContent.content,
                            board.title,
                            user.nickName.as("writer"),
                            board.boardCategory,
                            board.views,
                            board.numberOfFilePages,
                            board.exchangeSuccessCount.as("numberOfSuccessfulExchanges"),
                            board.createdDate,
                            exchange.type.coalesce(exchangeCompletion.Id.coalesce(0L)).as("exchangeType"), //null
                            board.userId,
                            user.desiredData
                    ))
                    .from(boardContent)
                    .leftJoin(exchange)
                    .on(boardContent.board.id.eq(exchange.boardId)
                            .and(exchange.userId.eq(userId))
                            .or(boardContent.board.id.eq(exchange.requesterBoardId)
                                    .and(exchange.requesterUserId.eq(userId))))
                    .innerJoin(boardContent.board, board)
                    .innerJoin(user)
                    .on(board.userId.eq(user.userId))
                    .leftJoin(exchangeCompletion)
                    .on(exchangeCompletion.board.eq(boardContent.board)
                            .and(exchangeCompletion.userId.eq(userId)))
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
                    boardContentInfo2Dto.getUserId().equals(userId) ? -1 : boardContentInfo2Dto.getExchangeType(), //-1이면 내가 쓴 글임,
                    splitDesiredData(boardContentInfo2Dto.getDesiredData())
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
                result.get(0).getNickName(),
                resultBoard.getBoardCategory(),
                resultBoard.getViews(),
                resultBoard.getNumberOfFilePages(),
                resultBoard.getExchangeSuccessCount(),
                resultBoard.getCreatedDate(),
                resultBoard.getUserId().equals(userId) ? -1 : result.get(0).getExchangeType(),
                splitDesiredData(result.get(0).getDesiredData())
        );

    }

    private List<String> splitDesiredData(String desiredData) {
        return Arrays.stream(desiredData.split(SEPARATOR))
                .collect(Collectors.toList());
    }

    private void addCommentList(List<BoardContentInfoDto> result, List<CommentInfoDto> commentList) {
        for (BoardContentInfoDto boardContentInfo : result) {
            commentList.add(new CommentInfoDto(
                    boardContentInfo.getCommentId(),
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
                        comment.id.as("commentId"),
                        user.nickName.as("writer"),
                        comment.content,
                        comment.depth,
                        comment.groupId.longValue(),
                        comment.createdDate,
                        boardContent,
                        board,
                        exchange.type.coalesce(exchangeCompletion.Id.coalesce(0L)).as("exchangeType"),
                        user.profileImage.coalesce("default.svg"),
                        board.user.nickName,
                        user.desiredData
                ))
                .from(comment)
                .innerJoin(user)
                .on(comment.userId.eq(user.userId))
                .innerJoin(board)
                .on(comment.boardId.eq(board.id))
                .innerJoin(boardContent)
                .on(comment.board.eq(boardContent.board))
                .leftJoin(exchange)
                .on(board.id.eq(exchange.boardId)
                        .and(exchange.userId.eq(userId))
                        .or(board.id.eq(exchange.requesterBoardId)
                                .and(exchange.requesterUserId.eq(userId))))
                .leftJoin(exchangeCompletion)
                .on(exchangeCompletion.requesterBoardId.eq(board.id)
                        .and(exchangeCompletion.userId.eq(userId)))
                .where(comment.board.id.eq(boardId)
                        .and(board.id.eq(boardId))
                        .and(boardContent.board.id.eq(boardId)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    public Boolean isModifiable() {//추후 boardId추가
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -3);
        Date date = new Date(calendar.getTimeInMillis());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Long count = queryFactory.select(exchange.count())
                .from(exchange)
                .where(getExchangeDate().gt(simpleDateFormat.format(date))
                        .or(exchange.type.eq(-2))
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
