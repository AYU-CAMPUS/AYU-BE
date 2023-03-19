package com.ay.exchange.user.service;

import com.ay.exchange.board.entity.Board;
import com.ay.exchange.board.entity.vo.BoardCategory;
import com.ay.exchange.board.entity.vo.Category;
import com.ay.exchange.board.entity.vo.DepartmentType;
import com.ay.exchange.board.entity.vo.FileType;
import com.ay.exchange.board.repository.BoardRepository;

import com.ay.exchange.exchange.entity.Exchange;
import com.ay.exchange.exchange.repository.ExchangeRepository;
import com.ay.exchange.user.dto.DownloadableInfo;
import com.ay.exchange.user.dto.request.ExchangeAccept;
import com.ay.exchange.user.dto.response.DownloadableResponse;
import com.ay.exchange.user.entity.User;
import com.ay.exchange.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
class MyPageServiceTest {
    @Autowired
    MyPageService myPageService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    ExchangeRepository exchangeRepository;

    User user, user2;
    Board board, board2, board3, board4;
    Exchange exchange, exchange2;

    @BeforeAll
        //마이페이지 쿼리문에 필요한 데이터 세팅
    void init() {
        user = User.builder()
                .email("test@gmail.com")
                .nickName("test")
                .exchangeSuccessCount(0)
                .desiredData("")
                .build();
        userRepository.save(user);

        user2 = User.builder()
                .email("test2@gmail.com")
                .nickName("test2")
                .exchangeSuccessCount(0)
                .desiredData("")
                .build();
        userRepository.save(user2);

        board = Board.builder()
                .title("title")
                .numberOfFilePages(1)
                .filePath("filePath")
                .originalFileName("fileName")
                .approval(1)
                .email(user2.getEmail())
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

        board2 = Board.builder()
                .title("title2")
                .numberOfFilePages(1)
                .filePath("filePath2")
                .originalFileName("fileName2")
                .approval(1)
                .email(user.getEmail())
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

        board3 = Board.builder()
                .title("title3")
                .numberOfFilePages(1)
                .filePath("filePath3")
                .originalFileName("fileName3")
                .approval(1)
                .email(user2.getEmail())
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
        boardRepository.save(board3);

        board4 = Board.builder()
                .title("title4")
                .numberOfFilePages(1)
                .filePath("filePath4")
                .originalFileName("fileName4")
                .approval(1)
                .email(user2.getEmail())
                .boardCategory(BoardCategory.builder().
                        category(Category.신학대학)
                        .departmentType(DepartmentType.기독교교육과)
                        .fileType(FileType.중간고사)
                        .gradeType("2")
                        .subjectName("subject4")
                        .professorName("professor4")
                        .build())
                .exchangeSuccessCount(0)
                .build();
        boardRepository.save(board4);

        exchange = Exchange.builder()
                .boardId(board.getId())
                .email(user.getEmail())
                .requesterBoardId(board3.getId())
                .requesterEmail(user2.getEmail())
                .type(-3) //-3은 교환 받음
                .build();
        exchangeRepository.save(exchange);

        exchange2 = Exchange.builder()
                .boardId(board3.getId())
                .email(user2.getEmail())
                .requesterBoardId(board.getId())
                .requesterEmail(user.getEmail())
                .type(-2) //-2는 교환 요청함
                .build();
        exchangeRepository.save(exchange2);
    }

    @Test
    @Order(1)
    @Rollback(false)
    void 교환_수락() {
        ExchangeAccept exchangeAccept = new ExchangeAccept(exchange.getId(),
                user2.getEmail(), board2.getId(), board4.getId());

        assertDoesNotThrow(() -> {
            myPageService.acceptExchange(exchangeAccept, user.getEmail());
        });
    }

    @Test
    @Order(2)
    void 교환_완료_수() {
        int actual = myPageService.getDownloadableCount(user.getEmail());
        int actual2 = myPageService.getDownloadableCount(user2.getEmail());

        assertEquals(1, actual);
        assertEquals(1, actual2);
    }

    @Test
    @Order(3)
    void 다운로드_가능한_자료_조회() {
        List<DownloadableInfo> downloadableInfos = List.of(
                new DownloadableInfo(exchange.getCreatedDate(),
                        board4.getTitle(),
                        user2.getNickName(),
                        board4.getId(),
                        board4.getBoardCategory().getCategory()));
        DownloadableResponse expected = new DownloadableResponse(1L, downloadableInfos);

        DownloadableResponse actual = myPageService.getDownloadable(0, user.getEmail());

        assertIterableEquals(expected.getDownloadableInfos(), actual.getDownloadableInfos());
    }

    @Test
    @Order(4)
    void 교환_요청_삭제(){
        ExchangeAccept exchangeAccept = new ExchangeAccept(exchange.getId(),
                user2.getEmail(), board.getId(), board3.getId());

        assertDoesNotThrow(() -> {
            myPageService.deleteExchange(exchangeAccept);
        });
    }

    @AfterAll
    void deleteEntity() {
        userRepository.delete(user);
        userRepository.delete(user2);
    }
}