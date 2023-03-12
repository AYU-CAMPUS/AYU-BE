package com.ay.exchange.comment.service;

import com.ay.exchange.comment.dto.response.CommentInfoDto;
import com.ay.exchange.comment.dto.request.DeleteRequest;
import com.ay.exchange.comment.dto.request.WriteRequest;
import com.ay.exchange.comment.entity.Comment;
import com.ay.exchange.comment.exception.FailDeleteCommentException;
import com.ay.exchange.comment.exception.FailWriteCommentException;
import com.ay.exchange.comment.repository.CommentRepository;
import com.ay.exchange.comment.repository.querydsl.CommentQueryRepository;
import com.ay.exchange.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentQueryRepository commentQueryRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public void writeComment(WriteRequest writeRequest, String email) {
        try {
            commentRepository.save(Comment.builder()
                    .content(writeRequest.getContent())
                    .depth(writeRequest.getDepth())
                    .groupId(writeRequest.getGroupId())
                    .email(email)
                    .boardId(writeRequest.getBoardId())
                    .build());
        } catch (Exception e) {
            throw new FailWriteCommentException();
        }

    }

    public void deleteComment(DeleteRequest deleteRequest, String email) {
        long successDeletedCount = commentQueryRepository.deleteComment(email, deleteRequest);
        if (successDeletedCount != 1L) {
            throw new FailDeleteCommentException();
        }
    }

    private boolean isAuthorized(String accessToken, String userId) {
        return jwtTokenProvider.getUserEmail(accessToken).equals(userId);
    }

    public List<CommentInfoDto> getComments(Long boardId, Integer page) {
        PageRequest pageRequest = PageRequest.of(page > 0 ? (page - 1) : 0, 2,
                Sort.by(Sort.Direction.DESC, "id"));
        return commentQueryRepository.getComments(pageRequest, boardId);
    }
}
