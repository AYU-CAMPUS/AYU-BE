package com.ay.exchange.comment.service;

import com.ay.exchange.board.entity.Board;
import com.ay.exchange.board.exception.NotFoundBoardException;
import com.ay.exchange.board.repository.BoardContentRepository;
import com.ay.exchange.board.repository.BoardRepository;
import com.ay.exchange.comment.dto.request.DeleteRequest;
import com.ay.exchange.comment.dto.request.WriteRequest;
import com.ay.exchange.comment.entity.Comment;
import com.ay.exchange.comment.repository.CommentRepository;
import com.ay.exchange.jwt.JwtTokenProvider;
import com.ay.exchange.user.exception.InvalidUserRoleException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final BoardContentRepository boardContentRepository;
    private final BoardRepository boardRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public void writeComment(WriteRequest writeRequest) {
        Board board = boardRepository
                .findById(writeRequest.getBoardId())
                .orElseThrow(
                        () -> {
                            throw new NotFoundBoardException();
                        }
                );

        Comment comment = Comment.builder()
                .board(board)
                .writer(writeRequest.getWriter())
                .content(writeRequest.getContent())
                .depth(writeRequest.getDepth())
                .groupId(writeRequest.getGroupId())
                .userId(writeRequest.getUserId())
                .build();
        commentRepository.save(comment);
    }

    public void deleteComment(DeleteRequest deleteRequest, String accessToken) {

        if (isAuthorized(accessToken, deleteRequest.getUserId())) { //추후 @PreAuthorize로 해결하자
            if (deleteRequest.getDepth()) //자식 댓글
                commentRepository.deleteById(deleteRequest.getCommentId());
            else //부모 댓글
                commentRepository.deleteAllByGroupId(deleteRequest.getGroupId());
        } else {
            throw new InvalidUserRoleException();
        }
    }

    private boolean isAuthorized(String accessToken, String userId) {
        return jwtTokenProvider.getUserId(accessToken).equals(userId);
    }
}
