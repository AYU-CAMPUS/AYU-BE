package com.ay.exchange.board.controller;

import com.ay.exchange.board.dto.request.DeleteRequest;
import com.ay.exchange.board.dto.response.BoardContentResponse;
import com.ay.exchange.board.service.BoardContentService;
import com.ay.exchange.comment.entity.Comment;
import com.ay.exchange.comment.repository.CommentRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardContentController {
    private final BoardContentService boardContentService;
    private final CommentRepository commentRepository;

    @Operation(summary = "게시글 삭제", description = "게시글 삭제(대댓글과 파일이 삭제됨)"
            , parameters = {@Parameter(name = "token", description = "액세스 토큰")}
    )
    @DeleteMapping("/delete")
    public ResponseEntity<Boolean> deleteBoard(
            @RequestBody DeleteRequest deleteRequest,
            @RequestHeader("token") String token
    ) {
        boardContentService.deleteBoard(token, deleteRequest);
        return ResponseEntity.ok(true);
    }

    @GetMapping("/content/{boardId}")
    @Transactional
    @Operation(summary = "게시글 보기", description = "게시글 목록에서 글을 눌렀을 때"
            , parameters = {
            @Parameter(name = "boardId", description = "게시글 번호"),
            @Parameter(name = "token", description = "액세스 토큰")}
    )
    public ResponseEntity<BoardContentResponse> getBoardContent(
            @PathVariable("boardId") Long boardId,
            @RequestHeader("token") String token
    ) {
        return ResponseEntity.ok(boardContentService.getBoardContent(boardId, token));
    }

    @GetMapping("/entity")
    public Comment getComment() {
        return commentRepository.findById(1L).get();
    }
}
