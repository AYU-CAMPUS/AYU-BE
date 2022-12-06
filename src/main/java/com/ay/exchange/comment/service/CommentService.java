package com.ay.exchange.comment.service;

import com.ay.exchange.comment.dto.response.CommentInfoDto;
import com.ay.exchange.comment.dto.request.DeleteRequest;
import com.ay.exchange.comment.dto.request.WriteRequest;
import com.ay.exchange.comment.entity.Comment;
import com.ay.exchange.comment.exception.FailWriteCommentException;
import com.ay.exchange.comment.repository.CommentRepository;
import com.ay.exchange.comment.repository.querydsl.CommentQueryRepository;
import com.ay.exchange.jwt.JwtTokenProvider;
import com.ay.exchange.mypage.exception.FailWithdrawalException;
import com.ay.exchange.user.exception.InvalidUserRoleException;
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

    public void writeComment(WriteRequest writeRequest, String token) {
        Comment comment = Comment.builder()
                .content(writeRequest.getContent())
                .depth(writeRequest.getDepth())
                .groupId(writeRequest.getGroupId())
                .userId(jwtTokenProvider.getUserId(token))
                .boardId(writeRequest.getBoardId())
                .build();
        try {
            commentRepository.save(comment);
        } catch (Exception e) {
            throw new FailWriteCommentException();
        }

    }

    public void deleteComment(DeleteRequest deleteRequest, String token) {
        commentQueryRepository.deleteComment(jwtTokenProvider.getUserId(token), deleteRequest.getCommentId());
    }

    private boolean isAuthorized(String accessToken, String userId) {
        return jwtTokenProvider.getUserId(accessToken).equals(userId);
    }

    public List<CommentInfoDto> getComments(Long boardId, Integer page) {
        PageRequest pageRequest = PageRequest.of(page > 0 ? (page - 1) : 0, 2,
                Sort.by(Sort.Direction.DESC, "id"));

        return commentQueryRepository.getComments(pageRequest, boardId);
    }
}
