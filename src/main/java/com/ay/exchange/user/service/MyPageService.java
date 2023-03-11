package com.ay.exchange.user.service;

import com.ay.exchange.user.dto.MyPageInfo;
import com.ay.exchange.user.dto.response.LoginNotificationResponse;
import com.ay.exchange.user.repository.MyPageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyPageService {
    private final MyPageRepository myPageRepository;

    public LoginNotificationResponse findUserNotificationByEmail(String email) {
        return myPageRepository.findUserNotificationByEmail(email);
    }

    public void updateUserSuspendedDate(String email) {
        myPageRepository.updateUserSuspendedDate(email);
    }

    public MyPageInfo getMyPage(String email) {
        return myPageRepository.getMyPage(email);
    }

    public int getDownloadableCount(String email) {
        return myPageRepository.getDownloadableCount(email).intValue();
    }
}
