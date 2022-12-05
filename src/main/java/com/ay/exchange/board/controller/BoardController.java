package com.ay.exchange.board.controller;

import com.ay.exchange.board.dto.CategoryDto;
import com.ay.exchange.board.dto.request.DeleteRequest;
import com.ay.exchange.board.dto.request.WriteRequest;
import com.ay.exchange.board.dto.response.BoardContentResponse;
import com.ay.exchange.board.dto.response.BoardResponse;
import com.ay.exchange.board.exception.FileInvalidException;
import com.ay.exchange.board.service.BoardContentService;
import com.ay.exchange.board.service.BoardService;
import com.ay.exchange.common.error.dto.ErrorDto;
import com.ay.exchange.common.util.FileValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;
    private final BoardContentService boardContentService;

    @Operation(summary = "게시글 작성", description = "게시글 작성",
            parameters = {
                    @Parameter(name = "title", description = "글제목"),
                    @Parameter(name = "category", description = "신학대학(0), 인문대학(1), 예술체육대학(2), " +
                            "사회과학대학(3), 창의융합대학(4), 인성양성(5), 리더십(6), 융합실무(7), 문제해결(8), 글로벌(9), 의사소통(10), " +
                            "레포트(11), PPT템플릿(12), 한국사자격증(13), 토익(14), 토플(15), 논문(16), 이력서(17), 컴활자격증(18)"),
                    @Parameter(name = "departmentType", description = "신학과(0), 기독교교육과(1), 국어국문학과(2), 영미언어문화학과(3), " +
                            "러시아언어문화학과(4), 중국언어문화학과(5), 유아교육과(6), 공연예술학과(7), 음악학과(8), " +
                            "디지털미디어디자인학과(9), 화장품발명디자인학과(10), 뷰티메디컬디자인학과(11), " +
                            "글로벌경영학과(12), 행정학과(13), 관광경영학과(14), 식품영양학과(15), " +
                            "컴퓨터공학과(16), 정보전기전자공학과(17), 통계데이터사이언스학과(18), 소프트웨어학과(19), " +
                            "도시정보공학과(20), 환경에너지공학과(21), AI융합학과(22)"),
                    @Parameter(name = "fileType", description = "중간고사(0), 기말고사(1), 과제(2), 요약(3)"),
                    @Parameter(name = "gradeType", description = "Freshman(0), Sophomore(1), Junior(2), Senior(3)"),
                    @Parameter(name = "subjectName", description = "전공 또는 교양 선택 시 과목명 입력"),
                    @Parameter(name = "professorName", description = "전공 또는 교양 선택 시 교수명 입력"),
                    @Parameter(name = "numberOfFilePages", description = "파일 페이지 수"),
                    @Parameter(name = "content", description = "글 내용"),
                    @Parameter(name = "file", description = "파일"),
                    @Parameter(name = "token", description = "액세스 토큰")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = ByteArrayResource.class))),
                    @ApiResponse(responseCode = "400", description = "파일 형식이 잘못되었습니다.", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
                    @ApiResponse(responseCode = "422", description = "게시글 작성에 실패하였습니다.", content = @Content(schema = @Schema(implementation = ErrorDto.class)))}
    )
    @PostMapping(value = "/write")
    public ResponseEntity<Boolean> writeBoard(
            @RequestPart("title") String title,
            @RequestPart("category") Integer category,
            @RequestPart(value = "departmentType", required = false) Integer departmentType,
            @RequestPart(value = "fileType", required = false) Integer fileType,
            @RequestPart(value = "gradeType", required = false) Integer gradeType,
            @RequestPart(value = "subjectName", required = false) String subjectName,
            @RequestPart(value = "professorName", required = false) String professorName,
            @RequestPart("numberOfFilePages") Integer numberOfFilePages,
            @RequestPart("content") String content,
            @RequestPart("file") MultipartFile multipartFile,
            @RequestHeader("token") String token
    ) {
        if (FileValidator.isAllowedFileType(multipartFile)) {
            CategoryDto categoryDto = new CategoryDto(category, departmentType, fileType, gradeType, subjectName, professorName);
            boardService.writeBoard(new WriteRequest(title, categoryDto, numberOfFilePages, content), multipartFile, token);
            return ResponseEntity.ok(true);
        }
        throw new FileInvalidException();
    }

    @Operation(summary = "게시글 조회", description = "메인 페이지에서 클릭 된 카테고리 별 게시글 조회",
            parameters = {
                    @Parameter(name = "page", description = "페이지 번호"),
                    @Parameter(name = "category", description = "신학대학(0), 인문대학(1), 예술체육대학(2), " +
                            "사회과학대학(3), 창의융합대학(4), 인성양성(5), 리더십(6), 융합실무(7), 문제해결(8), 글로벌(9), 의사소통(10), 레포트(11), PPT템플릿(12), 한국사자격증(13), 토익(14), 토플(15), 논문(16), 이력서(17), 컴활자격증(18)")
            }
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
    @Operation(summary = "게시글 보기", description = "게시글 목록에서 글을 눌렀을 때",
            parameters = {
                    @Parameter(name = "boardId", description = "게시글 번호"),
                    @Parameter(name = "token", description = "액세스 토큰")
            }
    )
    public ResponseEntity<BoardContentResponse> getBoardContent(
            @PathVariable("boardId") Long boardId,
            @RequestHeader("token") String token
    ) {
        return ResponseEntity.ok(boardContentService.getBoardContent(boardId, token));
    }

    @Operation(summary = "게시글 삭제", description = "게시글 삭제(대댓글과 파일이 삭제됨)",
            parameters = {@Parameter(name = "token", description = "액세스 토큰")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = ByteArrayResource.class))),
                    @ApiResponse(responseCode = "422", description = "게시글 삭제에 실패하였습니다.", content = @Content(schema = @Schema(implementation = ErrorDto.class)))}
    )
    @DeleteMapping("/delete")
    public ResponseEntity<Boolean> deleteBoard(
            @RequestBody DeleteRequest deleteRequest,
            @RequestHeader("token") String token
    ) {
        boardService.deleteBoard(token, deleteRequest);
        return ResponseEntity.ok(true);
    }

//    @GetMapping("/edit")
//    public ResponseEntity<Boolean> editBoard(){
//        return ResponseEntity.ok(boardContentService.editBoard());
//    }

}