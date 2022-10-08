package com.ay.exchange.board.controller;

import com.ay.exchange.board.dto.request.DeleteRequest;
import com.ay.exchange.board.service.BoardContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardContentController {
    private final BoardContentService boardContentService;

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
}
