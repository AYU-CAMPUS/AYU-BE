package com.ay.exchange.management.service;

import com.ay.exchange.board.entity.Board;
import com.ay.exchange.board.entity.vo.BoardCategory;
import com.ay.exchange.board.entity.vo.Category;
import com.ay.exchange.board.entity.vo.DepartmentType;
import com.ay.exchange.board.entity.vo.FileType;
import com.ay.exchange.board.repository.BoardRepository;
import com.ay.exchange.common.util.Approval;
import com.ay.exchange.management.dto.request.BoardIdRequest;
import com.ay.exchange.management.dto.request.SuspensionRequest;
import com.ay.exchange.management.dto.response.BoardInfo;
import com.ay.exchange.management.exception.FailAcceptRequestBoard;
import com.ay.exchange.management.exception.FailUpdatedSuspension;
import com.ay.exchange.user.entity.User;
import com.ay.exchange.user.entity.vo.Authority;
import com.ay.exchange.user.repository.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ManagementServiceTest {
    @Autowired
    ManagementService managementService;

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
                .authority(Authority.Admin)
                .build();
        userRepository.save(user);

        User user2 = User.builder()
                .email("test2@gmail.com")
                .nickName("test2")
                .exchangeSuccessCount(0)
                .desiredData("")
                .authority(Authority.User)
                .build();
        userRepository.save(user2);
    }

    @Test
    void 요청_게시글_허가_실패() {
        BoardIdRequest boardIdRequest = new BoardIdRequest(1L);

        assertThrows(FailAcceptRequestBoard.class, () -> {
            managementService.acceptRequestBoard(boardIdRequest);
        });
    }

    @Test
    void 요청_게시글_허가_성공() {
        Board board = Board.builder()
                .title("title")
                .numberOfFilePages(1)
                .filePath("filePath")
                .originalFileName("fileName")
                .approval(Approval.WAITING.getApproval())
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
        BoardIdRequest boardIdRequest = new BoardIdRequest(board.getId());

        assertDoesNotThrow(() -> {
            managementService.acceptRequestBoard(boardIdRequest);
        });
    }

    @Test
    void 요청_게시글_거절_실패() {
        BoardIdRequest boardIdRequest = new BoardIdRequest(1L);

        assertThrows(FailAcceptRequestBoard.class, () -> {
            managementService.rejectRequestBoard(boardIdRequest);
        });
    }

    @Test
    void 요청_게시글_거절_성공() {
        Board board = Board.builder()
                .title("title")
                .numberOfFilePages(1)
                .filePath("filePath")
                .originalFileName("fileName")
                .approval(Approval.WAITING.getApproval())
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
        BoardIdRequest boardIdRequest = new BoardIdRequest(board.getId());

        assertDoesNotThrow(() -> {
            managementService.rejectRequestBoard(boardIdRequest);
        });
    }

    @Test
    void 사용자_정지_주기_실패() {
        SuspensionRequest suspensionRequest = new SuspensionRequest("anonymous@gmail.com", "2023-03-25");

        assertThrows(FailUpdatedSuspension.class, () -> {
            managementService.updateSuspension(suspensionRequest);
        });
    }

    @Test
    void 사용자_정지_주기_성공() {
        SuspensionRequest suspensionRequest = new SuspensionRequest("test@gmail.com", "2023-03-25");

        assertDoesNotThrow(() -> {
            managementService.updateSuspension(suspensionRequest);
        });
    }

    @Test
    void 사용자_요청_게시글_조회() {
        Board board = Board.builder()
                .title("title")
                .numberOfFilePages(1)
                .filePath("filePath")
                .originalFileName("fileName")
                .approval(Approval.WAITING.getApproval())
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
                .approval(Approval.WAITING.getApproval())
                .email("test2@gmail.com")
                .boardCategory(BoardCategory.builder().
                        category(Category.신학대학)
                        .departmentType(DepartmentType.신학과)
                        .fileType(FileType.중간고사)
                        .gradeType("1")
                        .subjectName("subject2")
                        .professorName("professor2")
                        .build())
                .exchangeSuccessCount(0)
                .build();
        boardRepository.save(board2);

        List<BoardInfo> boardInfos = managementService.findRequestBoard(0);

        assertEquals(2, boardInfos.size());
        assertTrue(boardInfos.stream().anyMatch(boardInfo -> boardInfo.getBoardId().equals(board.getId())));
        assertTrue(boardInfos.stream().anyMatch(boardInfo -> boardInfo.getBoardId().equals(board2.getId())));
    }

    @AfterAll
    void deleteEntity() {
        userRepository.deleteById("test@gmail.com");
        userRepository.deleteById("test2@gmail.com");
    }
}