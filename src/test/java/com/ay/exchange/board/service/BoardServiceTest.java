package com.ay.exchange.board.service;

import com.ay.exchange.board.dto.query.BoardInfoDto;
import com.ay.exchange.board.entity.Board;
import com.ay.exchange.board.entity.vo.BoardCategory;
import com.ay.exchange.board.entity.vo.Category;
import com.ay.exchange.board.entity.vo.DepartmentType;
import com.ay.exchange.board.entity.vo.FileType;
import com.ay.exchange.board.repository.BoardRepository;
import com.ay.exchange.user.entity.User;
import com.ay.exchange.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class BoardServiceTest {
    @Autowired
    BoardService boardService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BoardRepository boardRepository;

    @BeforeAll
    void init() {
        User user = User.builder()
                .email("test@gmail.com")
                .nickName("test")
                .exchangeSuccessCount(0)
                .desiredData("")
                .build();
        userRepository.save(user);
    }

    @Test
    @DisplayName("1,2학년 필터링")
    void 게시글_조회() {
        Board board = Board.builder()
                .title("title")
                .numberOfFilePages(1)
                .filePath("filePath")
                .originalFileName("fileName")
                .approval(1)
                .email("test@gmail.com")
                .boardCategory(BoardCategory.builder().
                        category(Category.신학대학)
                        .departmentType(DepartmentType.신학과)
                        .fileType(FileType.중간고사)
                        .gradeType("1")
                        .subjectName("subject")
                        .professorName("professor")
                        .build())
                .exchangeSuccessCount(0)
                .build();
        boardRepository.save(board);

        Board board2 = Board.builder()
                .title("title2")
                .numberOfFilePages(1)
                .filePath("filePath2")
                .originalFileName("fileName2")
                .approval(1)
                .email("test@gmail.com")
                .boardCategory(BoardCategory.builder().
                        category(Category.신학대학)
                        .departmentType(DepartmentType.신학과)
                        .fileType(FileType.중간고사)
                        .gradeType("2")
                        .subjectName("subject2")
                        .professorName("professor2")
                        .build())
                .exchangeSuccessCount(0)
                .build();
        boardRepository.save(board2);

        List<BoardInfoDto> boardInfos = boardService.getBoardList(0,
                Category.신학대학.ordinal(),
                DepartmentType.신학과.name(),
                "1,2",
                FileType.중간고사.name()).getContent();

        assertTrue(boardInfos.stream().anyMatch(boardInfo -> boardInfo.getId().equals(board.getId())));
        assertTrue(boardInfos.stream().anyMatch(boardInfo -> boardInfo.getId().equals(board2.getId())));
    }

    @AfterAll
    void deleteEntity() {
        userRepository.deleteById("test@gmail.com");
    }

}