package com.ay.exchange.user.service;

import com.ay.exchange.jwt.JwtTokenProvider;
import com.ay.exchange.user.dto.query.UserIdDto;
import com.ay.exchange.user.dto.query.UserInfoDto;
import com.ay.exchange.user.dto.request.ResetPasswordRequest;
import com.ay.exchange.user.dto.request.SignInRequest;
import com.ay.exchange.user.dto.request.SignUpRequest;
import com.ay.exchange.user.dto.request.VerificationCodeRequest;
import com.ay.exchange.user.dto.response.FindIdResponse;
import com.ay.exchange.user.dto.response.SignInResponse;
import com.ay.exchange.user.dto.response.SignUpResponse;
import com.ay.exchange.user.dto.response.VerificationCodeResponse;
import com.ay.exchange.user.entity.vo.Authority;
import com.ay.exchange.user.entity.User;
import com.ay.exchange.user.exception.*;
import com.ay.exchange.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;

    public SignInResponse signIn(SignInRequest signInRequest) {
        UserInfoDto userInfoDto = userRepository
                .findUserInfoByUserId(signInRequest.getUserId())
                .orElseThrow(() -> {
                    throw new NotExistsUserException();
                });

        if (passwordEncoder.matches(signInRequest.getPassword(),
                userInfoDto.getPassword())) {
            return new SignInResponse(
                    jwtTokenProvider.createToken(signInRequest.getUserId(), userInfoDto.getNickName(), userInfoDto.getAuthority())
                    , userInfoDto.getNickName()
                    , userInfoDto.getAuthority()
                    , userInfoDto.getSuspendedDate()
            );
        }
        throw new NotExistsUserException();
    }

    public SignUpResponse signUp(SignUpRequest signUpRequest) {
        try {
            userRepository.save(
                    User.builder()
                            .userId(signUpRequest.getUserId())
                            .password(passwordEncoder.encode(signUpRequest.getPassword()))
                            .email(signUpRequest.getEmail())
                            .nickName(signUpRequest.getNickName())
                            .authority(Authority.User)
                            .exchangeSuccessCount(0)
                            .desiredData("")
                            .build()
            );
        } catch (Exception e) {
            throw new DuplicateUserIdException();
        }

        return new SignUpResponse(
                jwtTokenProvider.createToken(signUpRequest.getUserId(), signUpRequest.getNickName(), Authority.User)
                , signUpRequest.getNickName()
        );
    }

    public VerificationCodeResponse getVerificationCodeForSignUp(String email) {
        if (checkExistsEmail(email)) {
            throw new DuplicateEmailException();
        }

        String verificationCode = createVerificationCode();
        return new VerificationCodeResponse(
                jwtTokenProvider.createVerificationCodeToken(0, verificationCode, email), verificationCode);
    }

    public VerificationCodeResponse getVerificationCodeForPW(String email) {
        if (checkExistsEmail(email)) {
            String verificationCode = createVerificationCode();
            return new VerificationCodeResponse( //selection 0 1 하드코딩 수정
                    jwtTokenProvider.createVerificationCodeToken(1, verificationCode, email), verificationCode);
        }
        throw new NotExistsUserIdException();

    }

    public Boolean checkExistsUserId(String userId) {
        return userRepository.existsByUserId(userId);
    }

    public Boolean checkExistsNickName(String nickName) {
        return userRepository.existsByNickName(nickName);
    }

    public String findUserIdByEmail(String email) {
        return userRepository
                .findUserIdByEmail(email)
                .orElseThrow(() -> {
                    throw new NotExistsUserIdException();
                }).getUserId();
    }

    public String getTemporaryPassword(
            String number, String verificationCode
    ) {
        if (isVerificationCode(1, number, verificationCode)) {
            String email = jwtTokenProvider.getEmailBySignUpVerificationCode(verificationCode);
            String temporaryPassword = createTemporaryPassword();

            if (updateUserPassword(email, passwordEncoder.encode(temporaryPassword))) return temporaryPassword;
        }
        throw new FailVerificationException();
    }

    public Boolean confirmVerificationCode(int selection, VerificationCodeRequest verificationCodeRequest, String verificationCode) {
        String email = jwtTokenProvider.getEmailBySignUpVerificationCode(verificationCode);
        if(!email.equals(verificationCodeRequest.getEmail())) {
            throw new FailVerificationException();
        }
        if(isVerificationCode(selection, verificationCodeRequest.getNumber(), verificationCode)){
            return true;
        }
        throw new FailVerificationException();
    }

    public Boolean resetPassword(ResetPasswordRequest resetPasswordRequest) {
        return updateUserPassword(resetPasswordRequest.getEmail(), passwordEncoder.encode(resetPasswordRequest.getPassword()));
    }

    public FindIdResponse findUserId(String number, String verificationCode) {
        if (isVerificationCode(1, number, verificationCode)) {
            String email = jwtTokenProvider.getEmailByFindUserVerificationCode(verificationCode);
            UserIdDto userIdDto = userRepository.findUserIdByEmail(email)
                    .orElseThrow(() -> {
                        throw new NotExistsUserIdException();
                    });
            return new FindIdResponse(userIdDto.getUserId(), userIdDto.getProfileImage() == null ? "default.svg" : userIdDto.getProfileImage());
        }
        throw new FailVerificationException();
    }

    private Boolean isVerificationCode(int selection, String number, String verificationCode) {
        return jwtTokenProvider
                .getVerificationCode(selection, verificationCode)
                .equals(number);
    }

    private String createTemporaryPassword() {
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < 9; i++) {
            password.append((char) (Math.random() * 26 + 65));
        }
        return password.toString();
    }

    private Boolean updateUserPassword(
            String email, String password
    ) {
        try {
            userRepository.updatePassword(email, passwordEncoder.encode(password));
        } catch (Exception e) {
            throw new NotExistsUserIdException();
        }
        return true;
    }

    private void sendVerificationCodeByEmail(String email, String verificationCode) {
        SimpleMailMessage message = new SimpleMailMessage();

        //@gs.anyang.ac.kr
        message.setTo(email + "@gs.anyang.ac.kr");
        message.setSubject("AYU Campus 인증번호");
        message.setText(verificationCode);

        javaMailSender.send(message);
    }

    private String createVerificationCode() {
        StringBuilder verificationCode = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            verificationCode.append((int) (Math.random() * 9 + 1));
        }

        //sendVerificationCodeByEmail(email, verificationCode); //배포 시 주석 제거
        return verificationCode.toString();
    }

    private Boolean checkExistsEmail(String email) {
        return userRepository.existsByEmail(email);
    }

}
