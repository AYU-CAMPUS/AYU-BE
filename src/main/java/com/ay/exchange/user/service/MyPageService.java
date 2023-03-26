package com.ay.exchange.user.service;

import com.ay.exchange.user.dto.MyPageInfo;
import com.ay.exchange.user.dto.request.ExchangeAccept;
import com.ay.exchange.user.dto.request.UserInfoRequest;
import com.ay.exchange.user.dto.response.LoginNotificationResponse;
import com.ay.exchange.user.exception.*;
import com.ay.exchange.user.repository.MyPageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyPageService {
    private final MyPageRepository myPageRepository;

    public LoginNotificationResponse findUserNotificationByEmail(String email) {
        LoginNotificationResponse loginNotificationResponse = myPageRepository.findUserNotificationByEmail(email);
        if (loginNotificationResponse == null) { //존재하지 않는 회원
            throw new NotExistsUserException();
        }
        return loginNotificationResponse;
    }

    public void updateUserSuspendedDate(String email) {
        myPageRepository.updateUserSuspendedDate(email);
    }

    public MyPageInfo getMyPage(String email) {
        return myPageRepository.getMyPage(email);
    }

    public void updateUserInfo(String email, UserInfoRequest userInfoRequest) {
        boolean isSuccessUpdateUserInfo = myPageRepository.updateUserInfo(email, userInfoRequest);
        if (!isSuccessUpdateUserInfo) {
            throw new FailUpdateUserInfoException();
        }
    }

    public void increaseExchangeCompletion(ExchangeAccept exchangeAccept, String email) {
        int successCount = myPageRepository.increaseExchangeCompletion(exchangeAccept, email);
        if (successCount != 4) {
            throw new FailAcceptFileException();
        }
    }

    public String findProfilePath(String email) {
        String profileImagePath = myPageRepository.findProfilePath(email);
        if (profileImagePath == null) {
            throw new NotExistsUserException();
        }
        return profileImagePath;
    }

    public void withdrawalUser(String email) {
        myPageRepository.withdrawalUser(email);
    }

    public void updateProfile(String email, String profilePath) {
        boolean isSuccessUpdatedProfile = myPageRepository.updateProfile(email, profilePath);
        if (!isSuccessUpdatedProfile) {
            throw new FailUpdateProfileException();
        }
    }
}
