package com.ay.exchange.user.facade;

import com.ay.exchange.aws.service.AwsS3Service;
import com.ay.exchange.common.service.RedisService;
import com.ay.exchange.jwt.JwtTokenProvider;
import com.ay.exchange.user.dto.response.LoginNotificationResponse;
import com.ay.exchange.user.exception.NotExistsUserException;
import com.ay.exchange.user.service.MyPageService;
import com.ay.exchange.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;

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

}
