package com.ay.exchange.user.repository;

import com.ay.exchange.user.entity.User;
import com.ay.exchange.user.entity.vo.Authority;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MyPageRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @Test
    void deploymentTest() {
        User user = User.builder()
                .email("test@gmail.com")
                .nickName("test")
                .exchangeSuccessCount(0)
                .desiredData("")
                .authority(Authority.User)
                .build();
        userRepository.save(user);
        assertEquals("test", userRepository.findById("test@gmail.com").get().getNickName());
    }
}