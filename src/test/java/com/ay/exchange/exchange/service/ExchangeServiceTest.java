package com.ay.exchange.exchange.service;

import com.ay.exchange.board.entity.Board;
import com.ay.exchange.board.entity.vo.BoardCategory;
import com.ay.exchange.board.entity.vo.Category;
import com.ay.exchange.board.entity.vo.DepartmentType;
import com.ay.exchange.board.entity.vo.FileType;
import com.ay.exchange.board.repository.BoardRepository;
import com.ay.exchange.common.util.ExchangeType;
import com.ay.exchange.exchange.dto.request.ExchangeRequest;
import com.ay.exchange.exchange.dto.response.ExchangeResponse;
import com.ay.exchange.exchange.entity.Exchange;
import com.ay.exchange.exchange.exception.UnableExchangeException;
import com.ay.exchange.exchange.repository.ExchangeRepository;
import com.ay.exchange.user.dto.request.ExchangeAccept;
import com.ay.exchange.user.dto.request.ExchangeRefusal;
import com.ay.exchange.user.entity.User;
import com.ay.exchange.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExchangeServiceTest {
    @Autowired
    ExchangeService exchangeService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    ExchangeRepository exchangeRepository;

    @BeforeAll
    void init() {
        User user = User.builder()
                .email("test@gmail.com")
                .nickName("test")
                .exchangeSuccessCount(0)
                .desiredData("")
                .build();
        userRepository.save(user);

        User user2 = User.builder()
                .email("test2@gmail.com")
                .nickName("test2")
                .exchangeSuccessCount(0)
                .desiredData("")
                .build();
        userRepository.save(user2);
    }

    @Test
    @DisplayName("해당 자료 작성자가 존재하지 않음")
    void 교환_요청_실패() {
        ExchangeRequest exchangeRequest = new ExchangeRequest(1L, 2L);

        assertThrows(UnableExchangeException.class, () -> {
            exchangeService.requestExchange(exchangeRequest, "test@gmail.com");
        });
    }

    @Test
    @DisplayName("내 자료가 존재하지 않음")
    void 교환_요청_실패2() {
        Board board = Board.builder()
                .title("title")
                .numberOfFilePages(1)
                .filePath("filePath")
                .originalFileName("fileName")
                .approval(1)
                .email("test2@gmail.com")
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
        ExchangeRequest exchangeRequest = new ExchangeRequest(board.getId(), board.getId() + 1);

        assertThrows(UnableExchangeException.class, () -> {
            exchangeService.requestExchange(exchangeRequest, "test@gmail.com");
        });
    }

    @Test
    void 교환_요청_성공() {
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
                .title("title3")
                .numberOfFilePages(1)
                .filePath("filePath3")
                .originalFileName("fileName3")
                .approval(1)
                .email("test2@gmail.com")
                .boardCategory(BoardCategory.builder().
                        category(Category.신학대학)
                        .departmentType(DepartmentType.기독교교육과)
                        .fileType(FileType.중간고사)
                        .gradeType("1")
                        .subjectName("subject3")
                        .professorName("professor3")
                        .build())
                .exchangeSuccessCount(0)
                .build();
        boardRepository.save(board2);
        ExchangeRequest exchangeRequest = new ExchangeRequest(board2.getId(), board.getId());

        assertDoesNotThrow(() -> {
            exchangeService.requestExchange(exchangeRequest, "test@gmail.com");
        });
    }

    @Test
    void 교환_요청_삭제() {
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
                .email("test2@gmail.com")
                .boardCategory(BoardCategory.builder().
                        category(Category.신학대학)
                        .departmentType(DepartmentType.기독교교육과)
                        .fileType(FileType.중간고사)
                        .gradeType("1")
                        .subjectName("subject2")
                        .professorName("professor2")
                        .build())
                .exchangeSuccessCount(0)
                .build();
        boardRepository.save(board2);

        Exchange exchange = Exchange.builder()
                .boardId(board.getId())
                .email("test@gmail.com")
                .requesterBoardId(board2.getId())
                .requesterEmail("test2@gmail.com")
                .type(ExchangeType.ACCEPT.getType()) //-3은 교환 받음
                .build();
        exchangeRepository.save(exchange);

        Exchange exchange2 = Exchange.builder()
                .boardId(board2.getId())
                .email("test2@gmail.com")
                .requesterBoardId(board.getId())
                .requesterEmail("test@gmail.com")
                .type(ExchangeType.REQUEST.getType()) //-2는 교환 요청함
                .build();
        exchangeRepository.save(exchange2);

        ExchangeAccept exchangeAccept = new ExchangeAccept(exchange.getId(),
                "test2@gmail.com", board.getId(), board2.getId());

        assertDoesNotThrow(() -> {
            exchangeService.deleteExchange(exchangeAccept);
        });
    }

    @Test
    void 교환_거절() {
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
                .email("test2@gmail.com")
                .boardCategory(BoardCategory.builder().
                        category(Category.신학대학)
                        .departmentType(DepartmentType.기독교교육과)
                        .fileType(FileType.중간고사)
                        .gradeType("1")
                        .subjectName("subject2")
                        .professorName("professor2")
                        .build())
                .exchangeSuccessCount(0)
                .build();
        boardRepository.save(board2);

        Exchange exchange = Exchange.builder()
                .boardId(board.getId())
                .email("test@gmail.com")
                .requesterBoardId(board2.getId())
                .requesterEmail("test2@gmail.com")
                .type(ExchangeType.ACCEPT.getType()) //-3은 교환 받음
                .build();
        exchangeRepository.save(exchange);

        Exchange exchange2 = Exchange.builder()
                .boardId(board2.getId())
                .email("test2@gmail.com")
                .requesterBoardId(board.getId())
                .requesterEmail("test@gmail.com")
                .type(ExchangeType.REQUEST.getType()) //-2는 교환 요청함
                .build();
        exchangeRepository.save(exchange2);

        ExchangeRefusal exchangeRefusal = new ExchangeRefusal(exchange.getId(),
                "test2@gmail.com", board.getId(), board2.getId());

        assertDoesNotThrow(() -> {
            exchangeService.refuseExchange(exchangeRefusal, "test@gmail.com");
        });
    }

    @Test
    void 교환_신청_조회(){
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
                .email("test2@gmail.com")
                .boardCategory(BoardCategory.builder().
                        category(Category.신학대학)
                        .departmentType(DepartmentType.기독교교육과)
                        .fileType(FileType.중간고사)
                        .gradeType("1")
                        .subjectName("subject2")
                        .professorName("professor2")
                        .build())
                .exchangeSuccessCount(0)
                .build();
        boardRepository.save(board2);

        Exchange exchange = Exchange.builder()
                .boardId(board.getId())
                .email("test@gmail.com")
                .requesterBoardId(board2.getId())
                .requesterEmail("test2@gmail.com")
                .type(ExchangeType.ACCEPT.getType()) //-3은 교환 받음
                .build();
        exchangeRepository.save(exchange);

        Exchange exchange2 = Exchange.builder()
                .boardId(board2.getId())
                .email("test2@gmail.com")
                .requesterBoardId(board.getId())
                .requesterEmail("test@gmail.com")
                .type(ExchangeType.REQUEST.getType()) //-2는 교환 요청함
                .build();
        exchangeRepository.save(exchange2);

        ExchangeResponse exchangeResponse = exchangeService.getExchanges(0,"test@gmail.com");

        assertEquals("title", exchangeResponse.getExchangeInfos().get(0).getMyTitle());
        assertEquals("title2", exchangeResponse.getExchangeInfos().get(0).getTitle());
        assertEquals(1L, exchangeResponse.getExchangePages());
    }

    @AfterAll
    void deleteEntity() {
        userRepository.deleteById("test@gmail.com");
        userRepository.deleteById("test2@gmail.com");
    }
}