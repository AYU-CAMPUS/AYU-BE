package com.ay.exchange.comment.controller;

import com.ay.exchange.comment.dto.response.CommentInfoDto;
import com.ay.exchange.comment.dto.request.DeleteRequest;
import com.ay.exchange.comment.dto.request.WriteRequest;
import com.ay.exchange.comment.service.CommentService;
import com.ay.exchange.common.error.dto.ErrorDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
@Tag(name = "대댓글", description = "대댓글 관련 api")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/write")
    @Operation(summary = "대댓글 작성", description = "댓글 및 대댓글 작성",
            parameters = {@Parameter(name = "token", description = "액세스 토큰")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = ByteArrayResource.class))),
                    @ApiResponse(responseCode = "422", description = "댓글 작성에 실패하였습니다.", content = @Content(schema = @Schema(implementation = ErrorDto.class)))}
    )
    public ResponseEntity<Boolean> writeComment(
            @RequestBody WriteRequest writeRequest,
            @RequestHeader("token") String token
    ) {
        commentService.writeComment(writeRequest, token);
        return ResponseEntity.ok(true);
    }

    @Operation(summary = "대댓글 삭제", description = "댓글 및 대댓글 삭제",
            parameters = {@Parameter(name = "token", description = "액세스 토큰")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = ByteArrayResource.class))),
                    @ApiResponse(responseCode = "422", description = "댓글 삭제에 실패하였습니다.", content = @Content(schema = @Schema(implementation = ErrorDto.class)))}
    )
    @DeleteMapping("/delete")
    public ResponseEntity<Boolean> deleteComment(
            @RequestBody DeleteRequest deleteRequest,
            @RequestHeader("token") String token
    ) {
        commentService.deleteComment(deleteRequest, token);
        return ResponseEntity.ok(true);
    }

    @Operation(summary = "댓글 페이징", description = "무한스크롤 댓글 조회",
            parameters = {
                    @Parameter(name = "boardId", description = "게시글 번호"),
                    @Parameter(name = "page", description = "페이징 번호")}
    )
    @GetMapping("/{boardId}") //validate page과 board값이 최대값을 넘어서면 안된다.
    public List<CommentInfoDto> getComments(
            @PathVariable("boardId") Long boardId,
            @RequestParam(name = "page", required = false, defaultValue = "1") Integer page
    ) {
        return commentService.getComments(boardId, page);
    }


}
