package com.ay.exchange.user.facade;

import com.ay.exchange.aws.service.AwsS3Service;
import com.ay.exchange.common.service.RedisService;
import com.ay.exchange.jwt.JwtTokenProvider;
import com.ay.exchange.user.dto.MyPageInfo;
import com.ay.exchange.user.dto.request.UserInfoRequest;
import com.ay.exchange.user.dto.response.LoginNotificationResponse;
import com.ay.exchange.user.dto.response.MyDataResponse;
import com.ay.exchange.user.dto.response.MyPageResponse;
import com.ay.exchange.user.exception.DuplicateNickNameException;
import com.ay.exchange.user.exception.FailUpdateUserInfoException;
import com.ay.exchange.user.exception.NotExistsUserException;
import com.ay.exchange.user.service.MyPageService;
import com.ay.exchange.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

        if (loginNotificationResponse == null) { //존재하지 않는 회원
            throw new NotExistsUserException();
        }

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

    public Boolean checkExistsNickName(String nickName) {
        return userService.existsByNickName(nickName);
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

        boolean isDuplicateNickName = userService.existsByNickName(userInfoRequest.getNickName());
        if (isDuplicateNickName) {
            throw new DuplicateNickNameException();
        }

        boolean isSuccessUpdateUserInfo = myPageService.updateUserInfo(email, userInfoRequest);
        if (!isSuccessUpdateUserInfo) {
            throw new FailUpdateUserInfoException();
        }
    }

    public MyDataResponse getMyData(Integer page, String token) {
        return myPageService.getMyData(page, jwtTokenProvider.getUserEmail(token));
    }
}
