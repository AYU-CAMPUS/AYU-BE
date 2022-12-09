package com.ay.exchange.mypage.service;

import com.ay.exchange.aws.service.AwsS3Service;
import com.ay.exchange.jwt.JwtTokenProvider;
import com.ay.exchange.mypage.dto.*;
import com.ay.exchange.mypage.dto.request.ExchangeAccept;
import com.ay.exchange.mypage.dto.request.ExchangeRefusal;
import com.ay.exchange.mypage.dto.request.UserInfoRequest;
import com.ay.exchange.mypage.dto.response.DownloadableResponse;
import com.ay.exchange.mypage.dto.response.ExchangeResponse;
import com.ay.exchange.mypage.dto.response.MyDataResponse;
import com.ay.exchange.mypage.dto.response.MyPageResponse;
import com.ay.exchange.mypage.exception.NotExistsFileException;
import com.ay.exchange.user.dto.request.PasswordChangeRequest;
import com.ay.exchange.mypage.repository.MyPageRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MyPageService {
    private final MyPageRepository myPageRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final AwsS3Service awsS3Service;
    private final int UPDATE_PROFILE = 1;

    public MyPageResponse getMypage(String token) {
        MyPageInfo myPageInfo = myPageRepository.getMyPage(jwtTokenProvider.getUserId(token));

        return new MyPageResponse(myPageInfo.getNickName(),
                myPageInfo.getProfileImage(),
                myPageInfo.getExchangeSuccessCount(),
                myPageInfo.getMyDataCounts().size(),
                myPageInfo.getDownloadCount(),
                myPageRepository.getExchangeRequestCount(myPageInfo.getMyDataCount()),
                null
        );
    }

    public Boolean updatePassword(PasswordChangeRequest passwordChangeRequest, String token) {
        return myPageRepository.updatePassword(jwtTokenProvider.getUserId(token), passwordEncoder.encode(passwordChangeRequest.getPassword()));
    }

    public MyDataResponse getMyData(Integer page, String token) {
        PageRequest pageRequest = PageRequest.of(page > 0 ? (page - 1) : 0, 2,
                Sort.by(Sort.Direction.DESC, "id"));
        return myPageRepository.getMyData(pageRequest, jwtTokenProvider.getUserId(token));
    }

    public DownloadableResponse getDownloadable(Integer page, String token) {
        PageRequest pageRequest = PageRequest.of(page > 0 ? (page - 1) : 0, 2,
                Sort.by(Sort.Direction.DESC, "id"));
        return myPageRepository.getDownloadable(pageRequest, jwtTokenProvider.getUserId(token));
    }

    public String getFilePath(Long boardId, String token) {
        FilePathInfo filePathInfo = myPageRepository.getFilePath(boardId, jwtTokenProvider.getUserId(token));
        if (filePathInfo == null) {
            throw new NotExistsFileException();
        }
        return filePathInfo.toString();
    }

    public ExchangeResponse getExchanges(Integer page, String token) {
        PageRequest pageRequest = PageRequest.of(page > 0 ? (page - 1) : 0, 2,
                Sort.by(Sort.Direction.DESC, "id"));
        return myPageRepository.getExchanges(pageRequest, jwtTokenProvider.getUserId(token));
    }

    public Boolean acceptExchange(ExchangeAccept exchangeAccept, String token) {
        myPageRepository.acceptExchange(exchangeAccept, jwtTokenProvider.getUserId(token));
        //알림도 생성
        return true;
    }

    public Boolean refuseExchange(ExchangeRefusal exchangeRefusal, String token) {
        myPageRepository.refuseExchange(exchangeRefusal, jwtTokenProvider.getUserId(token));

        //알림도 생성
        return true;
    }


    @Transactional(rollbackFor = Exception.class)
    public Boolean updateProfile(MultipartFile multipartFile, String token) {
        String userId = jwtTokenProvider.getUserId(token);
        String beforeProfilePath = myPageRepository.findProfilePath(userId);
        String profilePath = awsS3Service.buildFileName(multipartFile.getOriginalFilename(), userId, UPDATE_PROFILE);
        myPageRepository.updateProfile(userId, profilePath);

        if (beforeProfilePath != null) {
            awsS3Service.deleteProfile("profile/" + beforeProfilePath); //기본 프로필이 아니라면 이전 프로필 삭제
        }
        awsS3Service.uploadFile(multipartFile, profilePath);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean withdrawalUser(String token) {
        String userId = jwtTokenProvider.getUserId(token);
        String profilePath = myPageRepository.findProfilePath(userId);
        myPageRepository.withdrawalUser(userId);
        awsS3Service.deleteProfile("profile/" + profilePath);
        awsS3Service.deleteUserFiles(userId);
        return true;
    }

    public void updateUserInfo(UserInfoRequest userInfoRequest, String token) {
        String userId = jwtTokenProvider.getUserId(token);
        myPageRepository.updateUserInfo(userId, userInfoRequest);
    }

}