package com.ay.exchange.exchange.service;

import com.ay.exchange.board.repository.BoardRepository;
import com.ay.exchange.exchange.dto.request.ExchangeRequest;
import com.ay.exchange.exchange.exception.UnableExchangeException;
import com.ay.exchange.user.entity.User;
import com.ay.exchange.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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
}