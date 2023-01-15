package com.ay.exchange.user.service;

import com.ay.exchange.aws.service.AwsS3Service;
import com.ay.exchange.common.util.DateGenerator;
import com.ay.exchange.jwt.JwtTokenProvider;
import com.ay.exchange.user.dto.FilePathInfo;
import com.ay.exchange.user.dto.MyPageInfo;
import com.ay.exchange.user.dto.request.ExchangeAccept;
import com.ay.exchange.user.dto.request.ExchangeRefusal;
import com.ay.exchange.user.dto.request.UserInfoRequest;
import com.ay.exchange.user.dto.response.*;
import com.ay.exchange.user.entity.vo.Authority;
import com.ay.exchange.user.exception.NotExistsFileException;
import com.ay.exchange.user.exception.NotExistsUserException;
import com.ay.exchange.user.repository.MyPageRepository;
import com.ay.exchange.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final MyPageRepository myPageRepository;
    private final JwtTokenProvider jwtTokenProvider;

    private final AwsS3Service awsS3Service;
    private final RedisTemplate<String, Object> redisTemplate;
    private final int UPDATE_PROFILE = 1;
    private final UserRepository userRepository;
    private final String SEPARATOR = ";";

    @Value("${cookie.domain}")
    private String DOMAIN;

    public Boolean checkExistsNickName(String nickName) {
        return userRepository.existsByNickName(nickName);
    }

    public MyPageResponse getMypage(String token) {
        String email = jwtTokenProvider.getUserEmail(token);
        MyPageInfo myPageInfo = myPageRepository.getMyPage(email);

        System.out.print("교환 요청 자료 번호: ");
        for(Long i:myPageInfo.getExchangeRequests()){
            System.out.print(i+" ");
        }
        System.out.println("");

        return new MyPageResponse(myPageInfo.getNickName(),
                myPageInfo.getProfileImage(),
                myPageInfo.getExchangeSuccessCount(),
                myPageInfo.getMyDataCounts().size(),
                myPageRepository.getDonwloadableCount(email).intValue(),
                myPageInfo.getExchangeRequestsCount(),
                Arrays.stream(myPageInfo.getDesiredData().split(SEPARATOR))
                        .collect(Collectors.toList())
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

    @Transactional(rollbackFor = Exception.class)
    public LoginNotificationResponse getUserNotification(HttpServletResponse response, String token) throws ParseException {
        String email = jwtTokenProvider.getUserEmail(token);
        LoginNotificationResponse loginNotificationResponse = myPageRepository.findUserNotificiatonByEmail(email);

        if (loginNotificationResponse == null) { //존재하지 않는 회원
            throw new NotExistsUserException();
        }

        if (loginNotificationResponse.getSuspendedDate() != null) { //정지회원이라면
            if (isSuspensionExpired(loginNotificationResponse.getSuspendedDate())) { //정지가 만료되었다면 => update
                System.out.println("정지 만료된 회원");
                myPageRepository.updateUserSuspendedDate(email);
                loginNotificationResponse.setSuspendedDate(null);
            } else { //정지가 유지 중 => 쿠키 삭제 redis도
                System.out.println("정지 회원입니다.");
                response.setHeader(HttpHeaders.SET_COOKIE,removeCookie());
                redisTemplate.delete(token);
                redisTemplate.delete(email);
                return loginNotificationResponse;
            }
        }

        return loginNotificationResponse;
    }

    private String removeCookie(){
        ResponseCookie cookie = ResponseCookie.from("token", null)
                .httpOnly(true)
                .domain(DOMAIN)
                .path("/")
                .maxAge(0)
                .secure(true)
                .sameSite("None").build();
        return cookie.toString();
    }

    private boolean isSuspensionExpired(String suspendedDate) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Date date = dateFormat.parse(suspendedDate);
        Date date2 = dateFormat.parse(DateGenerator.getCurrentDate());

        if (date.before(date2)) {
            return true;
        }
        return false;
    }
}