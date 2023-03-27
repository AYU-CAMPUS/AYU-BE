package com.ay.exchange.board.service;

import com.ay.exchange.board.dto.query.BoardContentInfo2Dto;
import com.ay.exchange.board.dto.query.BoardContentInfoDto;
import com.ay.exchange.board.entity.Board;
import com.ay.exchange.board.entity.BoardContent;
import com.ay.exchange.board.entity.vo.BoardCategory;
import com.ay.exchange.board.entity.vo.Category;
import com.ay.exchange.board.entity.vo.DepartmentType;
import com.ay.exchange.board.entity.vo.FileType;

import com.ay.exchange.board.repository.BoardContentRepository;
import com.ay.exchange.board.repository.BoardRepository;
import com.ay.exchange.comment.entity.Comment;
import com.ay.exchange.comment.repository.CommentRepository;

import com.ay.exchange.exchange.entity.Exchange;
import com.ay.exchange.exchange.repository.ExchangeRepository;
import com.ay.exchange.exchange.repository.querydsl.ExchangeCompletionRepository;
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
class BoardContentServiceTest {
    @Autowired
    BoardContentService boardContentService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    BoardContentRepository boardContentRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    ExchangeRepository exchangeRepository;

    Board board, board2;

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

        board = Board.builder()
                .title("title")
                .numberOfFilePages(1)
                .filePath("filePath")
                .originalFileName("fileName")
                .approval(1)
                .email("test@gmail.com")
                .boardCategory(BoardCategory.builder()
                        .category(Category.신학대학)
                        .departmentType(DepartmentType.신학과)
                        .fileType(FileType.중간고사)
                        .gradeType("1")
                        .subjectName("subject")
                        .professorName("professor")
                        .build())
                .exchangeSuccessCount(0)
                .build();
        boardRepository.save(board);

        BoardContent boardContent = BoardContent.builder()
                .content("content")
                .board(board)
                .build();
        boardContentRepository.save(boardContent);

        board2 = Board.builder()
                .title("title3")
                .numberOfFilePages(1)
                .filePath("filePath3")
                .originalFileName("fileName3")
                .approval(1)
                .email("test2@gmail.com")
                .boardCategory(BoardCategory.builder()
                        .category(Category.신학대학)
                        .departmentType(DepartmentType.기독교교육과)
                        .fileType(FileType.중간고사)
                        .gradeType("1")
                        .subjectName("subject3")
                        .professorName("professor3")
                        .build())
                .exchangeSuccessCount(0)
                .build();
        boardRepository.save(board2);

        BoardContent boardContent2 = BoardContent.builder()
                .content("content2")
                .board(board2)
                .build();
        boardContentRepository.save(boardContent2);
    }

    @Test
    @DisplayName("내가 쓴 글인지도 테스트")
    void 댓글_대댓글_조회() {
        Comment comment = Comment.builder()
                .boardId(board.getId())
                .email("test2@gmail.com")
                .content("content")
                .depth(false)//false: 댓글
                .build();
        commentRepository.save(comment);

        Comment comment2 = Comment.builder()
                .boardId(board.getId())
                .email("test@gmail.com")
                .content("content2")
                .depth(true)//true: 대댓글
                .groupId(comment.getId()) //댓글 고유id
                .build();
        commentRepository.save(comment2);

        List<BoardContentInfoDto> boardContentInfos = boardContentService.findBoardContentWithComments(board.getId(), "test@gmail.com");

        assertTrue(boardContentInfos.stream().anyMatch(info -> info.getDepth().equals(comment.getDepth())));
        assertTrue(boardContentInfos.stream().anyMatch(info -> info.getDepth().equals(comment2.getDepth())));
        assertTrue(boardContentInfos.get(0).getBoard().getEmail().equals("test@gmail.com"));
    }

    @Test
    @DisplayName("교환이 안 된 다른 사람 게시글 인지")
    void 게시글_상세_조회() {
        BoardContentInfo2Dto boardContentInfo2Dto = boardContentService.findBoardContentWithNoComments(board2.getId(), "test@mgail.com");

        assertTrue(boardContentInfo2Dto.getExchangeType().equals(0));
    }

    @Test
    @DisplayName("다른 사람 게시글이랑 교환 중인지")
    void 게시글_상세_조회2() {
        Exchange exchange = Exchange.builder()
                .boardId(board.getId())
                .email("test@gmail.com")
                .requesterBoardId(board2.getId())
                .requesterEmail("test2@gmail.com")
                .type(-3) //-3은 교환 받음
                .build();
        exchangeRepository.save(exchange);

        Exchange exchange2 = Exchange.builder()
                .boardId(board2.getId())
                .email("test2@gmail.com")
                .requesterBoardId(board.getId())
                .requesterEmail("test@gmail.com")
                .type(-2) //-2는 교환 요청함
                .build();
        exchangeRepository.save(exchange2);
        BoardContentInfo2Dto boardContentInfo = boardContentService.findBoardContentWithNoComments(board2.getId(), "test@gmail.com");
        BoardContentInfo2Dto boardContentInfo2 = boardContentService.findBoardContentWithNoComments(board.getId(), "test2@gmail.com");

        assertTrue(boardContentInfo.getExchangeType().equals(-2)); //교환 받음
        assertTrue(boardContentInfo2.getExchangeType().equals(-3)); //교환 신청함
    }

    @AfterAll
    void deleteEntity() {
        userRepository.deleteById("test@gmail.com");
        userRepository.deleteById("test2@gmail.com");
    }
}