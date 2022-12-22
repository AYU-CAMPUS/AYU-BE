package com.ay.exchange.user.service;

import com.ay.exchange.aws.service.AwsS3Service;
import com.ay.exchange.jwt.JwtTokenProvider;
import com.ay.exchange.user.dto.FilePathInfo;
import com.ay.exchange.user.dto.MyPageInfo;
import com.ay.exchange.user.dto.request.ExchangeAccept;
import com.ay.exchange.user.dto.request.ExchangeRefusal;
import com.ay.exchange.user.dto.request.UserInfoRequest;
import com.ay.exchange.user.dto.response.*;
import com.ay.exchange.user.exception.NotExistsFileException;
import com.ay.exchange.user.repository.MyPageRepository;
import com.ay.exchange.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserService {
    private final MyPageRepository myPageRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final AwsS3Service awsS3Service;
    private final int UPDATE_PROFILE = 1;
    private final UserRepository userRepository;

    public Boolean checkExistsNickName(String nickName) {
        return userRepository.existsByNickName(nickName);
    }

    public MyPageResponse getMypage(String token) {
        String email = jwtTokenProvider.getUserEmail(token);
        MyPageInfo myPageInfo = myPageRepository.getMyPage(email);

        return new MyPageResponse(myPageInfo.getNickName(),
                myPageInfo.getProfileImage(),
                myPageInfo.getExchangeSuccessCount(),
                myPageInfo.getMyDataCounts().size(),
                myPageRepository.getDonwloadableCount(email).intValue(),
                myPageInfo.getExchangeRequestsCount(),
                null
        );
    }

//    public Boolean updatePassword(PasswordChangeRequest passwordChangeRequest, String token) {
//        return myPageRepository.updatePassword(jwtTokenProvider.getUserId(token), passwordEncoder.encode(passwordChangeRequest.getPassword()));
//    }

    public MyDataResponse getMyData(Integer page, String token) {
        PageRequest pageRequest = PageRequest.of(page > 0 ? (page - 1) : 0, 2,
                Sort.by(Sort.Direction.DESC, "id"));
        return myPageRepository.getMyData(pageRequest, jwtTokenProvider.getUserEmail(token));
    }

    public DownloadableResponse getDownloadable(Integer page, String token) {
        PageRequest pageRequest = PageRequest.of(page > 0 ? (page - 1) : 0, 2,
                Sort.by(Sort.Direction.DESC, "id"));
        return myPageRepository.getDownloadable(pageRequest, jwtTokenProvider.getUserEmail(token));
    }

    public String getFilePath(Long boardId, String token) {
        FilePathInfo filePathInfo = myPageRepository.getFilePath(boardId, jwtTokenProvider.getUserEmail(token));
        if (filePathInfo == null) {
            throw new NotExistsFileException();
        }
        return filePathInfo.toString();
    }

    public ExchangeResponse getExchanges(Integer page, String token) {
        PageRequest pageRequest = PageRequest.of(page > 0 ? (page - 1) : 0, 2,
                Sort.by(Sort.Direction.DESC, "id"));
        return myPageRepository.getExchanges(pageRequest, jwtTokenProvider.getUserEmail(token));
    }

    public Boolean acceptExchange(ExchangeAccept exchangeAccept, String token) {
        myPageRepository.acceptExchange(exchangeAccept, jwtTokenProvider.getUserEmail(token));
        //알림도 생성
        return true;
    }

    public Boolean refuseExchange(ExchangeRefusal exchangeRefusal, String token) {
        myPageRepository.refuseExchange(exchangeRefusal, jwtTokenProvider.getUserEmail(token));

        //알림도 생성
        return true;
    }


    @Transactional(rollbackFor = Exception.class)
    public Boolean updateProfile(MultipartFile multipartFile, String token) {
        String email = jwtTokenProvider.getUserEmail(token);
        String beforeProfilePath = myPageRepository.findProfilePath(email);
        String profilePath = awsS3Service.buildFileName(multipartFile.getOriginalFilename(), email, UPDATE_PROFILE);
        myPageRepository.updateProfile(email, profilePath);

        if (beforeProfilePath != null) {
            awsS3Service.deleteProfile("profile/" + beforeProfilePath); //기본 프로필이 아니라면 이전 프로필 삭제
        }
        awsS3Service.uploadFile(multipartFile, profilePath);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean withdrawalUser(String token) {
        String email = jwtTokenProvider.getUserEmail(token);
        String profilePath = myPageRepository.findProfilePath(email);
        myPageRepository.withdrawalUser(email);
        awsS3Service.deleteProfile("profile/" + profilePath);
        awsS3Service.deleteUserFiles(email);
        return true;
    }

    public void updateUserInfo(UserInfoRequest userInfoRequest, String token) {
        String email = jwtTokenProvider.getUserEmail(token);
        myPageRepository.updateUserInfo(email, userInfoRequest);
    }

    public LoginNotificationResponse getUserNotification(String token) {
        return myPageRepository.findUserNotificiatonByEmail(jwtTokenProvider.getUserEmail(token));
    }
}