package com.ay.exchange.board.controller;

import com.ay.exchange.aws.service.AwsS3Service;
import com.ay.exchange.board.dto.request.DeleteRequest;
import com.ay.exchange.board.dto.request.WriteRequest;
import com.ay.exchange.board.dto.response.BoardContentResponse;
import com.ay.exchange.board.dto.response.BoardResponse;
import com.ay.exchange.board.entity.Board;
import com.ay.exchange.board.service.BoardContentService;
import com.ay.exchange.board.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;
    private final BoardContentService boardContentService;
    private final AwsS3Service awsS3Service;

    @Operation(summary = "게시글 작성", description = "게시글 작성")
    @PostMapping(value = "/write")
    public ResponseEntity<Boolean> writeBoard(
            @RequestPart("writeRequest") WriteRequest writeRequest,
            @RequestPart("file") MultipartFile multipartFile,
            @RequestHeader("token") String accessToken
    ) {
        boardService.writeBoard(writeRequest, multipartFile, accessToken);
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

    @Operation(summary = "게시글 삭제", description = "게시글 삭제(대댓글과 파일이 삭제됨)"
            , parameters = {@Parameter(name = "token", description = "액세스 토큰")}
    )
    @DeleteMapping("/delete")
    public ResponseEntity<Boolean> deleteBoard(
            @RequestBody DeleteRequest deleteRequest,
            @RequestHeader("token") String token
    ) {
        boardService.deleteBoard(token, deleteRequest);
        return ResponseEntity.ok(true);
    }


    //tkddls8900/김상인파일_1666970104756.txt
    //bpax7m4BI/김상인파일.txt
    @GetMapping(value = "/file/download")
    public ResponseEntity<ByteArrayResource> downloadFile(
            @RequestParam("filePath") String filePath
    ) {
        byte[] data = awsS3Service.downloadFile(filePath);
        ByteArrayResource resource = new ByteArrayResource(data);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentLength(data.length);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(AwsS3Service.createContentDisposition(filePath));

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(resource);
    }
}