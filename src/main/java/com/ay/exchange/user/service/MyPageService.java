package com.ay.exchange.user.service;

import com.ay.exchange.jwt.JwtTokenProvider;
import com.ay.exchange.user.dto.query.MyPageInfo;
import com.ay.exchange.user.dto.response.MyPageResponse;
import com.ay.exchange.user.repository.querydsl.UserQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyPageService {
    private final UserQueryRepository userQueryRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public MyPageResponse getMypage(String token) {
        MyPageInfo myPageInfo = userQueryRepository.getMyPage(jwtTokenProvider.getUserId(token));

        return new MyPageResponse(myPageInfo.getNickName(),
                myPageInfo.getProfileImage(),
                myPageInfo.getExchangeSuccessCount(),
                myPageInfo.getMyDataCounts().size(),
                myPageInfo.getDownloadCount(),
                userQueryRepository.getExchangeRequestCount(myPageInfo.getMyDataCount())
        );
    }
}