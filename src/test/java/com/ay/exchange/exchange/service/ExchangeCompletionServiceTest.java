package com.ay.exchange.exchange.service;

import com.ay.exchange.board.entity.Board;
import com.ay.exchange.board.entity.vo.BoardCategory;
import com.ay.exchange.board.entity.vo.Category;
import com.ay.exchange.board.entity.vo.DepartmentType;
import com.ay.exchange.board.entity.vo.FileType;
import com.ay.exchange.board.exception.FailModifyBoardException;
import com.ay.exchange.board.repository.BoardRepository;
import com.ay.exchange.common.util.DateUtil;
import com.ay.exchange.common.util.ExchangeType;
import com.ay.exchange.exchange.dto.request.ExchangeRequest;
import com.ay.exchange.exchange.entity.Exchange;
import com.ay.exchange.exchange.exception.UnableExchangeException;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
class ExchangeCompletionServiceTest {
    @Autowired
    ExchangeCompletionService exchangeCompletionService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    ExchangeRepository exchangeRepository;

    Board board, board2;
    Exchange exchange, exchange2;

    @BeforeAll
    void init() {
        //마이페이지 쿼리문에 필요한 데이터 세팅
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

        board = Board.builder()
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

        board2 = Board.builder()
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

        exchange = Exchange.builder()
                .boardId(board.getId())
                .email("test@gmail.com")
                .requesterBoardId(board2.getId())
                .requesterEmail("test2@gmail.com")
                .type(ExchangeType.ACCEPT.getType()) //-3은 교환 받음
                .build();
        exchangeRepository.save(exchange);

        exchange2 = Exchange.builder()
                .boardId(board2.getId())
                .email("test2@gmail.com")
                .requesterBoardId(board.getId())
                .requesterEmail("test@gmail.com")
                .type(ExchangeType.REQUEST.getType()) //-2는 교환 요청함
                .build();
        exchangeRepository.save(exchange2);
    }

    @Test
    @Order(1)
    @Rollback(false)
    void 교환_수락() {
        ExchangeAccept exchangeAccept = new ExchangeAccept(exchange.getId(),
                "test2@gmail.com", board.getId(), board2.getId());

        assertDoesNotThrow(() -> {
            exchangeCompletionService.acceptExchange(exchangeAccept, "test@gmail.com");
        });
    }

    @Test
    @Order(2)
    void 교환_완료_수() {
        int actual = exchangeCompletionService.getDownloadableCount("test@gmail.com");
        int actual2 = exchangeCompletionService.getDownloadableCount("test2@gmail.com");

        assertEquals(1, actual);
        assertEquals(1, actual2);
    }

    @Test
    @Order(3)
    void 다운로드_가능한_자료_조회() {
        List<DownloadableInfo> downloadableInfos = List.of(
                new DownloadableInfo(exchange.getCreatedDate(),
                        board2.getTitle(),
                        "test2",
                        board2.getId(),
                        board2.getBoardCategory().getCategory()));
        DownloadableResponse expected = new DownloadableResponse(1L, downloadableInfos);

        DownloadableResponse actual = exchangeCompletionService.getDownloadable(0, "test@gmail.com");

        assertIterableEquals(expected.getDownloadableInfos(), actual.getDownloadableInfos());
    }

    @Test
    @Order(4)
    @DisplayName("최근 교환일이 3일이 넘지 않음")
    void 최근_교환일_확인() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -3);
        Date date = new Date(calendar.getTimeInMillis());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        assertThrows(FailModifyBoardException.class, ()->{
            exchangeCompletionService.checkExchangeCompletionDate(simpleDateFormat.format(date), "test@gmail.com", board.getId());
        });
    }

    @Test
    @Order(5)
    @DisplayName("최근 교환일이 3일이 넘음")
    void 최근_교환일_확인2() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, +3);
        Date date = new Date(calendar.getTimeInMillis());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        assertDoesNotThrow(()->{
            exchangeCompletionService.checkExchangeCompletionDate(simpleDateFormat.format(date), "test@gmail.com", board.getId());
        });
    }

    @Test
    @Order(6)
    @DisplayName("교환 완료된 자료인지 확인")
    void 교환_완료_확인() {
        Board board3 = Board.builder()
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
        boardRepository.save(board3);

        ExchangeRequest exchangeRequest = new ExchangeRequest(board.getId(), board3.getId());

        assertThrows(UnableExchangeException.class, ()->{
            exchangeCompletionService.existsExchangeCompletion(exchangeRequest, "test2@gmail.com", "test@gmail.com");
        });

    }

    @AfterAll
    void deleteEntity() {
        userRepository.deleteById("test@gmail.com");
        userRepository.deleteById("test2@gmail.com");
    }
}