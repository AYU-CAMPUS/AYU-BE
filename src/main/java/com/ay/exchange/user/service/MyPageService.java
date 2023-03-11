package com.ay.exchange.user.service;

import com.ay.exchange.common.util.DateUtil;
import com.ay.exchange.user.dto.FilePathInfo;
import com.ay.exchange.user.dto.MyPageInfo;
import com.ay.exchange.user.dto.request.ExchangeAccept;
import com.ay.exchange.user.dto.request.UserInfoRequest;
import com.ay.exchange.user.dto.response.DownloadableResponse;
import com.ay.exchange.user.dto.response.ExchangeResponse;
import com.ay.exchange.user.dto.response.LoginNotificationResponse;
import com.ay.exchange.user.dto.response.MyDataResponse;
import com.ay.exchange.user.exception.FailAcceptFileException;
import com.ay.exchange.user.exception.FailUpdateUserInfoException;
import com.ay.exchange.user.exception.NotExistsFileException;
import com.ay.exchange.user.exception.NotExistsUserException;
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
        LoginNotificationResponse loginNotificationResponse =  myPageRepository.findUserNotificationByEmail(email);
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

    public int getDownloadableCount(String email) {
        return myPageRepository.getDownloadableCount(email).intValue();
    }

    public void updateUserInfo(String email, UserInfoRequest userInfoRequest) {
        boolean isSuccessUpdateUserInfo = myPageRepository.updateUserInfo(email, userInfoRequest);
        if (!isSuccessUpdateUserInfo) {
            throw new FailUpdateUserInfoException();
        }
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
        FilePathInfo filePathInfo = myPageRepository.getFilePath(requesterBoardId, email);
        if (filePathInfo == null) {
            throw new NotExistsFileException();
        }
        return filePathInfo;
    }

    public ExchangeResponse getExchanges(Integer page, String email) {
        PageRequest pageRequest = PageRequest.of(page > 0 ? (page - 1) : 0, 2,
                Sort.by(Sort.Direction.DESC, "id"));
        return myPageRepository.getExchanges(pageRequest, email);
    }

    public void deleteExchange(ExchangeAccept exchangeAccept) {
        Long deletedExchangeCount = myPageRepository.deleteExchange(exchangeAccept);
        if (deletedExchangeCount != 2L) { //교환목록을 삭제
            throw new FailAcceptFileException();
        }
    }

    public void acceptExchange(ExchangeAccept exchangeAccept, String email) {
        String currentDate = DateUtil.getCurrentDate();
        int successExchangeCount = myPageRepository.acceptExchange(currentDate, exchangeAccept, email);
        if (successExchangeCount != 2) {
            throw new FailAcceptFileException();
        }
    }

    public void increaseExchangeCompletion(ExchangeAccept exchangeAccept, String email) {
        int successCount = myPageRepository.increaseExchangeCompletion(exchangeAccept, email);
        if (successCount != 4) {
            throw new FailAcceptFileException();
        }
    }
}
