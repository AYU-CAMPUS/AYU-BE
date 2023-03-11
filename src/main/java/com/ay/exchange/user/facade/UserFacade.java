package com.ay.exchange.user.facade;

import com.ay.exchange.aws.service.AwsS3Service;
import com.ay.exchange.common.service.RedisService;
import com.ay.exchange.jwt.JwtTokenProvider;
import com.ay.exchange.user.dto.DownloadFileInfo;
import com.ay.exchange.user.dto.FilePathInfo;
import com.ay.exchange.user.dto.MyPageInfo;
import com.ay.exchange.user.dto.request.ExchangeAccept;
import com.ay.exchange.user.dto.request.ExchangeRefusal;
import com.ay.exchange.user.dto.request.UserInfoRequest;
import com.ay.exchange.user.dto.response.*;
import com.ay.exchange.user.exception.*;
import com.ay.exchange.user.service.MyPageService;
import com.ay.exchange.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.Arrays;
import java.util.stream.Collectors;

import static com.ay.exchange.common.util.DateUtil.isSuspensionPeriodExpired;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserFacade {
    private final UserService userService;
    private final AwsS3Service awsS3Service;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;
    private final MyPageService myPageService;
    private final String SEPARATOR = ";";

    @Transactional(rollbackFor = Exception.class)
    public LoginNotificationResponse getUserNotification(String token) throws ParseException {
        String email = jwtTokenProvider.getUserEmail(token);
        LoginNotificationResponse loginNotificationResponse = myPageService.findUserNotificationByEmail(email);

        if (loginNotificationResponse.getSuspendedDate() != null) { //정지회원이라면
            if (isSuspensionPeriodExpired(loginNotificationResponse.getSuspendedDate())) { //정지가 만료되었다면 => update
                log.info("정지 만료된 회원");
                myPageService.updateUserSuspendedDate(email);
                loginNotificationResponse.setSuspendedDate(null);
            } else { //정지가 유지 중 => 쿠키 삭제 redis도
                log.info("정지 회원입니다.");
                redisService.deleteUserInfo(token, email);
                return loginNotificationResponse;
            }
        }

        return loginNotificationResponse;
    }

    public Boolean logout(String token) {
        redisService.deleteUserInfo(token, jwtTokenProvider.getUserEmail(token));
        return true;
    }

    public boolean checkExistsNickName(String nickName) {
        try { //프론트엔드에서 중복닉네임 체크를 true와 false로 확인하고 있어서 우선 예외처리를 함.
            userService.existsByNickName(nickName);
            return true;
        } catch (DuplicateNickNameException exception) {
            return false;
        }
    }

    public MyPageResponse getMyPage(String token) {
        String email = jwtTokenProvider.getUserEmail(token);
        MyPageInfo myPageInfo = myPageService.getMyPage(email);

        return new MyPageResponse(myPageInfo.getNickName(),
                myPageInfo.getProfileImage(),
                myPageInfo.getExchangeSuccessCount(),
                myPageInfo.getMyDataCounts().size(),
                myPageService.getDownloadableCount(email),
                myPageInfo.getExchangeRequestsCount(),
                Arrays.stream(myPageInfo.getDesiredData().split(SEPARATOR))
                        .collect(Collectors.toList())
        );
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateUserInfo(UserInfoRequest userInfoRequest, String token) {
        String email = jwtTokenProvider.getUserEmail(token);

        userService.existsByNickName(userInfoRequest.getNickName());

        myPageService.updateUserInfo(email, userInfoRequest);
    }

    public MyDataResponse getMyData(Integer page, String token) {
        return myPageService.getMyData(page, jwtTokenProvider.getUserEmail(token));
    }

    public DownloadableResponse getDownloadable(Integer page, String token) {
        return myPageService.getDownloadable(page, jwtTokenProvider.getUserEmail(token));
    }

    public DownloadFileInfo downloadFile(Long requesterBoardId, String token) {
        FilePathInfo filePathInfo = myPageService.getFilePath(requesterBoardId, jwtTokenProvider.getUserEmail(token));

        String filePath = filePathInfo.toString();
        ByteArrayResource resource = awsS3Service.downloadFile(filePath);
        return new DownloadFileInfo(filePath, resource);
    }

    public ExchangeResponse getExchanges(Integer page, String token) {
        return myPageService.getExchanges(page, jwtTokenProvider.getUserEmail(token));
    }

    @Transactional(rollbackFor = Exception.class)
    public void acceptExchange(ExchangeAccept exchangeAccept, String token) {
        String email = jwtTokenProvider.getUserEmail(token);

        myPageService.deleteExchange(exchangeAccept); //교환 요청 목록 삭제

        myPageService.acceptExchange(exchangeAccept, email); //교환 완료

        myPageService.increaseExchangeCompletion(exchangeAccept, email); //도메인에 비즈니스 로직을 하는 것이 좀 더 깔끔할 것 같다. 추후 비즈니스 로직을 도메인으로 변경하자.

        //추후 알림도 생성
        //exchangeRequest.getApplicantId(); 사용자 고유 아이디로 알림을 주자

    }

    @Transactional(rollbackFor = Exception.class)
    public void refuseExchange(ExchangeRefusal exchangeRefusal, String token) {
        myPageService.refuseExchange(exchangeRefusal, jwtTokenProvider.getUserEmail(token));

        //추후 알림도 생성
    }

    @Transactional(rollbackFor = Exception.class)
    public void withdrawalUser(String token) {
        String email = jwtTokenProvider.getUserEmail(token);
        String profilePath = myPageService.findProfilePath(email);
        myPageService.withdrawalUser(email);

        awsS3Service.deleteProfile("profile/" + profilePath);
        awsS3Service.deleteUserFiles(email);
    }
}
