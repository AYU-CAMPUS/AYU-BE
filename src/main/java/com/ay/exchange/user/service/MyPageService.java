package com.ay.exchange.user.service;

import com.ay.exchange.user.dto.FilePathInfo;
import com.ay.exchange.user.dto.MyPageInfo;
import com.ay.exchange.user.dto.request.UserInfoRequest;
import com.ay.exchange.user.dto.response.DownloadableResponse;
import com.ay.exchange.user.dto.response.ExchangeResponse;
import com.ay.exchange.user.dto.response.LoginNotificationResponse;
import com.ay.exchange.user.dto.response.MyDataResponse;
import com.ay.exchange.user.repository.MyPageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    public boolean updateUserInfo(String email, UserInfoRequest userInfoRequest) {
        return myPageRepository.updateUserInfo(email, userInfoRequest);
    }

    public MyDataResponse getMyData(Integer page, String email) {
        PageRequest pageRequest = PageRequest.of(page > 0 ? (page - 1) : 0, 2,
                Sort.by(Sort.Direction.DESC, "id"));

        return myPageRepository.getMyData(pageRequest, email);
    }

    public DownloadableResponse getDownloadable(Integer page, String email) {
        PageRequest pageRequest = PageRequest.of(page > 0 ? (page - 1) : 0, 2,
                Sort.by(Sort.Direction.DESC, "id"));
        return myPageRepository.getDownloadable(pageRequest, email);
    }

    public FilePathInfo getFilePath(Long requesterBoardId, String email) {
        return myPageRepository.getFilePath(requesterBoardId, email);
    }

    public ExchangeResponse getExchanges(Integer page, String email) {
        PageRequest pageRequest = PageRequest.of(page > 0 ? (page - 1) : 0, 2,
                Sort.by(Sort.Direction.DESC, "id"));
        return myPageRepository.getExchanges(pageRequest, email);
    }
}
