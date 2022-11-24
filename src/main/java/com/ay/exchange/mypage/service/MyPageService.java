package com.ay.exchange.mypage.service;

import com.ay.exchange.jwt.JwtTokenProvider;
import com.ay.exchange.mypage.dto.*;
import com.ay.exchange.mypage.dto.response.DownloadableResponse;
import com.ay.exchange.mypage.dto.response.ExchangeResponse;
import com.ay.exchange.mypage.dto.response.MyDataResponse;
import com.ay.exchange.mypage.dto.response.MyPageResponse;
import com.ay.exchange.mypage.exception.NotExistsFileException;
import com.ay.exchange.user.dto.request.PasswordChangeRequest;
import com.ay.exchange.mypage.repository.UserQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyPageService {
    private final UserQueryRepository userQueryRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

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

    public Boolean updatePassword(PasswordChangeRequest passwordChangeRequest, String token) {
        return userQueryRepository.updatePassword(jwtTokenProvider.getUserId(token), passwordEncoder.encode(passwordChangeRequest.getPassword()));
    }

    public MyDataResponse getMyData(Integer page, String token) {
        PageRequest pageRequest = PageRequest.of(page > 0 ? (page - 1) : 0, 2,
                Sort.by(Sort.Direction.DESC, "id"));
        return userQueryRepository.getMyData(pageRequest, jwtTokenProvider.getUserId(token));
    }

    public DownloadableResponse getDownloadable(Integer page, String token) {
        PageRequest pageRequest = PageRequest.of(page > 0 ? (page - 1) : 0, 2,
                Sort.by(Sort.Direction.DESC, "id"));
        return userQueryRepository.getDownloadable(pageRequest, jwtTokenProvider.getUserId(token));
    }

    public String getFilePath(Long boardId, String token) {
        FilePathInfo filePathInfo = userQueryRepository.getFilePath(boardId, jwtTokenProvider.getUserId(token));
        if (filePathInfo == null) {
            throw new NotExistsFileException();
        }
        return filePathInfo.toString();
    }

    public ExchangeResponse getExchanges(Integer page, String token) {
        PageRequest pageRequest = PageRequest.of(page > 0 ? (page - 1) : 0, 2,
                Sort.by(Sort.Direction.DESC, "id"));
        return userQueryRepository.getExchanges(pageRequest, jwtTokenProvider.getUserId(token));
    }
}