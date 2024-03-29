package com.ay.exchange.oauth.facade;


import com.ay.exchange.user.dto.query.UserInfoDto;

import com.ay.exchange.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class Oauth2Facade implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final String ANYANG_DOMAIN = "gs.anyang.ac.kr";
    private final UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        Map<String, Object> attributes = oAuth2User.getAttributes(); // OAuth 서비스의 유저 정보들

        return new DefaultOAuth2User(
                null,
                attributes,
                userNameAttributeName);
    }

    public boolean isAnyangDomain(String domain) {
        if (domain == null) return false;
        return domain.equals(ANYANG_DOMAIN);
    }

    public UserInfoDto findUserByEmail(String email) {
        return userService.findUserInfoByEmail(email);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveUser(String email, String randomNickName) {
        userService.saveUser(email, randomNickName);
    }

}
