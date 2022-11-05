package com.ay.exchange.board.repository.querydsl.impl;

import com.ay.exchange.board.dto.query.BoardContentInfo2Dto;
import com.ay.exchange.board.dto.query.BoardContentInfoDto;
import com.ay.exchange.board.dto.response.BoardContentResponse;
import com.ay.exchange.board.entity.Board;
import com.ay.exchange.board.entity.BoardContent;
import com.ay.exchange.board.entity.vo.Category;
import com.ay.exchange.board.repository.querydsl.BoardContentQueryRepository;
import com.ay.exchange.comment.dto.CommentInfoDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static com.ay.exchange.board.entity.QBoard.board;
import static com.ay.exchange.board.entity.QBoardContent.boardContent;
import static com.ay.exchange.comment.entity.QComment.comment;

@RequiredArgsConstructor
public class BoardContentQueryRepositoryImpl implements BoardContentQueryRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public BoardContentResponse findBoardContent(Long boardId, Pageable pageable) {
        List<CommentInfoDto> commentList=new ArrayList<>();

        Long count=queryFactory
                .select(comment.count())
                .from(comment)
                .where(comment.board.id.eq(boardId))
                .fetchOne();

        if(count==0){
            BoardContentInfo2Dto boardContentInfo2Dto=queryFactory
                    .select(Projections.fields(
                            BoardContentInfo2Dto.class,
                            boardContent.content,
                            boardContent.board.title,
                            boardContent.board.writer,
                            boardContent.board.boardCategory,
                            boardContent.board.views,
                            boardContent.board.numberOfFilePages,
                            boardContent.board.numberOfSuccessfulExchanges,
                            boardContent.board.createdDate
                    ))
                    .from(boardContent)
                    .innerJoin(boardContent.board,board)
                    .where(board.id.eq(boardId))
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
                    boardContentInfo2Dto.getCreatedDate()
            );
        }

        List<BoardContentInfoDto>result = getBoardContentInfoDto(boardId,pageable);

        addCommentList(result,commentList);

        BoardContent resultBoardContent=result.get(0).getBoardContent();
        Board resultBoard=result.get(0).getBoard();

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
                resultBoard.getCreatedDate()
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
        for(BoardContentInfoDto boardContentInfo:result){
            commentList.add(new CommentInfoDto(
                    boardContentInfo.getWriter(),
                    boardContentInfo.getContent(),
                    boardContentInfo.getDepth(),
                    boardContentInfo.getGroupId(),
                    boardContentInfo.getCreatedDate()
            ));
        }
    }

    private List<BoardContentInfoDto> getBoardContentInfoDto(Long boardId, Pageable pageable){
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
                        comment.board
                ))
                .from(comment, boardContent)
                .where(boardContent.board.id.eq(boardId))
                //.innerJoin(comment.board.boardContent, board)
                .innerJoin(comment.board, board)
                .where(comment.board.id.eq(boardId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }
}