package com.ay.exchange.management.service;

import com.ay.exchange.board.repository.BoardRepository;
import com.ay.exchange.management.dto.request.BoardIdRequest;
import com.ay.exchange.management.exception.FailAcceptRequestBoard;
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

    @AfterAll
    void deleteEntity() {
        userRepository.deleteById("test@gmail.com");
        userRepository.deleteById("test2@gmail.com");
    }
}