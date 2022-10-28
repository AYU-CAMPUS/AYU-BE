package com.ay.exchange.comment.service;

import com.ay.exchange.board.entity.BoardContent;
import com.ay.exchange.board.exception.NotFoundBoardException;
import com.ay.exchange.board.repository.BoardContentRepository;
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
    private final JwtTokenProvider jwtTokenProvider;

    public void writeComment(WriteRequest writeRequest) {
        BoardContent boardContent = boardContentRepository
                .findById(writeRequest.getBoardContentId())
                .orElseThrow(
                        () -> {
                            throw new NotFoundBoardException();
                        }
                );

        Comment comment = Comment.builder()
                .boardContent(boardContent)
                .writer(writeRequest.getWriter())
                .content(writeRequest.getContent())
                .depth(writeRequest.getDepth())
                .groupId(writeRequest.getGroupId())
                .email(writeRequest.getUserId())
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
