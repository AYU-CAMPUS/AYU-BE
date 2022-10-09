package com.ay.exchange.board.controller;

import com.ay.exchange.board.dto.request.DeleteRequest;
import com.ay.exchange.board.dto.request.WriteRequest;
import com.ay.exchange.board.dto.response.BoardResponse;
import com.ay.exchange.board.entity.Board;
import com.ay.exchange.board.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    @Operation(summary = "게시글 작성", description = "게시글 작성")
    @PostMapping("/write")
    public ResponseEntity<Boolean> writeBoard(
            @RequestBody WriteRequest writeRequest
    ) {
        boardService.writeBoard(writeRequest);
        return ResponseEntity.ok(true);
    }

    @Operation(summary = "게시글 조회"
            , description = "메인 페이지에서 클릭 된 카테고리 별 게시글 조회"
            , parameters = {@Parameter(name = "page", description = "페이지 번호")
            , @Parameter(name = "category", description = "신학대학(0), 인문대학(1), 예술체육대학(2), " +
            "사회과학대학(3), 창의융합대학(4), 인성양성(5), 리더십(6), 융합실무(7), 문제해결(8), 글로벌(9), 의사소통(10),논문(11), 자격증(12)")}
    )
    @GetMapping("/{category}")
    public ResponseEntity<BoardResponse> findBoardList(
            @PathVariable("category") Integer category,
            @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(name = "department", required = false, defaultValue = ",") String department,
            @RequestParam(name = "grade", required = false, defaultValue = ",") String grade,
            @RequestParam(name = "type", required = false, defaultValue = ",") String type
    ) {
        return ResponseEntity.ok(boardService
                .getBoardList(page, category, department, grade, type));
    }
//department, grade, type
//    @GetMapping("/{mediumCategory}")
//    public ResponseEntity<Boolean> getq(
//            @RequestParam("asdf")String asdf
//    ){
//        return ResponseEntity.ok(true);
//    }
}