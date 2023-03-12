package com.ay.exchange.comment.facade;

import com.ay.exchange.comment.dto.request.DeleteRequest;
import com.ay.exchange.comment.dto.request.WriteRequest;
import com.ay.exchange.comment.service.CommentService;
import com.ay.exchange.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentFacade {
    private final JwtTokenProvider jwtTokenProvider;
    private final CommentService commentService;

    @Transactional(rollbackFor = Exception.class)
    public void writeComment(WriteRequest writeRequest, String token) {
        commentService.writeComment(writeRequest, jwtTokenProvider.getUserEmail(token));
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(DeleteRequest deleteRequest, String token) {
        commentService.deleteComment(deleteRequest, jwtTokenProvider.getUserEmail(token));
    }
}
