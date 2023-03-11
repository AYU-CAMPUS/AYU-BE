package com.ay.exchange.user.service;

import com.ay.exchange.user.dto.query.UserInfoDto;
import com.ay.exchange.user.entity.User;
import com.ay.exchange.user.entity.vo.Authority;
import com.ay.exchange.user.exception.DuplicateNickNameException;
import com.ay.exchange.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void existsByNickName(String nickName) {
        boolean isDuplicateNickName = userRepository.existsByNickName(nickName);
        if (isDuplicateNickName) {
            throw new DuplicateNickNameException();
        }
    }

    public UserInfoDto findUserInfoByEmail(String email) {
        return userRepository.findUserInfoByEmail(email).orElse(null);
    }

    public void saveUser(String email, String randomNickName) {
        userRepository.save(User.builder()
                .email(email)
                .nickName(randomNickName)
                .authority(Authority.User)
                .desiredData("")
                .exchangeSuccessCount(0).build());
    }
}