package com.ay.exchange.board.repository.querydsl.impl;

import com.ay.exchange.board.dto.query.BoardContentInfo2Dto;
import com.ay.exchange.board.dto.query.BoardContentInfoDto;
import com.ay.exchange.board.dto.response.BoardContentResponse;
import com.ay.exchange.board.dto.response.ModifiableBoardResponse;
import com.ay.exchange.board.entity.Board;
import com.ay.exchange.board.entity.BoardContent;
import com.ay.exchange.board.repository.querydsl.BoardContentQueryRepository;
import com.ay.exchange.comment.dto.response.CommentInfoDto;
import com.ay.exchange.common.util.Approval;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.DateTemplate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.util.*;
import java.util.stream.Collectors;

import static com.ay.exchange.board.entity.QBoard.board;
import static com.ay.exchange.board.entity.QBoardContent.boardContent;
import static com.ay.exchange.comment.entity.QComment.comment;
import static com.ay.exchange.common.util.DateUtil.getAvailableDate;
import static com.ay.exchange.exchange.entity.QExchange.*;
import static com.ay.exchange.exchange.entity.QExchangeCompletion.exchangeCompletion;
import static com.ay.exchange.user.entity.QUser.user;

@RequiredArgsConstructor
public class BoardContentQueryRepositoryImpl implements BoardContentQueryRepository {
    private final JPAQueryFactory queryFactory;
    private final EntityManager em;
    private static final String SEPARATOR = ";";
    private final int BOARD_OWNER = -1;
    private final Long INTERCHANGEABLE = 0L;

    @Override
    public BoardContentResponse findBoardContent(Long boardId, Pageable pageable, String email) {
        //infrastructure에 복잡한 로직이 들어간 것 같다. 일단 원하는 대로 동작하니 나중에 리팩터링하는 걸로......
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

            return new BoardContentResponse(
                    count,
                    commentList,
                    boardContentInfo2Dto.getContent(),
                    boardContentInfo2Dto.getTitle(),
                    boardContentInfo2Dto.getWriter(),
                    boardContentInfo2Dto.getBoardCategory(),
                    boardContentInfo2Dto.getNumberOfFilePages(),
                    boardContentInfo2Dto.getNumberOfSuccessfulExchanges(),
                    boardContentInfo2Dto.getCreatedDate(),
                    boardContentInfo2Dto.getEmail().equals(email) ? BOARD_OWNER : (boardContentInfo2Dto.getExchangeType() >= 1 ? 1 : boardContentInfo2Dto.getExchangeType()), //-1이면 내가 쓴 글임,
                    splitDesiredData(boardContentInfo2Dto.getDesiredData())
            );
        }

        List<BoardContentInfoDto> result = getBoardContentInfoDto(boardId, pageable, email);

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
                resultBoard.getNumberOfFilePages(),
                resultBoard.getExchangeSuccessCount(),
                resultBoard.getCreatedDate(),
                resultBoard.getEmail().equals(email) ? BOARD_OWNER : (result.get(0).getExchangeType() >= 1 ? 1 : result.get(0).getExchangeType()),
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

    private List<BoardContentInfoDto> getBoardContentInfoDto(Long boardId, Pageable pageable, String email) {
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

    public ModifiableBoardResponse findModifiableBoard(String date, String email, Long boardId) {
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

    @Override
    public Boolean canDeleted(String email, Long boardId) {
        return isBoardOwner(email, boardId)
                && checkExchangeCompletionDate(getAvailableDate(), email, boardId);
    }

    private Boolean isBoardOwner(String email, Long boardId) {
        Long count = queryFactory.select(board.count())
                .from(board)
                .where(board.email.eq(email)
                        .and(board.id.eq(boardId))
                        .and(board.approval.eq(Approval.AGREE.getApproval()))
                )
                .limit(1L)
                .fetchOne();
        return count == 1L;
    }

    @Override
    public Boolean checkExchangeDate(String date, Long boardId) {
        Long count = queryFactory.select(exchange.count())
                .from(exchange)
                .where(getExchangeDate().gt(date)
                        .and(exchange.board.id.eq(boardId)
                                .or(exchange.requesterBoardId.eq(boardId))))
                .limit(1L)
                .fetchOne();
        return count == 0L;
    }

    @Override
    public Boolean checkExchangeCompletionDate(String date, String email, Long boardId) {
        Long count = queryFactory.select(exchangeCompletion.count())
                .from(exchangeCompletion)
                .where(getExchangeCompletionDate().gt(date)
                        .and(exchangeCompletion.boardId.eq(boardId))
                        .and(exchangeCompletion.email.eq(email)))
                .limit(1L)
                .fetchOne();
        return count == 0L;
    }

    private DateTemplate getExchangeDate() {
        return Expressions.dateTemplate(
                String.class,
                "DATE_FORMAT({0}, {1})",
                exchange.createdDate,
                ConstantImpl.create("%Y-%m-%d")
        );
    }

    private DateTemplate getExchangeCompletionDate() {
        return Expressions.dateTemplate(
                String.class,
                "DATE_FORMAT({0}, {1})",
                exchangeCompletion.date,
                ConstantImpl.create("%Y-%m-%d")
        );
    }
}
